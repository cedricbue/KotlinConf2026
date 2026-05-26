# KotlinConf2026

## Workshop
### Spring Boot With Coroutines and Virtual Threads

* [Summary](workshop/COURSE_SUMMARY.md)
* [Slides](workshop/Reactive%20Spring%20Boot%20With%20Coroutines%20and%20Virtual%20Threads%20-%20KotlinConf%202026.pdf)
* [Sources](workshop/kotlin-training-labs.zip)

*Decision Tree: When to use which Spring Boot Stack*

```mermaid
flowchart TD
    Q1["Need rich ORM / JPA or\nmostly blocking I/O?\n─────────────────────\n- Complex relational domain model\n- JPA / Hibernate\n- Blocking clients or SDKs\n- Filesystem access / legacy libs"]
    Q2["Small relational domain or need\nreactive database semantics?\n─────────────────────\n- Simple relational model\n- Streaming query results\n- DB access as part of a Flow pipeline"]
    Q3["Need HTTP streaming / SSE /\nWebSockets or long-lived responses?"]
    Q4["Need foremost structured parallelism?\n─────────────────────\n- Fan-out / fan-in\n- Parallel remote calls\n- Concurrent enrichment, no Flow"]

    R1["MVC + Virtual Threads + JPA\n─────────────────────\n✔ JDBC / JPA\n✔ Thread-bound transactions\n✔ Blocking DB — blocking is cheap\n⚠ Optional coroutine bridge for parallel calls\n✖ No JPA inside async branches"]
    R2["WebFlux + Coroutines + R2DBC\n─────────────────────\n✔ Flow for DB streaming\n✔ Reactive transactions\n✔ No raw Mono/Flux in app code"]
    R3["WebFlux + Coroutines + Flow"]
    R4A["MVC + Virtual Threads + Coroutines\n(blocking-first stack)"]
    R4B["WebFlux + Coroutines\n(non-blocking / reactive stack)"]

    Q1 -->|YES| R1
    Q1 -->|NO| Q2
    Q2 -->|YES| R2
    Q2 -->|NO| Q3
    Q3 -->|YES| R3
    Q3 -->|NO| Q4
    Q4 -->|YES - blocking-first| R4A
    Q4 -->|YES - reactive| R4B

    style R1  fill:#0F6E56,color:#E1F5EE,stroke:#085041
    style R2  fill:#3C3489,color:#EEEDFE,stroke:#26215C
    style R3  fill:#3C3489,color:#EEEDFE,stroke:#26215C
    style R4A fill:#0F6E56,color:#E1F5EE,stroke:#085041
    style R4B fill:#3C3489,color:#EEEDFE,stroke:#26215C
```

## Sessions

### Opening Keynote

[![Youtube Keynote](https://img.youtube.com/vi/MmwBJbzWbV0/0.jpg)](https://www.youtube.com/watch?v=MmwBJbzWbV0)

### Bootiful Kotlin
Speaker: _Josh Long_ - Spring Developer Advocate

Spring Boot marries Spring's flexibility with conventional, common sense defaults to make application development on the JVM not just fly, but pleasant!

The framework is as clean as it gets, wouldn't it be nice if the language with which you wielded it matched its elegance?

Kotlin, the productivity-focused language from our friends at JetBrains, takes up the productivity slack to make the experience leaner, cleaner and even more pleasant.

The Spring and Kotlin teams have worked hard to make sure that Kotlin and Spring Boot are a first-class experience for all developers trying to get to production, faster and safer.

Come for the Spring and stay for the Bootiful Kotlin.

### Concurrency Patterns for Modern High Performance Kotlin Servers
Speaker: _Bowen Feng_ - Software Engineer @Google

Kotlin coroutines give us powerful tools for asynchronous server programming, but using them well requires more than knowing launch, async, or Flow in isolation. High-performance servers are built from recurring concurrency patterns: small, composable ways to structure work, stream results, isolate slow dependencies, and control producer-consumer boundaries.

In this session, we will explore several useful concurrency patterns by building a simplified AI assistant server live. We will break complex server behavior into distinct concurrency challenges and show how Kotlin’s async primitives provide elegant, structured answers: generator-style flows for more responsive event streams, fan-in for concurrent execution, sequence numbers for handling out-of-order responses, coarse and fine-grained timeouts for reliability, request hedging to improve tail latency, and backpressure and buffering for slow clients.

The demo is intentionally small, but it mirrors real server-side problems: many independent async operations, variable dependency latency, partial responses, cancellation, and streaming output. By the end, you will have a practical mental model for composing coroutines, structured concurrency, channels, and Flow into clear, reusable concurrency building blocks for Kotlin servers.
