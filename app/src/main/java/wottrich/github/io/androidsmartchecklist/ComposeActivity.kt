package wottrich.github.io.androidsmartchecklist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.example.androidsmartchecklist.R
import wottrich.github.io.components.RowComponent
import wottrich.github.io.components.SubtitleRow
import wottrich.github.io.components.TitleRow

class ComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val modifier = Modifier.clickable {  }
                RowComponent(
                    modifier = modifier,
                    leftContent = {
                        TitleRow(text = "Title")
                    },
                    rightIconContent = {
                        val deleteModifier = Modifier.clip(CircleShape).clickable { }
                        val checkModifier = Modifier.clip(CircleShape).clickable { }
                        Icon(
                            modifier = deleteModifier,
                            painter = painterResource(id = R.drawable.ic_completed), contentDescription = "Home"
                        )

                        Icon(
                            modifier = checkModifier,
                            imageVector = Icons.Default.Check, contentDescription = "Home"
                        )
                    }
                )
            }
        }
    }

}