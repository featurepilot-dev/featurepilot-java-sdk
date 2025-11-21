package dev.featurepilot.registry;

import dev.featurepilot.annotations.Flow;
import dev.featurepilot.model.FlowTarget;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Central registry for all {@link Flow}-annotated routing targets in the application.
 * <p>
 * During Spring context initialization, {@code FlowRegistry} scans all beans,
 * unwraps any Spring AOP proxies, and inspects their declared methods for a
 * {@link Flow} annotation. Each discovered flow is mapped into an internal structure
 * and stored as a {@link FlowTarget} containing the bean instance and the
 * corresponding handler method.
 * <p>
 * This registry powers FeaturePilot's flow-routing mechanism. After the
 * {@code FeatureManager} determines the active variant for a feature, the engine uses
 * this registry to look up the correct flow implementation at runtime. Developers do
 * not need to register flows manually—annotating a method with {@code @Flow} is enough.
 * <p>
 * Registry layout:
 * <pre>
 *   featureKey -> {
 *       flowValue -> FlowTarget(beanInstance, method)
 *   }
 * </pre>
 *
 * Typical usage:
 * <pre>
 *   FlowTarget target = flowRegistry.get("payment_flow", "v2");
 *   Object result = target.method().invoke(target.bean(), args);
 * </pre>
 *
 * This component performs all scanning once at startup, ensuring zero runtime overhead.
 * The lookup method is constant-time and thread-safe for concurrent access.
 */
public class FlowRegistry implements ApplicationContextAware {

    private final Map<String, Map<String, FlowTarget>> registry = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        ctx.getBeansOfType(Object.class).values().forEach(bean -> {
            Class<?> targetClass = AopUtils.getTargetClass(bean);

            for (Method m : targetClass.getDeclaredMethods()) {
                Flow ann = m.getAnnotation(Flow.class);
                if (ann != null) {
                    registry
                            .computeIfAbsent(ann.feature(), f -> new HashMap<>())
                            .put(ann.flow(), new FlowTarget(bean, m));
                }
            }
        });
    }

    /**
     * Resolves a {@link FlowTarget} for the given feature and flow name.
     *
     * @param feature the feature key (e.g., "payment_flow")
     * @param flow    the flow/variant value (e.g., "v1", "v2")
     * @return the resolved {@link FlowTarget}, or {@code null} if not registered
     */
    public FlowTarget get(String feature, String flow) {
        return registry.getOrDefault(feature, Map.of()).get(flow);
    }
}
