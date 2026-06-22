package com.ada.training.domain

/**
 * A JVM inline value class wraps a single value without the usual runtime cost
 * of allocating a wrapper object in many call sites.
 *
 * Java equivalent: a small final class or record around `String`.
 *
 * The private constructor forces callers through `Email.parse(...)`, similar to
 * a Java static factory method. Kotlin's `companion object` is where that
 * factory function lives.
 */
@JvmInline
value class Email private constructor(val value: String) {
    companion object {
        fun parse(raw: String): Email {
            val normalized = raw.trim().lowercase()
            require("@" in normalized) { "Email must contain @." }
            require(normalized.substringAfter("@").contains(".")) {
                "Email domain must contain a dot."
            }
            return Email(normalized)
        }
    }

    override fun toString(): String = value
}
