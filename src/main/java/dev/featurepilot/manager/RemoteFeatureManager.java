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
 * <p>
 * {@code RemoteFeatureManager} periodically polls a remote FeaturePilot instance
 * for the latest feature flags and caches them in-memory for fast, zero-allocation
 * lookup at runtime. This enables centralized, dynamic control of feature flows
 * without requiring application restarts.
 *
 * <h2>How it works</h2>
 * <ol>
 *   <li>At startup, a dedicated daemon thread begins polling the configured
 *       FeaturePilot server at a fixed interval.</li>
 *   <li>Responses are stored in an in-memory {@link ConcurrentHashMap}.</li>
 *   <li>Calls to {@link #getFlow(String, FeatureContext)} simply return the
 *       cached variant, ensuring constant-time evaluation.</li>
 *   <li>On network errors, the previous cache is preserved and the application
 *       continues using last known values ("fail-open" behavior).</li>
 * </ol>
 *
 * <h2>Example remote configuration</h2>
 * <pre>
 * featurepilot:
 *   source:
 *     provider: server
 *     server:
 *       url: https://api.featurepilot.dev
 *       refresh: 10000
 *       project-ids: [1,2]
 *       auth:
 *         api-key: abc123
 * </pre>
 *
 * <h2>Threading model</h2>
 * <ul>
 *   <li>A single, daemon thread runs the polling loop.</li>
 *   <li>Polling interval defaults to 10 seconds but is fully configurable.</li>
 *   <li>The scheduler never blocks the application shutdown sequence.</li>
 * </ul>
 *
 * <p>This implementation is safe for concurrent use and imposes no lock contention
 * during flag evaluation.</p>
 */
@Slf4j
@RequiredArgsConstructor
public class RemoteFeatureManager implements FeatureManager {

    private final FeaturePilotProperties props;
    private final RestClient client;

    /** In-memory cache containing the latest resolved flags. */
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    /**
     * Initializes the remote polling scheduler after the bean is constructed.
     * Polls the remote server immediately and then at a fixed interval.
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
     * Returns the cached flow variant for the given feature.
     * <p>
     * Remote evaluation is purely cache-based; this method never blocks or makes
     * a network request.
     *
     * @param feature the feature key
     * @param ctx     evaluation context (not used directly in remote mode)
     * @return the resolved flow or {@code default} if not available
     */
    @Override
    public String getFlow(String feature, FeatureContext ctx) {
        return cache.getOrDefault(feature, DEFAULT);
    }

    /**
     * Polls the FeaturePilot server for the latest flags and updates the cache.
     * <p>
     * Failures are logged and ignored; stale values remain available until the
     * next successful poll.
     */
    private void pollRemoteFlags() {
        String url = props.getSource().getServer().getUrl() + FLAGS_ENDPOINT;

        try {
            Map<String, String> response =
                    client.get().uri(url).retrieve().body(Map.class);

            if (response != null) {
                cache.clear();
                cache.putAll(response);
                log.debug("Remote flags updated: {}", cache);
            }

        } catch (Exception e) {
            if(props.getSource().getServer().isFallback()){
                cache.clear();
            }
            log.warn("Remote flag polling failed: {}. Default flow will be used", e.getMessage());
        }
    }
}
