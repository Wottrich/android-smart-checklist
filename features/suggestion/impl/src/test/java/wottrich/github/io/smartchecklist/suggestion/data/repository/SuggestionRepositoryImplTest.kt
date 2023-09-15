package wottrich.github.io.smartchecklist.suggestion.data.repository

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import wottrich.github.io.smartchecklist.datasource.dao.TagDao
import wottrich.github.io.smartchecklist.datasource.entity.SuggestionEntity
import wottrich.github.io.smartchecklist.datasource.entity.TagEntity
import wottrich.github.io.smartchecklist.datasource.entity.TagSuggestionEmbedded
import wottrich.github.io.smartchecklist.suggestion.domain.repository.SuggestionRepository
import wottrich.github.io.smartchecklist.testtools.BaseUnitTest
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
internal class SuggestionRepositoryImplTest : BaseUnitTest() {

    private lateinit var sut: SuggestionRepository
    private val tagDao: TagDao = mockk()

    override fun setUp() {
        sut = SuggestionRepositoryImpl(tagDao)
    }

    @Test
    fun `GIVEN has no tag registered WHEN returns all tags with suggestion function is called THEN must return an empty list`() =
        runBlockingUnitTest {
            coEvery { tagDao.getAllTagsWithSuggestions() } returns listOf()
            val tags = sut.getAllTagsWithSuggestions()
            assertTrue(tags.isEmpty())
        }

    @Test
    fun `GIVEN has tag registered WHEN returns all tags with suggestion function is called THEN must return an registered tags`() =
        runBlockingUnitTest {
            val tag = TagEntity(name = "first tag")
            coEvery { tagDao.getAllTagsWithSuggestions() } returns listOf(
                TagSuggestionEmbedded(
                    tag,
                    listOf(
                        SuggestionEntity(parentUuid = tag.tagUuid, name = "first suggestion")
                    )
                )
            )
            val tags = sut.getAllTagsWithSuggestions()
            assertFalse(tags.isEmpty())
        }
}