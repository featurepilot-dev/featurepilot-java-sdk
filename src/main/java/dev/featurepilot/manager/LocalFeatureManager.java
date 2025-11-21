package dev.featurepilot.manager;

import dev.featurepilot.config.FeaturePilotProperties;
import dev.featurepilot.context.FeatureContext;

import java.util.Objects;

import static dev.featurepilot.constants.Common.DEFAULT;

/**
 * Local, configuration-based implementation of {@link FeatureManager}.
 * <p>
 * {@code LocalFeatureManager} resolves feature variants exclusively from the
 * application's configuration (typically {@code application.yml}). This mode is
 * lightweight, fast, and ideal for:
 * <ul>
 *   <li>development and testing environments</li>
 *   <li>CI pipelines</li>
 *   <li>simple feature toggles without remote control</li>
 * </ul>
 *
 * <h2>Example YAML Configuration</h2>
 * <pre>
 * featurepilot:
 *   source:
 *     provider: local
 *   flags:
 *     payment_flow: v2
 *     checkout_ui: experimental
 * </pre>
 *
 * <p>The manager simply retrieves the configured value for the provided feature.
 * If no value is defined or the value is blank, the {@code default} variant is returned.
 *
 * <p>No context-based evaluation is performed in local mode; the
 * {@link FeatureContext} parameter is accepted only for API compatibility.
 */
public record LocalFeatureManager(FeaturePilotProperties properties) implements FeatureManager {

    /**
     * Resolves the flow by reading the flag value from {@link FeaturePilotProperties}.
     *
     * @param feature the feature key
     * @param ctx     unused in local mode; included for interface consistency
     * @return the configured flow, or {@code default} if not configured
     */
    @Override
    public String getFlow(String feature, FeatureContext ctx) {
        String value = properties.getFlags().get(feature);
        return Objects.isNull(value) || value.isBlank() ? DEFAULT : value.trim();
    }
}
