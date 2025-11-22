package dev.featurepilot.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Shared constants used across the FeaturePilot SDK.
 * <p>
 * These values define configuration prefixes, provider identifiers,
 * property aliases, and default endpoint paths used by both local and
 * remote feature managers. This class is not intended to be instantiated.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Common {

    /** Root configuration prefix: {@code featurepilot}. */
    public static final String FEATURE_PILOT = "featurepilot";

    /** Thread name / bean name used for remote polling scheduling. */
    public static final String REMOTE_POLLER = "featurepilot-remote-poller";

    /** Local provider identifier. */
    public static final String LOCAL = "local";

    /** Remote provider identifier. */
    public static final String SERVER = "server";

    /** Default flow value used when no explicit variant is configured. */
    public static final String DEFAULT = "default";

    /** YAML key alias for API keys: {@code api-key}. */
    public static final String API_KEY = "api-key";

    /** YAML key alias for project ID list: {@code project-ids}. */
    public static final String PROJECT_ID = "project-id";

    /** REST endpoint for fetching flags from the FeaturePilot server. */
    public static final String FLAGS_ENDPOINT = "/api/flags";
}
