# Spring Boot With Coroutines and Virtual Threads

**Level:** Intermediate

## Description

To achieve low latency, high throughput, and resource-efficient Spring Boot applications, developers often turn to virtual threads or reactive libraries WebFlux. But both come with trade-offs:

- **Virtual threads** are limited to non-blocking IO, they can't give you parallelism
- **WebFlux** offers full reactivity but adds complexity, reduced readability, and steep learning curves

**Kotlin Coroutines** offer the best of both worlds — non-blocking reactive performance with simple, sequential code that's easy to reason about and maintain.

## What You'll Learn

In this hands-on workshop, you'll learn how to harness Spring Boot's Coroutine support to build clean, performant, and scalable applications. Step by step, you'll develop non-blocking APIs with parallelism and streaming from scratch and learn how to:

- Master Kotlin's Coroutines and its sidekick: Flow
- Use coroutines on top of WebFlux and/or virtual threads
- Perform non-blocking remote API calls with WebClient or RestClient
- Access databases reactively via R2DBC or JDBC
- Apply structured concurrency to run tasks in parallel
- Create advanced streaming APIs using Server-Sent Events with Coroutines and Flow
- Explore how virtual threads and Coroutines complement each other for maximum efficiency

By the end of the workshop, you'll master how to use Coroutines in Spring Boot to achieve reactive performance without the complexity of pure reactive programming.

## Why Should You Attend?

- **Get real performance gains:** Build highly concurrent, non-blocking applications with minimal effort
- **Simplify your reactive code:** Say goodbye to unreadable reactive chains and steep learning curves
- **Master next-gen concurrency:** Fully understand Kotlin coroutines and virtual threads and see how to combine them effectively
- **Apply instantly:** Walk away with a working reactive Spring Boot API powered by Coroutines you can use in your own projects the next day