package arush.baatcheet

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import arush.baatcheet.model.Cryptography
import arush.baatcheet.model.DatabaseHandler
import arush.baatcheet.model.FileHandler
import arush.baatcheet.ui.theme.PanchayatTheme
import arush.baatcheet.view.SavedMessagesActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PanchayatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                    val intent = Intent(this, SavedMessagesActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    val context = LocalContext.current
    Text(text = "Hello")
    Button(onClick = {
    },
        modifier = Modifier.fillMaxHeight(0.1f).fillMaxWidth(0.5f)) {
        Text(text = "CLICK", style = TextStyle(fontFamily = FontFamily(Font((R.font.lexend_medium), weight = FontWeight.W500))))
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PanchayatTheme {
        Greeting()
    }
}

