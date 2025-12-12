# FeaturePilot Java SDK

<p align="center">
  <img src="https://img.shields.io/maven-central/v/dev.featurepilot/sdk?color=%2306ab78&label=FeaturePilot%20SDK" alt="Maven Central"/>
  <img src="https://img.shields.io/badge/status-alpha-yellow" alt="aplha"/>
</p>

<div align="center">
  <img src="logo.svg" alt="FeaturePilot Logo" width="200"/>
  <h1>Feature Pilot Java SDK</h1>
  <h3>Feature Flags & Intelligent Flow Routing for Java + Spring Boot</h3>
</div>

---

## ✨ Overview

FeaturePilot is a **lightweight, annotation-based feature flag & flow routing SDK** designed for modern Java and Spring Boot applications.

It helps you:

- Release features safely
- Route traffic between multiple flows
- Evaluate flows using request context
- Switch between **local** or **remote (server)** providers
- Use clean, production-grade AOP with Spring Boot starter

---

## 🗺️ Roadmap

### ✅ What we have now
- MVP (annotation-based feature flags + flow routing)
- Local + Remote provider foundation

### 🚧 Next
- % rollout (gradual releases)
- Complex context evaluation (advanced targeting)

### 🏢 Later
- Enterprise-ready offering (admin UI, multi-project support, advanced strategies)

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
implementation 'dev.featurepilot:sdk:0.0.4'
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
This is an entry point of the feature you want to control
```java
@Feature("payment_flow")
public String main(@Context("userId") String userId) {
    return "Main Flow";
}
```

### 2. Flow Variants

```java
@Flow(feature = "payment_flow", flow = "v1")
public String v1(String user) { return "Flow v1"; }

@Flow(feature = "payment_flow", flow = "v2")
public String v2(String user) { return "Flow v2"; }
```

### 3. Inline Flag Checks

```java
boolean enabled = featureClient.isEnabled("payment", "new", ctx);
```

---

## 🌐 Feature Pilot Server

You can run your own Feature Pilot Server using Docker:

```bash
docker pull israf1l/featurepilot-server:latest
```

### Run:

```bash
docker run -d \
  -p 3000:3000 \
  -e DATABASE_URL="postgresql://username:password@host:port/db?schema=name" \
  -e AUTH_SECRET="(your secret)" \
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

| Property                                  | Description                  |
|-------------------------------------------|------------------------------|
| `featurepilot.source.provider`            | `local` or `server`          |
| `featurepilot.features.*`                 | Local features               |
| `featurepilot.source.server.url`          | FeaturePilot Server base URL |
| `featurepilot.source.server.project`      | Project name                 |
| `featurepilot.source.server.refresh`      | Poll interval in ms          |
| `featurepilot.source.server.fallback`     | Use default if server fails  |
| `featurepilot.source.server.auth.api-key` | API key                      |

---

## 🤝 Contributing

Feature Pilot is early-stage and evolving fast. Expect bugs and major changes. Pull requests and issues are welcome.

---

## 👨‍💻 Author

**Israfil Iskandarov**  
Founder @ FeaturePilot  
🔗 https://www.linkedin.com/in/israfiliskandarov/

---

## 📄 License

MIT License — see `LICENSE`.