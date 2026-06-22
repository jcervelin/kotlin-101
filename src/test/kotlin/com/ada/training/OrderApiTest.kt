package com.ada.training

import io.ktor.client.statement.bodyAsText
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

class OrderApiTest {
    @Test
    fun `catalog endpoint returns sample products`() = testApplication {
        application {
            module()
        }

        val response = client.get("/catalog")

        assertEquals(HttpStatusCode.OK, response.status)
        val products = Json.parseToJsonElement(response.bodyAsText()).jsonArray
        assertTrue(products.size >= 3)
    }

    @Test
    fun `order can be created through HTTP`() = testApplication {
        application {
            module()
        }

        val response = client.post("/orders") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "customerId": "customer-1",
                  "customerName": "Ada Lovelace",
                  "customerEmail": "ada@example.com",
                  "items": [
                    {
                      "productId": "kotlin-book",
                      "quantity": 2
                    }
                  ]
                }
                """.trimIndent(),
            )
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val order = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals("Ada Lovelace", order.string("customerName"))
    }
}

private fun JsonObject.string(name: String): String =
    getValue(name).toString().trim('"')
