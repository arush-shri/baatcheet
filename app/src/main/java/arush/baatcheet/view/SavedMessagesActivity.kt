package arush.baatcheet.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import arush.baatcheet.R
import arush.baatcheet.model.FileHandler
import arush.baatcheet.presenter.SavedMessagePresenter
import arush.baatcheet.view.ui.theme.BaatcheetTheme

class SavedMessagesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BaatcheetTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SavedMessage()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedMessage(){
    val context = LocalContext.current
    val savedMessages = SavedMessagePresenter(context).getSaveMessage()
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.KeyboardArrowLeft , contentDescription = "Go back"
                ,modifier = Modifier
                        .size(40.dp)
                        .padding(top = 3.dp)
                        .clickable { })
                Text(text = "Saved Messages",
                    style = TextStyle(fontFamily = FontFamily(Font((R.font.lexend_medium))), fontSize = 29.sp)
                )
            }
        })
        Column (modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
            LazyColumn(modifier = Modifier.fillMaxHeight()){
                items(savedMessages){it->
                    Column (modifier = Modifier
                        .fillParentMaxHeight(0.2f)
                        .padding(vertical = 14.dp)){
                        MessageCard(username = it.username, message = it.message, timeStamp = it.timestamp)
                    }
                    Divider(color = Color(0xFFA3A3A3), modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .height(1.dp))
                }
            }
        }
    }
}

@Composable
fun MessageCard(username: String, message: Any?, timeStamp: String){
    Card(modifier = Modifier
        .fillMaxWidth(0.9f)
        .fillMaxHeight(),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(Color.White)) {
            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp)
                , horizontalArrangement = Arrangement.SpaceBetween){
                Text(text = username,
                    style = TextStyle(fontFamily = FontFamily(Font((R.font.lexend_regular))), fontSize = 15.sp))
                Text(text = timeStamp,
                    style = TextStyle(fontFamily = FontFamily(Font((R.font.lexend_regular))), fontSize = 15.sp))
            }
        Text(text = message.toString(),
            style = TextStyle(fontFamily = FontFamily(Font((R.font.lexend_regular))), fontSize = 18.sp),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    BaatcheetTheme {
        SavedMessage()
    }
}