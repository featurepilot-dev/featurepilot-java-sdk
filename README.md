# FeaturePilot Java SDK

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
  <h3>Feature Flags & Intelligent Flow Routing for Java + Spring Boot</h3>
</div>

---

## ✨ Overview

FeaturePilot is a **lightweight, annotation-based feature flag & flow routing SDK** designed for modern Java and Spring Boot applications.

It helps you:

- Release features safely
- Route traffic between flow variants
- Run experiments / A/B tests
- Evaluate flags using request context
- Switch between **local** or **remote (server)** providers
- Use clean, production-grade AOP with Spring Boot starter

---

## 📦 Requirements

- **Java 21+**
- **Spring Boot 3.5.x**
- Maven or Gradle

---

## 🚀 Quick Start

### 1. Add Dependency

#### **Maven**
```xml
<dependency>
  <groupId>dev.featurepilot</groupId>
  <artifactId>sdk</artifactId>
  <version>0.0.x</version>
</dependency>
```

#### **Gradle**
```gradle
implementation 'dev.featurepilot:sdk:0.0.x'
```

---

## ⚙️ Configuration

### Local Mode (default)

```yaml
featurepilot:
  source:
    provider: local
  features:
    payment: v2
    inline: yes
```

### Remote Server Mode

```yaml
featurepilot:
  source:
    provider: server
    server:
      url: https://your-featurepilot-server.com
      project: my-project
      refresh: 10000
      fallback: true
      auth:
        api-key: ${FEATUREPILOT_API_KEY}
```

---

## 🧩 Usage

### 1. Feature Annotation

```java
@Feature("payment_flow")
public String main(@Context("userId") String userId) {
    return "Main Flow";
}
```

### 2. Flow Variants

```java
@Flow(feature = "payment_flow", value = "v1")
public String v1(String user) { return "Flow v1"; }

@Flow(feature = "payment_flow", value = "v2")
public String v2(String user) { return "Flow v2"; }
```

### 3. Inline Flag Checks

```java
boolean enabled = featureClient.isEnabled("payment", "new", ctx);
```

---

## 🧠 Context Passing

```java
FeatureContext ctx = FeatureContextBuilder.newBuilder()
    .with("userId", 123)
    .with("country", "AZ")
    .build();
```

---

## 🗂 Architecture (SDK Internals)

- **Annotations**: `@Feature`, `@Flow`, `@Context`
- **AOP Interceptor**: Routes annotated methods to correct flow
- **FlowRegistry**: Registers and resolves flow targets
- **FeatureManager**:
    - `SimpleConfigFeatureManager` — reads from YAML
    - `RemoteFeatureManager` — polls FeaturePilot Server
- **FeatureClient**: Inline usage for `isEnabled` / `getVariant`

All components follow clean, Spring Boot starter conventions.

---

## 🌐 FeaturePilot Server

You can run your own FeaturePilot Server using Docker:

```bash
docker pull israf1l/featurepilot-server:latest
```

### Run:

```bash
docker run -d \
  -p 3000:3000 \
  -e DATABASE_URL="postgresql://postgres:password@host:5432/featurepilot?schema=public" \
  -e AUTH_SECRET="$(npx auth secret)" \
  -e ADMIN_USERNAME="admin" \
  -e ADMIN_PASSWORD="pass" \
  --name featurepilot \
  israf1l/featurepilot-server:latest
```

### API endpoint for SDK:

```
GET /api/{project}/features
x-api-key: YOUR_API_KEY
```

---

## 🏷 Configuration Reference

| Property | Description |
|----------|-------------|
| `featurepilot.source.provider` | `local` or `server` |
| `featurepilot.flags.*` | Local static flags |
| `featurepilot.source.server.url` | FeaturePilot Server base URL |
| `featurepilot.source.server.project` | Project name |
| `featurepilot.source.server.refresh` | Poll interval in ms |
| `featurepilot.source.server.fallback` | Use default if server fails |
| `featurepilot.source.server.auth.api-key` | API key |

---

## 🤝 Contributing

FeaturePilot is early-stage and evolving fast. Pull requests and issues are welcome.

---

## 👨‍💻 Author

**Israfil Iskandarov**  
Founder @ FeaturePilot  
🔗 https://www.linkedin.com/in/israfiliskandarov/

---

## 📄 License

MIT License — see `LICENSE`.