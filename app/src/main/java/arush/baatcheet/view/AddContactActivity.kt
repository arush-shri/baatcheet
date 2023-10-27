package arush.baatcheet.view

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import arush.baatcheet.R
import arush.baatcheet.presenter.AddContactPresenter
import arush.baatcheet.view.ui.theme.BaatcheetTheme

class AddContactActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BaatcheetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AddContact()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContact() {
    var textVisibility by remember { mutableStateOf(false) }
    var searchVisibility by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var contactList = AddContactPresenter().getContactList(LocalContext.current.contentResolver)

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.085f),
            contentAlignment = Alignment.BottomStart) {
            if(searchVisibility){
                BackHandler {
                    searchVisibility = false
                }
                SearchContacts(
                    onSearchBarClose = {
                        searchVisibility = false
                    }
                )
            }
            else{
                if(textVisibility){
                    TextField(value = searchText, onValueChange = { searchText=it }, placeholder = {Text("Enter Group Name",
                        modifier = Modifier.padding(start = 10.dp),color = if(isSystemInDarkTheme()){
                            Color.LightGray
                        }else{
                            Color.DarkGray
                        }, style = TextStyle(fontSize = 22.sp,fontFamily = FontFamily(Font((R.font.lexend_regular))))
                    )},
                        colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
                        modifier = Modifier.fillMaxSize(),
                        textStyle = TextStyle( fontSize = 22.sp,fontFamily = FontFamily(Font((R.font.lexend_regular))) )
                    )
                    BackHandler {
                        textVisibility = !textVisibility
                    }
                }
                else{
                    Row (modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically){
                        Row (modifier = Modifier
                            .fillMaxHeight()
                            .clickable { textVisibility = !textVisibility },
                            verticalAlignment = Alignment.CenterVertically){
                            Icon(imageVector = Icons.Filled.Add, contentDescription = "new group",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(top = 4.dp))
                            Text("Create New Group",
                                style = TextStyle(fontFamily = FontFamily(Font((R.font.lexend_regular))), fontSize = 23.sp),
                                modifier = Modifier.padding(start = 6.dp))
                        }
                        Icon(imageVector = Icons.Outlined.Search, contentDescription = "search",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(top = 6.dp)
                                .clickable { searchVisibility = true })
                    }
                }
            }
        }
        Divider(color = Color(0xFFA3A3A3), modifier = Modifier
            .fillMaxWidth()
            .height(1.dp))
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            LazyColumn(modifier = Modifier.fillMaxWidth(0.92f)){
                items(contactList){
                    Column (modifier = Modifier
                        .fillParentMaxHeight(0.08f)
                        .padding(vertical = 14.dp)){
                        ContactCard(it.name, it.phoneNumber, false)
                    }
                    Divider(color = Color(0xFFA3A3A3), modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContacts(
    onSearchBarClose: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
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
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 18.sp,fontFamily = FontFamily(Font((R.font.lexend_regular))),),
                placeholder = { Text(text = "Search", style = TextStyle(fontSize = 18.sp,fontFamily = FontFamily(Font((R.font.lexend_regular))),)) },
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

@Composable

fun ContactCard(name:String, number:String, invitation:Boolean){
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        Image(painter = painterResource(id = R.drawable.no_dp_logo), contentDescription = "image")
        Text(text = name)
    }
}

@Preview
@Composable
fun prev(){
    AddContact()
}
