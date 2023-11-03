package arush.baatcheet.view

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import arush.baatcheet.R
import arush.baatcheet.model.SaveMessageModel
import arush.baatcheet.presenter.HomeScreenPresenter
import arush.baatcheet.view.ui.theme.BaatcheetTheme
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contact = intent.getStringExtra("contactName")
        val number = intent.getStringExtra("contactNumber")
        val imageLink = intent.getStringExtra("imageLink")
        setContent {
            BaatcheetTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    if (contact != null) {
                        if (number != null) {
                            ChatScreen(
                                goBack = { finish() },
                                username = contact,
                                number = number,
                                userDP = imageLink,
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class, DelicateCoroutinesApi::class)
@Composable
fun ChatScreen(
    goBack: () -> Unit,
    username: String,
    number: String,
    userDP: String?
) {
    val context = LocalContext.current
    val presenter = HomeScreenPresenter.getInstance(context)
    val myNumber = presenter.getMyNum()
    var messageList by remember { mutableStateOf(presenter.retrieveMessage(number)) }

    LaunchedEffect(true) {
        presenter.getPublicKey(number)
    }
    LaunchedEffect(presenter){
        presenter.receiveMessage(number).collect{
            if(it){
                messageList = presenter.retrieveMessage(number)
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(username, fontFamily = FontFamily(Font((R.font.lexend_regular)))) },
                navigationIcon = {
                    IconButton(onClick = { goBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    ) {
                        if (userDP != null) {
                            Image(
                                painter = rememberImagePainter(userDP),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.no_dp_logo),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding()
            ) {
                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopCenter),
                    horizontalAlignment = Alignment.CenterHorizontally){
                    items(messageList){
                        it["timestamp"]?.let { it1 ->
                            it["message"]?.let { it2 ->
                                MessageBubble(
                                    message = it2,
                                    timestamp = it1,
                                    userNumber = number,
                                    isCurrentUserMessage = number==myNumber,
                                    context = context,
                                    saveMsg = {msg, time->
                                        presenter.saveMessage(number, msg, time)
                                    }
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .align(Alignment.BottomCenter),
                ) {
                    ChatInputField(){
                        GlobalScope.launch {
                            presenter.sendMessage(number, it)
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(message: String, timestamp: String, userNumber: String, isCurrentUserMessage: Boolean, context: Context,
                  saveMsg: (Any?, String) -> Unit) {
    val date = getCombinedTimestamp()
    val bubbleColor = if (isCurrentUserMessage) {
        MaterialTheme.colorScheme.onPrimary // User's sent message color
    } else {
        MaterialTheme.colorScheme.onSecondary // Other person's received message color
    }

    if(date.toInt() > timestamp.substring(0..7).toInt()){
        dateStamp(date)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(shape = RoundedCornerShape(16.dp))
            .background(bubbleColor)
            .combinedClickable(
                onClick = { },
                onLongClick = {
                    saveMsg(message, timestamp)
                    Toast
                        .makeText(context, "Message Saved", Toast.LENGTH_SHORT)
                        .show()
                },
            ),
    ) {
        Column {
            Text(
                text = userNumber, // Need to import from DB
                style = TextStyle(
                    color = Color.Unspecified,
                    fontSize = 6.sp,
                    fontFamily = FontFamily(Font((R.font.lexend_regular)))
                ),
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.Start)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Row(
                modifier = Modifier.padding(8.dp),
            ) {
                Text(
                    text = message,
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font((R.font.lexend_regular)))
                    ),
                    modifier = Modifier
                        .padding(4.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.width(2.dp))

                Text(
                    text = formatTimestamp(timestamp),
                    color = Color.Gray,
                    fontSize = 6.sp,
                    fontFamily = FontFamily(Font((R.font.lexend_regular))),
                    modifier = Modifier
                        .padding(2.dp)
                        .align(Alignment.Bottom)
                )
            }
        }
    }
}

@Composable
fun dateStamp(date: String){
    val date = formatDate(date)
    Box(modifier = Modifier
        .clip(shape = RoundedCornerShape(8.dp))
        .padding(vertical = 6.dp)){
        Text(
            text = date,
            fontSize = 10.sp,
            fontFamily = FontFamily(Font((R.font.lexend_regular))),
        )
    }
}

@Composable
fun ChatInputField(onSend: (String) -> Unit) {
    var message by remember { mutableStateOf("") }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.075f)
                .padding(bottom = 4.dp, start = 4.dp, end = 4.dp),
        ) {
            Box(modifier = Modifier
                .padding(end = 2.dp)
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .fillMaxWidth(0.82f)
                .clip(RoundedCornerShape(14.dp, 0.dp, 0.dp, 14.dp))
                .background(MaterialTheme.colorScheme.primary)){

                BasicTextField(
                    value = message,
                    onValueChange = {
                        message = it
                    },
                    textStyle = TextStyle(fontSize = 18.sp,fontFamily = FontFamily(Font((R.font.lexend_regular))),
                        color = MaterialTheme.colorScheme.secondary),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Send
                    ),
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 6.dp)
                )
            }
            Button(
                onClick = {
                    onSend(message)
                    message = "" },
                enabled = message.isNotEmpty(),
                shape = RoundedCornerShape(0.dp, 14.dp,14.dp,0.dp),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .fillMaxWidth(0.18f)) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Send",
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }
        }
}

private fun formatTimestamp(timestamp: String): String {
    val date = Date(timestamp.toLong())
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return dateFormat.format(date)
}

private fun formatDate(inputDate: String): String {
    val inputFormat = DateTimeFormatter.ofPattern("ddMMyyyy")
    val outputFormat = DateTimeFormatter.ofPattern("dd MMM yyyy")

    val date = LocalDate.parse(inputDate, inputFormat)
    return date.format(outputFormat)
}

private fun getCombinedTimestamp(): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("ddMMyyyy")
    return currentDateTime.format(formatter)
}
