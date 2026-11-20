package wottrich.github.io.smartchecklist.share.api

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Golden JSON fixtures copied from `docs/api/share-contract.md`. Any field drift
 * breaks the wire contract between the embedded host server and its clients.
 */
class ShareModelsSerializerTest {

    /** Mirrors the client config used by the sync feature (`KtorClientFactory`). */
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GIVEN a health response WHEN it is decoded THEN status and role are read`() {
        val decoded = json.decodeFromString<HealthResponse>(
            """{"status":"ok","role":"device-host"}"""
        )

        assertEquals("ok", decoded.status)
        assertEquals("device-host", decoded.role)
    }

    @Test
    fun `GIVEN a contract offers response WHEN it is decoded THEN each offer is read`() {
        val decoded = json.decodeFromString<ShareOffersResponse>(
            """{"offers":[""" +
                """{"shareId":"s-1","name":"Groceries","ownerDeviceName":"Pixel 8",""" +
                """"updatedAt":1700000000000},""" +
                """{"shareId":"s-2","name":"Packing","ownerDeviceName":"Galaxy S23",""" +
                """"updatedAt":1700000001000}]}"""
        )

        assertEquals(2, decoded.offers.size)
        assertEquals("s-1", decoded.offers[0].shareId)
        assertEquals("Groceries", decoded.offers[0].name)
        assertEquals("Pixel 8", decoded.offers[0].ownerDeviceName)
        assertEquals(1700000000000L, decoded.offers[0].updatedAt)
    }

    @Test
    fun `GIVEN an empty offers response WHEN it is decoded THEN offers default to empty`() {
        val decoded = json.decodeFromString<ShareOffersResponse>("""{"offers":[]}""")

        assertEquals(emptyList<ShareOfferDto>(), decoded.offers)
    }

    @Test
    fun `GIVEN a contract accept response WHEN it is decoded THEN shareId and cursor are read`() {
        val decoded = json.decodeFromString<ShareAcceptResponse>(
            """{"shareId":"s-1","cursor":42}"""
        )

        assertEquals("s-1", decoded.shareId)
        assertEquals(42L, decoded.cursor)
    }

    @Test
    fun `GIVEN a share offer WHEN it round-trips THEN the JSON matches the contract shape`() {
        val dto = ShareOfferDto(
            shareId = "s-1",
            name = "Groceries",
            ownerDeviceName = "Pixel 8",
            updatedAt = 1700000000000L,
        )

        val encoded = json.encodeToString(dto)

        assertEquals(
            """{"shareId":"s-1","name":"Groceries","ownerDeviceName":"Pixel 8",""" +
                """"updatedAt":1700000000000}""",
            encoded
        )
    }
}
