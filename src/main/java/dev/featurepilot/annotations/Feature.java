package dev.featurepilot.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the primary entry point of a feature-gated method.
 * <p>
 * Methods annotated with {@code @Feature} represent the logical feature that
 * the caller wants to execute. At runtime, FeaturePilot evaluates the active
 * variant for the given feature using the configured {@code FeatureManager}.
 * If the resolved variant matches the value declared in this annotation,
 * the method proceeds. Otherwise, {@code FeatureAspect} throws a mismatch
 * exception and the corresponding flow must be invoked instead.
 *
 * <p>This annotation is typically placed on the top-level method that starts
 * a feature’s execution journey:
 * <pre>
 * {@code
 * @Feature("payment_flow")
 * public String handlePayment(@Context("userId") String userId) {
 *     ...
 * }
 * }
 * </pre>
 *
 * The {@code value} should match the feature key used in {@link dev.featurepilot.annotations.Flow}
 * so FeaturePilot can properly route between the declared flows.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Feature {

    /**
     * The unique feature key this method belongs to.
     * Example: {@code "payment_flow"}.
     */
    String value();
}
