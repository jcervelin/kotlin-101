# Consuming Spring WebFlux SSE With Ktor

Some Spring Boot services expose reactive endpoints using `Flux<T>`. When the
endpoint produces `text/event-stream`, the HTTP response usually looks like
this:

```text
data: {"productId":"kotlin-book","availableQuantity":10,"status":"IN_STOCK"}
data: {"productId":"ktor-guide","availableQuantity":0,"status":"OUT_OF_STOCK"}
data: {"productId":"coroutines-course","availableQuantity":3,"status":"LOW_STOCK"}
```

This format is called Server-Sent Events, or SSE. Each `data:` block is one
event pushed by the server over a long-lived HTTP connection.

## The Important Difference

A normal JSON endpoint returns one complete response:

```text
GET /orders/123
-> one JSON body
-> connection can close
```

An SSE endpoint returns a stream:

```text
GET /inventory/stream
-> event 1
-> event 2
-> event 3
-> connection stays open
```

Because the response may never finish, do not consume it with:

```kotlin
client.get(url).body<Something>()
```

That style is for ordinary request-response calls. For SSE, consume each event
as it arrives.

## Required Dependencies

This project uses Ktor `3.5.0` through the published Ktor version catalog in
`settings.gradle.kts`.

The client example needs:

```kotlin
dependencies {
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
}
```

`ktor-client-core` contains the SSE client API. `ktor-client-cio` provides a JVM
HTTP engine for the standalone demo. `ktor-serialization-kotlinx-json` is used
to decode each event payload into a Kotlin data class.

## Example Payload

The demo package models one event as:

```kotlin
@Serializable
data class InventoryUpdateEvent(
    val productId: String,
    val availableQuantity: Int,
    val status: String,
)
```

If the Spring service sends a larger JSON document, create a data class that
matches the fields you care about. With `ignoreUnknownKeys = true`, the Ktor
consumer can safely ignore extra fields sent by the upstream service.

## Ktor Client Setup

The reusable client lives in:

```text
src/main/kotlin/com/ada/training/integration/sse/SpringFluxSseClient.kt
```

It installs Ktor's `SSE` plugin:

```kotlin
val client = HttpClient(CIO) {
    install(SSE) {
        maxReconnectionAttempts = 3
        reconnectionTime = 2.seconds
    }
}
```

The reconnection settings are useful for a long-running stream. If the
connection drops, Ktor can retry before giving up.

## Consuming Events

The core of the client is:

```kotlin
client.sse(urlString = url) {
    incoming.collect { event ->
        val payload = event.data ?: return@collect
        val update = json.decodeFromString<InventoryUpdateEvent>(payload)
        onUpdate(update)
    }
}
```

Read this from inside out:

1. `client.sse(...)` opens the SSE connection.
2. `incoming` is a Kotlin `Flow` of server-sent events.
3. `collect` handles one event at a time.
4. `event.data` contains the JSON after the `data:` prefix.
5. `Json.decodeFromString` turns the JSON payload into a Kotlin object.

## Running The Demo

Start the Spring Boot service that exposes the stream, then run:

```powershell
.\gradlew.bat runSseClientDemo
```

By default, the demo calls:

```text
http://localhost:8081/inventory/stream
```

You can also run the generated class directly with a different URL from your
IDE by passing the endpoint as the first program argument.

## Practical Guidance

Keep the SSE consumer small and explicit. Decode the payload, validate the
minimum fields your service needs, then hand the typed event to application
code.

For large payloads, avoid collecting events into a list unless the stream is
known to be short. Process each event as it arrives, persist or publish the
result if needed, and let the collector continue.

Use SSE when the communication is one-way from server to client. If your Ktor
service also needs to send messages back over the same connection, use
WebSockets instead.
