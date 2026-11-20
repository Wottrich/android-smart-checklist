package wottrich.github.io.smartchecklist.datasource.migration

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import wottrich.github.io.smartchecklist.datasource.version.DatabaseVersions

/**
 * Schema wiring of the Wi-Fi sharing migration (Phase B): VI→VII adds the share
 * routing (`shared_checklist_meta` + `sync_outbox.host_id`). SQL shape is validated
 * by Room's exported schema at compile time; this pins the version chain.
 */
class DatabaseMigrationTest {

    @Test
    fun `GIVEN the current database WHEN versions are checked THEN the app ships at VII`() {
        assertEquals(DatabaseVersions.VII, DatabaseVersions.currentVersion)
        assertEquals(7, DatabaseVersions.currentVersion)
    }

    @Test
    fun `GIVEN the migration chain WHEN it is checked THEN VI migrates to VII`() {
        val migration = migrations.single { it.startVersion == DatabaseVersions.VI }
        assertEquals(DatabaseVersions.VII, migration.endVersion)
    }

    @Test
    fun `GIVEN the migration chain WHEN it is checked THEN no version gaps exist`() {
        val endpoints = migrations.map { it.startVersion to it.endVersion }
        assertEquals(1 to 2, endpoints.first())
        assertEquals(DatabaseVersions.currentVersion, endpoints.last().second)
        endpoints.zipWithNext { previous, next ->
            assertTrue("gap between ${previous.second} and ${next.first}", previous.second == next.first)
        }
    }
}
