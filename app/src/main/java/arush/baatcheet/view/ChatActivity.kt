package arush.baatcheet.view

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextOverflow
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
import java.time.LocalTime
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
    var messageList by remember { mutableStateOf(presenter.retrieveMessage(number)) }
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        presenter.getPublicKey(number)
    }
    LaunchedEffect(presenter){
        presenter.receiveMessage(number).collect{
            if(it){
                messageList = presenter.retrieveMessage(number)
                presenter.removeMessages(number)
            }
        }
    }
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            lazyListState.scrollToItem(messageList.size - 1)
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
                    .fillMaxSize().padding(top = 10.dp)
            ) {
                LazyColumn(modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.91f)
                    .align(Alignment.TopCenter),
                    state = lazyListState,
                    horizontalAlignment = Alignment.CenterHorizontally,){
                    items(messageList){
                        it["timestamp"]?.let { it1 ->
                            it["message"]?.let { it2 ->
                                MessageBubble(
                                    message = it2,
                                    timestamp = it1,
                                    userName = username,
                                    isCurrentUserMessage = number==presenter.myNum,
                                    context = context,
                                    saveMsg = {msg, time->
                                        presenter.saveMessage(username, msg, time)
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
                            messageList = presenter.retrieveMessage(number)
                            coroutineScope.launch {
                                lazyListState.scrollToItem(messageList.size - 1)
                            }
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(message: String, timestamp: String, userName: String, isCurrentUserMessage: Boolean, context: Context,
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
            .padding(vertical = 5.dp)
            .clip(shape = RoundedCornerShape(12.dp))
            .background(bubbleColor)
            .combinedClickable(
                onClick = { Toast
                    .makeText(context, "Long press to save message", Toast.LENGTH_SHORT)
                    .show() },
                onLongClick = {
                    saveMsg(message, timestamp)
                    Toast
                        .makeText(context, "Message Saved", Toast.LENGTH_SHORT)
                        .show()
                },
            ),
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Text(
                text = if(isCurrentUserMessage){
                          "Me"
                } else{
                    userName
                },
                style = TextStyle(
                    color = Color.Unspecified,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font((R.font.lexend_regular)))
                ),
                modifier = Modifier
                    .padding(start = 6.dp)
                    .align(Alignment.Start)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Box(
                modifier = Modifier.padding(bottom = 4.dp, start = 8.dp, end = 8.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = message,
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font((R.font.lexend_regular)))
                    ),
                    modifier = Modifier,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.width(2.dp))

                Text(
                    text = formatTimestamp(timestamp.substring(8 until timestamp.length)),
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font((R.font.lexend_regular))),
                    modifier = Modifier
                        .padding(2.dp)
                        .align(Alignment.BottomEnd)
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
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
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
    val inputFormat = DateTimeFormatter.ofPattern("HHmmss")
    val outputFormat = DateTimeFormatter.ofPattern("hh:mm a")

    val time = LocalTime.parse(timestamp, inputFormat)
    return time.format(outputFormat)
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
