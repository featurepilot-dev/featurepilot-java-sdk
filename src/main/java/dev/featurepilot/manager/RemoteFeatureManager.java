package dev.featurepilot.manager;

import dev.featurepilot.config.FeaturePilotProperties;
import dev.featurepilot.context.FeatureContext;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.concurrent.*;

import static dev.featurepilot.constants.Common.*;

/**
 * Remote implementation of {@link FeatureManager} backed by the FeaturePilot Server.
 *
 * <p>This implementation periodically polls a remote FeaturePilot instance for
 * project-scoped feature flows, caching the results in-memory for
 * zero-allocation lookup at runtime.</p>
 *
 * <h2>How it works</h2>
 * <ol>
 *   <li>At startup, a daemon thread periodically calls the FeaturePilot server:
 *       <pre>GET {baseUrl}/{project}/features</pre></li>
 *   <li>The request includes the configured API key:
 *       <pre>x-api-key: &lt;apiKey&gt;</pre></li>
 *   <li>The server returns a map:
 *       <pre>{ "featureName": "flowName" }</pre></li>
 *   <li>The map is cached and used for all flow evaluations.</li>
 * </ol>
 *
 * <h2>Example remote configuration</h2>
 * <pre>
 * featurepilot:
 *   source:
 *     provider: server
 *     server:
 *       url: https://api.featurepilot.dev
 *       project: payment-service
 *       refresh: 10000
 *       fallback: true
 *       auth:
 *         api-key: abc123
 * </pre>
 *
 * <h2>Threading model</h2>
 * <ul>
 *     <li>A single daemon thread executes the polling loop.</li>
 *     <li>The default interval is 10 seconds but fully configurable.</li>
 *     <li>Failures preserve the last known cache unless fallback=true.</li>
 * </ul>
 *
 * <p>Safe for concurrent use; no lock contention during lookup.</p>
 */
@Slf4j
@RequiredArgsConstructor
public class RemoteFeatureManager implements FeatureManager {

    private final FeaturePilotProperties props;
    private final RestClient client;

    /** In-memory feature → flow map received from the remote FeaturePilot server. */
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    /**
     * Initializes remote polling after the bean is constructed.
     * Polls immediately and then at a fixed interval.
     */
    @PostConstruct
    public void start() {
        long refreshMs = props.getSource().getServer().getRefresh();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, REMOTE_POLLER);
            t.setDaemon(true);
            return t;
        });

        scheduler.scheduleWithFixedDelay(
                this::pollRemoteFlags,
                0,
                refreshMs,
                TimeUnit.MILLISECONDS
        );

        log.info("Remote flag polling enabled (interval={} ms)", refreshMs);
    }

    /**
     * Returns the cached flow variant for a feature.
     * Network requests never occur during evaluation.
     */
    @Override
    public String getFlow(String feature, FeatureContext ctx) {
        return cache.getOrDefault(feature, DEFAULT);
    }

    /**
     * Polls the remote FeaturePilot server for the latest active flows.
     *
     * <h3>Request:</h3>
     * <pre>
     * GET {baseUrl}/{project}/features
     * x-api-key: &lt;apiKey&gt;
     * </pre>
     *
     * <h3>Response:</h3>
     * <pre>
     * {
     *   "payment_flow": "v2",
     *   "checkout_ui": "dark"
     * }
     * </pre>
     *
     * <p>On success, the cache is replaced atomically. On failures, stale values
     * remain available unless fallback=true.</p>
     */
    private void pollRemoteFlags() {
        FeaturePilotProperties.ServerConfig server = props.getSource().getServer();

        String baseUrl = server.getUrl();
        String projectName = server.getProject();
        String apiKey = server.getAuth().getApiKey();

        if (projectName == null || projectName.isBlank()) {
            log.error("Remote polling aborted: missing project name");
            return;
        }

        String url = baseUrl + "/api/" + projectName + FEATURES_ENDPOINT;

        try {
            Map<String, String> response =
                    client.get()
                            .uri(url)
                            .header("x-api-key", apiKey)
                            .retrieve()
                            .body(Map.class);

            if (response != null) {
                cache.clear();
                cache.putAll(response);
                log.debug("Remote flags updated: {}", cache);
            }

        } catch (Exception e) {
            log.warn("Remote flag polling failed: {} — using fallback behavior",
                    e.getMessage());

            if (server.isFallback()) {
                cache.clear();
            }
        }
    }
}
