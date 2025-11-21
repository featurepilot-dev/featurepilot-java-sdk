package dev.featurepilot.config;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.featurepilot.constants.Common.*;

/**
 * Configuration properties for the FeaturePilot SDK.
 * <p>
 * These properties provide the runtime configuration for both local and remote
 * feature-flag providers. They are automatically bound from the application's
 * {@code application.yml} or {@code application.properties} files using the
 * prefix {@code featurepilot}.
 *
 * <h2>Typical YAML Example</h2>
 * <pre>
 * featurepilot:
 *   source:
 *     provider: local
 *   flags:
 *     payment_flow: v2
 *     checkout_ui: experimental
 * </pre>
 *
 * <h2>Remote Mode Example</h2>
 * <pre>
 * featurepilot:
 *   source:
 *     provider: server
 *     server:
 *       url: https://api.featurepilot.dev
 *       refresh: 10000
 *       project-ids: [1, 2]
 *       auth:
 *         api-key: abc123
 * </pre>
 *
 * <p>These properties drive the behavior of {@link dev.featurepilot.manager.FeatureManager},
 * selecting either {@code LocalFeatureManager} or {@code RemoteFeatureManager} depending
 * on the configured provider.</p>
 */
@Data
@ConfigurationProperties(prefix = FEATURE_PILOT)
public class FeaturePilotProperties {

    /**
     * Defines where feature flags should be loaded from (local or remote).
     */
    private SourceProperties source;

    /**
     * Local feature flag map used when {@code provider=local}.
     * <p>
     * Example:
     * <pre>
     * flags:
     *   payment_flow: v1
     *   inline_flag: enabled
     * </pre>
     */
    private Map<String, String> flags = new HashMap<>();

    /**
     * Nested configuration describing the feature provider.
     */
    @Data
    public static class SourceProperties {

        /**
         * Provider type: {@code local} or {@code server}.
         * Defaults to {@code local}.
         */
        private String provider = LOCAL;

        /**
         * Remote server configuration (only used when provider=server).
         */
        private ServerConfig server;
    }

    /**
     * Configuration for the FeaturePilot remote server.
     * Used only when {@code provider=server}.
     */
    @Data
    public static class ServerConfig {

        /**
         * Base URL of the FeaturePilot server.
         * Example: {@code https://api.featurepilot.dev}
         */
        private String url;

        /**
         * Authentication credentials for remote flag access.
         */
        private Auth auth;

        /**
         * Project identifiers used by remote multi-project environments.
         * Accepts YAML key {@code project-ids}.
         */
        @JsonAlias(PROJECT_IDS)
        private List<Integer> project;

        /**
         * Polling interval (in milliseconds) for refresh operations.
         * Default: 10 seconds.
         */
        private long refresh = 10_000;

        /**
         * Fallback to default flows if remote server is not available.
         * Default: false.
         */
        private boolean fallback = false;
    }

    /**
     * API authentication for remote flag retrieval.
     */
    @Data
    public static class Auth {

        /**
         * API key for remote access.
         * Binds from YAML key {@code api-key}.
         */
        @JsonAlias(API_KEY)
        private String apiKey;
    }
}
