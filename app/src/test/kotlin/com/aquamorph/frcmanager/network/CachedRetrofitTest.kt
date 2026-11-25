package com.aquamorph.frcmanager.network

import com.sun.net.httpserver.HttpServer
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import retrofit2.Retrofit
import java.net.InetSocketAddress
import java.util.concurrent.atomic.AtomicReference

class CachedRetrofitTest {
    @get:Rule
    val files = TemporaryFolder()

    @Test
    fun `previously loaded event matches survive reopening the cache offline`() {
        val directory = files.newFolder("tba-http")
        val auth = AtomicReference<String>()
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/event/2026test/matches") { exchange ->
            auth.set(exchange.requestHeaders.getFirst("X-TBA-Auth-Key"))
            val body = """[{"key":"2026test_qm1","match_number":1}]""".toByteArray(Charsets.UTF_8)
            exchange.responseHeaders.set("Content-Type", "application/json")
            exchange.responseHeaders.set("Cache-Control", "max-age=0")
            exchange.sendResponseHeaders(200, body.size.toLong())
            exchange.responseBody.use { it.write(body) }
        }
        val url = "http://127.0.0.1:${server.address.port}/"
        val header = "X-TBA-Auth-Key" to "test-key"
        server.start()
        try {
            val online = cachedRetrofit(url, directory, { true }, header)
            try {
                assertEquals("2026test_qm1", online.eventMatches("2026test").body()?.single()?.key)
                assertEquals("test-key", auth.get())
            } finally {
                online.closeCache()
            }
        } finally {
            server.stop(0)
        }

        val offline = cachedRetrofit(url, directory, { false }, header)
        val otherProvider = cachedRetrofit(url, files.newFolder("statbotics-http"), { false })
        try {
            val cached = offline.eventMatches("2026test")
            assertTrue(cached.isSuccessful)
            assertNotNull(cached.raw().cacheResponse)
            assertEquals("2026test_qm1", cached.body()?.single()?.key)
            assertEquals(504, offline.eventMatches("2026uncached").code())
            assertEquals(504, otherProvider.eventMatches("2026test").code())
        } finally {
            offline.closeCache()
            otherProvider.closeCache()
        }
    }

    private fun Retrofit.eventMatches(event: String) = create(TbaApi::class.java).getEventMatches(event).blockingFirst()

    private fun Retrofit.closeCache() {
        (callFactory() as OkHttpClient).cache?.close()
    }
}
