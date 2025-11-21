# FeaturePilot Java SDK
Feature flags & intelligent flow routing for Java & Spring Boot.

FeaturePilot is a lightweight, production-ready SDK that enables you to:
- Toggle features safely
- Run A/B or percentage rollouts
- Route logic using **flows**
- Evaluate rules using **context data**
- Use **local configuration** or **remote server mode**

Supports:
- Java 17+ (optimized for Java 21)
- Spring Boot 3.2+
- Minimal config (drop-in starter)

---

## 🚀 Quick Start

### 1. Add dependency
Once published to Maven Central:

```xml
<dependency>
  <groupId>dev.featurepilot</groupId>
  <artifactId>sdk</artifactId>
  <version>0.0.1</version>
</dependency>
```

Or Gradle:

```gradle
implementation("dev.featurepilot:sdk:0.0.1")
```

---

## ⚙️ Configuration

### **application.yml**

```yaml
featurepilot:
  mode: local # or: server
  source:
    local:
      features:
        new_checkout_flow: true
        pricing_experiment: false
```

### Remote mode

```yaml
featurepilot:
  mode: server
  source:
    server:
      url: https://api.featurepilot.dev
      projects: [101, 102]
      refresh-interval: 15
```

---

## 🏷 Using @Feature and @Flow

### Toggle a method

```java
@Feature("new_checkout_flow")
public CheckoutResponse handle() {
    return service.runNewFlow();
}
```

### Routing logic with flows

```java
@Flow(feature = "pricing_experiment", value = "variant_b")
public Price calculatePrice(FeatureContext ctx) {
    return priceV2(ctx);
}
```

---

## 🧠 Context Evaluation

```java
public Price calculate(@Context FeatureContext ctx) {
    String country = ctx.get("country");
}
```

Context is automatically injected when using `@Context` annotation.

---

## 🏗 Architecture

FeaturePilot Java SDK includes:

| Module | Description |
|--------|-------------|
| **FeatureManager** | Local/Remote evaluation engine |
| **FlowRegistry** | Binds `@Flow` handlers |
| **FeatureAspect** | AOP interceptor for `@Feature` & `@Flow` |
| **FeatureContextBuilder** | Creates context objects |

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
