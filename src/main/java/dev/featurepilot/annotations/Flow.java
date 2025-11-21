package dev.featurepilot.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a flow implementation for a specific feature.
 * <p>
 * A flow represents a concrete variant of a feature’s execution path. Developers
 * annotate multiple methods with the same {@code feature} but different {@code flow}
 * values to define alternative behaviors. The FeaturePilot engine determines the
 * active flow at runtime using the configured {@code FeatureManager}, and the
 * {@code FlowRegistry} maps these annotations to invocable handlers.
 *
 * <p>Example:
 * <pre>
 * {@code
 * @Flow(feature = "payment_flow", flow = "v1")
 * public String processV1(User user) { ... }
 *
 * @Flow(feature = "payment_flow", flow = "v2")
 * public String processV2(User user) { ... }
 * }
 * </pre>
 *
 * The combination of {@code feature} + {@code flow} must uniquely identify a flow
 * within the application. Annotation scanning occurs automatically at startup; no
 * registration or configuration is required from the user.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Flow {

    /**
     * The feature key that groups related flow variants.
     * Example: {@code "payment_flow"}.
     */
    String feature();

    /**
     * The specific flow/variant value for this method.
     * Example: {@code "v1"}, {@code "v2"}.
     */
    String flow();
}
