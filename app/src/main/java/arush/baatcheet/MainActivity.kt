package arush.baatcheet

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
import androidx.compose.ui.tooling.preview.Preview
import arush.baatcheet.model.DatabaseHandler
import arush.baatcheet.ui.theme.PanchayatTheme

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
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    Text(text = "Hello")
    Button(onClick = {DatabaseHandler().sendMessage("i am in firebase","123456789", "+919669620888")},
        modifier = Modifier.fillMaxHeight(0.1f).fillMaxWidth(0.5f)) {
        Text(text = "CLICK")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PanchayatTheme {
        Greeting()
    }
}

