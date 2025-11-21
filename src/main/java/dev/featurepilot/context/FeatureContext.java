package dev.featurepilot.context;

import java.util.Map;

/**
 * Simple container for contextual values used during feature evaluation.
 * <p>
 * A {@code FeatureContext} holds arbitrary key–value pairs extracted from
 * method parameters annotated with {@link dev.featurepilot.annotations.Context}.
 * These values can be consumed by the {@link dev.featurepilot.manager.FeatureManager}
 * to support user targeting, rollout strategies, or remote rule evaluation.
 *
 * <h2>Example</h2>
 * <pre>
 * {@code
 * @Feature("payment_flow")
 * public String process(@Context("userId") String userId,
 *                       @Context("locale") String locale) {
 *     ...
 * }
 * }
 * </pre>
 *
 * Produced context:
 * <pre>
 * {
 *   "userId": 123,
 *   "locale": "AZ"
 * }
 * </pre>
 *
 * <p>The context object is immutable in structure but the underlying map
 * can contain mutable values. It is lightweight and safe to pass through
 * the evaluation pipeline.</p>
 */
public record FeatureContext(Map<String, Object> data) { }
