package dev.featurepilot.context;

import dev.featurepilot.annotations.Context;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Parameter;
import java.util.HashMap;

/**
 * Builder responsible for constructing {@link FeatureContext} instances from
 * method parameters annotated with {@link Context}.
 * <p>
 * During {@code @Feature}-annotated method interception, FeaturePilot uses this
 * builder to extract contextual values from method arguments and expose them to
 * the feature evaluation engine. Each parameter annotated with {@code @Context}
 * contributes a key–value entry in the resulting {@link FeatureContext}.
 *
 * <h2>Example</h2>
 * <pre>
 * {@code
 * @Feature("payment_flow")
 * public String process(@Context("userId") String userId,
 *                       @Context("country") String country) {
 *     ...
 * }
 * }
 * </pre>
 *
 * <p>FeaturePilot will automatically build a context such as:</p>
 * <pre>
 * {
 *   "userId": "...",
 *   "country": "..."
 * }
 * </pre>
 *
 * <p>This makes contextual targeting (user attributes, request properties, etc.)
 * available to rollout strategies or remote rule evaluation. The builder does not
 * perform conversion or validation — it only collects annotated parameter values.</p>
 */
@Component
public class FeatureContextBuilder {

    /**
     * Builds a {@link FeatureContext} by scanning method parameters for
     * {@link Context} annotations and inserting the corresponding argument values.
     *
     * @param pjp the intercepted join point representing a {@code @Feature} method call
     * @return a populated {@link FeatureContext} containing all annotated arguments
     */
    public FeatureContext build(ProceedingJoinPoint pjp) {
        FeatureContext ctx = new FeatureContext(new HashMap<>());

        MethodSignature sig = (MethodSignature) pjp.getSignature();
        Parameter[] params = sig.getMethod().getParameters();
        Object[] args = pjp.getArgs();

        for (int i = 0; i < params.length; i++) {
            Context ann = params[i].getAnnotation(Context.class);
            if (ann != null) {
                ctx.data().put(ann.value(), args[i]);
            }
        }

        return ctx;
    }
}
