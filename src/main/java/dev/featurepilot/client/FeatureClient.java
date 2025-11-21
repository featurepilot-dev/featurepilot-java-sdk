package dev.featurepilot.client;

import dev.featurepilot.context.FeatureContext;
import dev.featurepilot.manager.FeatureManager;

import java.util.HashMap;

/**
 * High-level client API for interacting with the FeaturePilot engine.
 * <p>
 * {@code FeatureClient} provides a simple, developer-friendly interface for:
 * <ul>
 *     <li>Resolving the active flow/variant for a feature</li>
 *     <li>Checking whether a specific flow is currently enabled</li>
 *     <li>Performing lightweight inline feature checks</li>
 * </ul>
 *
 * <p>Typically injected into application services, this client delegates all
 * evaluation logic to a configured {@link FeatureManager}, which may be backed
 * by local configuration or a remote server.
 *
 * <p>Example usage:
 * <pre>
 * {@code
 * FeatureContext ctx = FeatureContextBuilder
 *         .newBuilder()
 *         .with("userId", 42)
 *         .build();
 *
 * if (featureClient.isEnabled("new_checkout", "v2", ctx)) {
 *     // run experimental flow
 * }
 * }
 * </pre>
 *
 * {@code FeatureClient} is lightweight, stateless, and safe for concurrent reuse.
 */
public record FeatureClient(FeatureManager featureManager) {

    /**
     * Resolves the active flow/variant for the given feature.
     *
     * @param feature the feature key, e.g. {@code "payment_flow"}
     * @param ctx     context used during evaluation (may include user, request, etc.)
     * @return the flow/variant name returned by the {@link FeatureManager}
     */
    public String getFlow(String feature, FeatureContext ctx) {
        return featureManager.getFlow(feature, ctx);
    }

    /**
     * Checks whether a specific flow is currently active for this feature.
     * This is the most typical check for inline feature usage.
     *
     * @param feature the feature key
     * @param flow    the flow name to compare against the resolved variant
     * @param ctx     evaluation context
     * @return {@code true} if the resolved flow matches the provided one
     */
    public boolean isEnabled(String feature, String flow, FeatureContext ctx) {
        return flow.equals(featureManager.getFlow(feature, ctx));
    }

    /**
     * Convenience overload for cases where no context is required.
     * A new, empty {@link FeatureContext} is created internally.
     *
     * @param feature the feature key
     * @param flow    the flow name to check
     * @return {@code true} if active variant equals the requested flow
     */
    public boolean isEnabled(String feature, String flow) {
        return flow.equals(featureManager.getFlow(feature, new FeatureContext(new HashMap<>())));
    }
}
