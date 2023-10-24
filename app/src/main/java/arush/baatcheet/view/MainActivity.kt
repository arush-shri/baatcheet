package arush.baatcheet.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import arush.baatcheet.view.ui.theme.BaatcheetTheme
import arush.baatcheet.R
import arush.baatcheet.model.FileHandler
import arush.baatcheet.presenter.HomeScreenPresenter
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import kotlinx.coroutines.flow.collect

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BaatcheetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var isSearchBarActive by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val homeScreenPresenter = remember { HomeScreenPresenter(context) }

    Box {
        Column {
            if (isSearchBarActive) {
                SearchBar(
                    onSearchQueryChange = { query ->
                    },
                    onSearchBarClose = {
                        isSearchBarActive = false
                    }
                )
            } else {
                AppBar( homeScreenPresenter,
                    onSearchIconClick = {
                        isSearchBarActive = true
                    }
                )
            }
            Divider(
                color = Gray,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            ChatList(homeScreenPresenter)
        }

        FloatingActionButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun AppBar(homeScreenPresenter: HomeScreenPresenter, onSearchIconClick: () -> Unit) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = stringResource(id = R.string.Baatcheet_title),
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSystemInDarkTheme()) Gray else Black
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .clickable {
                    onSearchIconClick()
                }
        )
        Spacer(modifier = Modifier.width(16.dp))
        Image(
            painter = rememberImagePainter(data = homeScreenPresenter.getMyDp()),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .size(42.dp)
                .fillMaxSize()
                .clip(CircleShape)
                .clickable {
                    val intent = Intent(context, ProfileActivity::class.java)
                    intent.putExtra("profile_name", "John Doe")
                    intent.putExtra("phone_number", "+1234567890")
                    context.startActivity(intent)
                }
        )
    }
}

@Composable
fun ChatList(homeScreenPresenter: HomeScreenPresenter) {
    var chatsData by remember { mutableStateOf<List<String>?>(null) }
    var homeData by remember { mutableStateOf<Map<String, Map<String,ArrayList<HashMap<String, String>>>>?>(null) }
    LaunchedEffect(homeScreenPresenter) {
        homeScreenPresenter.getMessageList().collect{
            homeData = it
            chatsData = homeData!!.keys.toList()
        }
//        list aane k baad indi file me store ka fun chala de
    }

    if(homeData == null){
        Column (modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){
            CircularProgressIndicator()
        }
    }
    else{
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            items(chatsData!!) { chat ->
                homeData!![chat]?.get("messages")?.let { ChatListItem(chat, it)
                }
                Divider(
                    color = Gray,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ChatListItem(contact: String, messages: ArrayList<HashMap<String, String>>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle chat item click here */ }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.no_dp_logo),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = contact,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            messages.last()["message"]?.let {
                Text(
                    text = it,
                    color = Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        CustomBadge(messages.size)
    }
}

@Composable
fun CustomBadge(unreadCount: Int) {
    if (unreadCount > 0) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(Color(0xFF3AC948), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$unreadCount",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    onSearchQueryChange: (String) -> Unit,
    onSearchBarClose: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .clickable { onSearchBarClose() }
        )

        Spacer(modifier = Modifier.width(16.dp))

        Surface(
            modifier = Modifier.weight(1f),
            color = Color.Transparent,
            shape = RoundedCornerShape(16.dp)
        ) {
            TextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    onSearchQueryChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 16.sp),
                placeholder = { Text(text = "Search") },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    BaatcheetTheme {
        MainScreen()
    }
}
