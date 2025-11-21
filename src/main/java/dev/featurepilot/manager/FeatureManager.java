package dev.featurepilot.manager;

import dev.featurepilot.context.FeatureContext;

/**
 * Strategy interface for resolving the active flow/variant of a feature.
 * <p>
 * Implementations of this interface determine which flow should execute for a
 * given feature, based on the evaluation context and the configured provider
 * (local configuration, remote server, rollout rules, targeting logic, etc.).
 *
 * <p>FeaturePilot ships with two default implementations:
 * <ul>
 *   <li>{@code LocalFeatureManager} — evaluates flags from local YAML config.</li>
 *   <li>{@code RemoteFeatureManager} — fetches flags from a FeaturePilot server
 *       and supports polling, caching, and advanced rule evaluation.</li>
 * </ul>
 *
 * <p>This interface is intentionally minimal to ensure that custom implementations
 * can easily be provided for advanced use cases such as A/B testing, machine-learning
 * models, or dynamic experiment assignment.</p>
 */
public interface FeatureManager {

    /**
     * Resolves the active flow/variant for the given feature.
     *
     * @param feature the feature key (e.g., {@code "payment_flow"})
     * @param ctx     evaluation context containing annotated method parameters
     * @return the resolved flow name (e.g., {@code "v1"}, {@code "v2"}, {@code "default"})
     */
    String getFlow(String feature, FeatureContext ctx);
}
