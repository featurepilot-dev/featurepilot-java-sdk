package dev.featurepilot.config;

import dev.featurepilot.aspect.FeatureAspect;
import dev.featurepilot.client.FeatureClient;
import dev.featurepilot.context.FeatureContextBuilder;
import dev.featurepilot.manager.FeatureManager;
import dev.featurepilot.manager.LocalFeatureManager;
import dev.featurepilot.manager.RemoteFeatureManager;
import dev.featurepilot.registry.FlowRegistry;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import static dev.featurepilot.constants.Common.LOCAL;
import static dev.featurepilot.constants.Common.SERVER;

/**
 * Auto-configuration entry point for the FeaturePilot SDK.
 * <p>
 * This configuration registers all core infrastructure components required
 * for annotation-based feature evaluation, flow routing, and remote flag
 * resolution. When included on the classpath, FeaturePilot sets up a fully
 * functional feature-flag environment with no additional application code.
 *
 * <h2>Registered Beans</h2>
 * <ul>
 *     <li><strong>{@link FeatureManager}</strong> — resolves active flows either
 *     from local configuration or a remote server based on
 *     {@link FeaturePilotProperties}.</li>
 *
 *     <li><strong>{@link RestClient}</strong> — HTTP client used by
 *     {@link RemoteFeatureManager} for polling or on-demand flag retrieval.</li>
 *
 *     <li><strong>{@link FlowRegistry}</strong> — scans the Spring context at
 *     startup and registers all {@code @Flow}-annotated methods.</li>
 *
 *     <li><strong>{@link FeatureContextBuilder}</strong> — extracts method
 *     parameters annotated with {@code @Context} and constructs a
 *     {@link dev.featurepilot.context.FeatureContext}.</li>
 *
 *     <li><strong>{@link FeatureClient}</strong> — developer-facing API for
 *     flow checks and inline feature evaluations.</li>
 *
 *     <li><strong>{@link FeatureAspect}</strong> — AOP interceptor that routes
 *     {@code @Feature}-annotated methods to the correct flow implementation.</li>
 * </ul>
 *
 * <p>All beans are lazily created and can be overridden by declaring custom beans
 * with the same type or name. The goal is to provide sensible defaults while
 * allowing complete customization when needed.</p>
 */
@Configuration
@EnableConfigurationProperties(FeaturePilotProperties.class)
@RequiredArgsConstructor
public class FeaturePilotAutoConfiguration {

    /**
     * Creates the {@link FeatureManager} using either a local or server-backed
     * implementation based on the configured provider.
     */
    @Bean
    @ConditionalOnMissingBean
    public FeatureManager featureManager(
            FeaturePilotProperties props,
            RestClient restClient
    ) {
        String provider = props.getSource().getProvider();

        return switch (provider) {
            case SERVER -> new RemoteFeatureManager(props, restClient);
            case LOCAL -> new LocalFeatureManager(props);
            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        };
    }

    /**
     * Creates a simple, low-allocation {@link RestClient} backed by Apache
     * HttpClient5. Used by {@link RemoteFeatureManager} to communicate with
     * the FeaturePilot Server.
     */
    @Bean
    @ConditionalOnMissingBean
    public RestClient restClient() {
        HttpClient httpClient = HttpClientBuilder.create().build();

        ClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        return RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    /**
     * Initializes and populates the {@link FlowRegistry} by scanning all beans
     * in the application context for {@code @Flow}-annotated methods.
     */
    @Bean
    public FlowRegistry flowRegistry(ApplicationContext ctx) {
        FlowRegistry registry = new FlowRegistry();
        registry.setApplicationContext(ctx);
        return registry;
    }

    /**
     * Provides the builder responsible for extracting and assembling
     * {@link dev.featurepilot.context.FeatureContext} objects.
     */
    @Bean
    public FeatureContextBuilder featureContextBuilder() {
        return new FeatureContextBuilder();
    }

    /**
     * Developer-facing API for retrieving flows and evaluating inline feature checks.
     */
    @Bean
    public FeatureClient featureClient(FeatureManager mgr) {
        return new FeatureClient(mgr);
    }

    /**
     * AOP router that intercepts {@code @Feature} methods and dispatches them
     * to the correct flow implementation using {@link FlowRegistry}.
     */
    @Bean
    public FeatureAspect featureAspect(
            FlowRegistry flowRegistry,
            FeatureContextBuilder contextBuilder,
            FeatureClient featureClient
    ) {
        return new FeatureAspect(flowRegistry, contextBuilder, featureClient);
    }
}
