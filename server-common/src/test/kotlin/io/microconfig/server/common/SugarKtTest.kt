package io.microconfig.server.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

internal class SugarKtTest {
    @Test
    fun should_deser_uuid() {
        val json = """{"id": "3d470e1c-28d6-421e-85f7-5c5220360fc2"}""".json()
        val uuid = json.uuid("id")

        assertEquals(UUID.fromString("3d470e1c-28d6-421e-85f7-5c5220360fc2"), uuid)
    }
}

