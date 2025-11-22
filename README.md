<p align="center">
  <img src="https://img.shields.io/maven-central/v/dev.featurepilot/sdk?color=%2306ab78&label=FeaturePilot%20SDK" alt="Maven Central"/>
  <img src="https://img.shields.io/github/license/featurepilot-dev/featurepilot-java-sdk" alt="License"/>
  <img src="https://img.shields.io/badge/Java-21+-green" alt="Java 21+"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.x-blue" alt="Spring Boot 3.5+"/>
  <img src="https://img.shields.io/badge/status-beta-yellow" alt="Beta"/>
</p>

<div align="center">
  <img src="logo.svg" alt="FeaturePilot Logo" width="200"/>
  <h1>FeaturePilot Java SDK</h1>
  <h3>Feature Flags & Intelligent Flow Routing for Java & Spring Boot</h3>
</div>

## ✨ Features

- **Feature Flags**: Safely toggle features in production
- **A/B Testing**: Run experiments with percentage-based rollouts
- **Flow Routing**: Route users to different code paths
- **Context-Aware**: Make decisions based on user context
- **Dual Modes**: Local configuration or remote server mode
- **Spring Boot Auto-Configuration**: Zero-config setup for Spring applications
- **Type-Safe**: Strongly-typed configuration and APIs

## 📦 Requirements

- Java 17 or higher (optimized for Java 21)
- Spring Boot 3.2+
- Maven or Gradle

## 🚀 Quick Start

### 1. Add Dependency

#### Maven
```xml
<dependency>
  <groupId>dev.featurepilot</groupId>
  <artifactId>sdk</artifactId>
  <version>0.0.1</version>
</dependency>
```

#### Gradle
```gradle
implementation 'dev.featurepilot:sdk:0.0.1'
```

### 2. Configure Feature Flags

#### Local Mode (Development)
```yaml
featurepilot:
  source:
    provider: local
  flags:
    new_checkout_flow: true
    pricing_experiment: "variant_b"
```

#### Server Mode (Production)
```yaml
featurepilot:
  source:
    provider: server
    server:
      url: https://api.featurepilot.dev
      project-ids: [101, 102]
      refresh: 15000  # Refresh interval in milliseconds
      auth:
        api-key: ${FEATURE_PILOT_API_KEY}
```

## 🏷 Usage

### Feature Flags

```java
@Service
public class CheckoutService {
    
    @Feature("new_checkout_flow")
    public CheckoutResponse processCheckout(Order order) {
        // This method will only execute if 'new_checkout_flow' is enabled
        return newCheckoutFlow(order);
    }
    
    @Feature(value = "new_checkout_flow", fallback = "legacyCheckout")
    public CheckoutResponse processCheckoutWithFallback(Order order) {
        return newCheckoutFlow(order);
    }
    
    public CheckoutResponse legacyCheckout(Order order) {
        // Fallback implementation
    }
}
```

### Flow Routing

```java
@Service
public class PricingService {
    
    @Flow(feature = "pricing_experiment", value = "v2")
    public Price calculatePrice(Order order, @Context FeatureContext ctx) {
        // This version will be used when 'pricing_experiment' is set to 'v2'
        return calculateV2Price(order, ctx);
    }
    
    @Flow(feature = "pricing_experiment", value = "v1")
    public Price calculatePrice(Order order, @Context FeatureContext ctx) {
        // Fallback implementation
        return calculateV1Price(order, ctx);
    }
}
```

### Context-Aware Evaluation

```java
@RestController
public class PricingController {
    
    @Autowired
    private FeatureManager featureManager;
    
    @GetMapping("/api/price")
    public Price getPrice(@RequestParam String productId, 
                         @RequestHeader("User-Agent") String userAgent) {
        
        // Create context with request data
        FeatureContext context = new FeatureContext()
            .with("productId", productId)
            .with("userAgent", userAgent);
            
        // Check feature with context
        if (featureManager.isEnabled("premium_features", context)) {
            return calculatePremiumPrice(productId);
        }
        
        return calculateStandardPrice(productId);
    }
}
```

## 🔧 Configuration Reference

### Common Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `featurepilot.source.provider` | String | `local` | Feature provider: `local` or `server` |
| `featurepilot.flags` | Map | `{}` | Feature flag definitions (local mode) |

### Server Mode Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `featurepilot.source.server.url` | String | Required | FeaturePilot server URL |
| `featurepilot.source.server.project-ids` | List<Integer> | Required | Project IDs to fetch flags for |
| `featurepilot.source.server.refresh` | long | `30000` | Refresh interval in milliseconds |
| `featurepilot.source.server.auth.api-key` | String | Required | API key for authentication |

## 📚 Advanced Usage

### Custom Feature Provider

Implement the `FeatureProvider` interface to create a custom feature provider:

```java
@Bean
public FeatureProvider customFeatureProvider() {
    return new CustomFeatureProvider();
}
```

### Programmatic Access

```java
@Autowired
private FeatureManager featureManager;

public void processOrder(Order order) {
    FeatureContext context = new FeatureContext()
        .with("userId", order.getUserId())
        .with("orderTotal", order.getTotal());
        
    if (featureManager.isEnabled("new_checkout_flow", context)) {
        // New flow
    } else {
        // Old flow
    }
}
```

## 🤝 Contributing

Contributions are welcome! Please read our [Contributing Guide](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🐳 Docker UI
A minimal UI for visualizing flags is available as a Docker image:

```
docker pull featurepilot/server:latest
```

---

## 📦 Publishing
We use the **Vanniktech Maven Publish Plugin** with **in-memory PGP signing** to publish artifacts to Maven Central.

Full guide: `docs/PUBLISHING.md` (coming soon)

---

## 👨‍💻 Author
**Israfil Iskandarov**  
Founder of FeaturePilot  
https://www.linkedin.com/in/israfiliskandarov/

---

## 📄 License
MIT License — see `LICENSE` for details.
