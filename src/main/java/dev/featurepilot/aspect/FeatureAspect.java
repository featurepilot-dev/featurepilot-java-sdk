package dev.featurepilot.aspect;

import dev.featurepilot.annotations.Feature;
import dev.featurepilot.client.FeatureClient;
import dev.featurepilot.context.FeatureContext;
import dev.featurepilot.context.FeatureContextBuilder;
import dev.featurepilot.model.FlowTarget;
import dev.featurepilot.registry.FlowRegistry;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Objects;

/**
 * Aspect responsible for routing feature-gated method calls to the correct flow
 * implementation based on the active feature variant.
 * <p>
 * When a method annotated with {@link Feature} is invoked, this aspect:
 * <ol>
 *     <li>Builds a {@link FeatureContext} from method arguments using
 *         {@link FeatureContextBuilder}.</li>
 *     <li>Asks {@link FeatureClient} to determine the active flow/variant for
 *         the feature.</li>
 *     <li>Looks up the corresponding {@link FlowTarget} in {@link FlowRegistry}.</li>
 *     <li>If a matching flow exists, invokes its handler method instead of the
 *         original method.</li>
 *     <li>If no flow is registered for the resolved variant, the original
 *         method is executed normally.</li>
 * </ol>
 *
 * <p>This mechanism enables clean, annotation-driven feature routing without
 * conditionals, switch-statements, or manual wiring. Developers simply declare:
 *
 * <pre>
 * {@code
 * @Feature("payment_flow")
 * public String handle(@Context("userId") String userId) { ... }
 *
 * @Flow(feature = "payment_flow", flow = "v1")
 * public String v1(String userId) { ... }
 *
 * @Flow(feature = "payment_flow", flow = "v2")
 * public String v2(String userId) { ... }
 * }
 * </pre>
 *
 * <p>The aspect ensures that only the appropriate flow method executes,
 * determined dynamically by the active feature configuration.
 */
@Aspect
@RequiredArgsConstructor
public class FeatureAspect {

    private final FlowRegistry flowRegistry;
    private final FeatureContextBuilder contextBuilder;
    private final FeatureClient featureClient;

    @Around("@annotation(feature)")
    public Object around(ProceedingJoinPoint pjp, Feature feature) throws Throwable {
        String featureName = feature.value();
        FeatureContext ctx = contextBuilder.build(pjp);
        String flow = featureClient.getFlow(featureName, ctx);
        FlowTarget target = flowRegistry.get(featureName, flow);

        if (Objects.nonNull(target)) {
            return target.method().invoke(target.bean(), pjp.getArgs());
        }

        return pjp.proceed();
    }
}
