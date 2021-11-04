package wottrich.github.io.baseui.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed class Dimens(val baseNumber: Int) {
    object BaseFour : Dimens(baseNumber = 4), TenSizeNumbers {
        override val SizeOne = getDp(1)
        override val SizeTwo = getDp(2)
        override val SizeThree = getDp(3)
        override val SizeFour = getDp(4)
        override val SizeFive = getDp(5)
        override val SizeSix = getDp(6)
        override val SizeSeven = getDp(7)
        override val SizeEight = getDp(8)
        override val SizeNine = getDp(9)
        override val SizeTen = getDp(10)
    }

    fun getDp(size: Int): Dp {
        return (baseNumber * size).dp
    }
}

interface TenSizeNumbers {
    val SizeOne: Dp
    val SizeTwo: Dp
    val SizeThree: Dp
    val SizeFour: Dp
    val SizeFive: Dp
    val SizeSix: Dp
    val SizeSeven: Dp
    val SizeEight: Dp
    val SizeNine: Dp
    val SizeTen: Dp
}