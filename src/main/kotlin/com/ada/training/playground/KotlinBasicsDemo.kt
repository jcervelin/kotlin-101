package com.ada.training.playground

import com.ada.training.domain.Email

fun main() {
    println("Kotlin basics demo")
    println("------------------")
    println("Type an email and press Enter:")

    val rawEmail = readlnOrNull().orEmpty()
    val parsedEmail = runCatching { Email.parse(rawEmail) }

    parsedEmail
        .onSuccess { email ->
            val displayName = rawEmail.substringBefore("@").ifBlank { "anonymous" }
            println("Normalized email: $email")
            println("Display name: ${displayName.titleCase()}")
        }
        .onFailure { error ->
            println("Invalid email: ${error.message}")
        }

    val nullableName: String? = readlnOrNull()
    val length = nullableName?.length ?: 0
    println("Safe-call example: the next input length was $length.")
}

private fun String.titleCase(): String =
    trim()
        .lowercase()
        .replaceFirstChar { it.titlecase() }
