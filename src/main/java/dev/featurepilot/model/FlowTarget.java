package dev.featurepilot.model;

import java.lang.reflect.Method;

/**
 * Represents a resolved flow handler consisting of a Spring bean instance
 * and the method that should be invoked for the selected feature variant.
 * <p>
 * {@code FlowTarget} entries are discovered and registered by
 * {@link dev.featurepilot.registry.FlowRegistry} during application startup.
 * Each target corresponds to a method annotated with
 * {@link dev.featurepilot.annotations.Flow}.
 *
 * <h2>Usage</h2>
 * FeaturePilot uses this record to dynamically dispatch flow execution:
 * <pre>
 * FlowTarget target = flowRegistry.get("payment_flow", "v2");
 * Object result = target.method().invoke(target.bean(), args);
 * </pre>
 *
 * <p>This record is immutable, thread-safe, and designed for fast lookup
 * during runtime routing.</p>
 */
public record FlowTarget(Object bean, Method method) {}
