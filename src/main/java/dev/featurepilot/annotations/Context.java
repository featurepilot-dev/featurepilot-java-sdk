package dev.featurepilot.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds a method parameter to the {@link dev.featurepilot.context.FeatureContext}
 * used during feature evaluation and flow resolution.
 * <p>
 * Parameters annotated with {@code @Context} are automatically extracted by
 * {@code FeatureAspect} and injected into the feature evaluation process.
 * The {@code value} represents the key under which the parameter is stored
 * inside the {@code FeatureContext} map.
 *
 * <p>Example:
 * <pre>
 * {@code
 * @Feature("payment_flow")
 * public String handle(@Context("userId") String userId) {
 *     ...
 * }
 * }
 * </pre>
 *
 * This allows feature managers and strategies (rollouts, user targeting, etc.)
 * to access request-level or domain-specific data without requiring any explicit
 * wiring by the developer.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Context {

    /**
     * The key under which this parameter will be stored inside the
     * {@code FeatureContext}.
     * Example: {@code "userId"}.
     */
    String value();
}
