package wottrich.github.io.tools.utils

import android.content.ClipData
import android.content.ClipboardManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 27/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 */

class ClipboardImplTest {

    private lateinit var sut: ClipboardImpl

    private lateinit var clipboardManager: ClipboardManager

    @Before
    fun setUp() {
        clipboardManager = mockk(relaxed = true)
        sut = ClipboardImpl(clipboardManager)
    }

    @Test
    fun `WHEN copy is requested THEN copy function must be called`() {
        val expectedClipData = ClipData(
            "any",
            arrayOf(""),
            ClipData.Item("any")
        )
        every { clipboardManager.setPrimaryClip(any()) } returns Unit

        sut.copy(expectedClipData)

        verify(exactly = 1) { clipboardManager.setPrimaryClip(any()) }
    }

}