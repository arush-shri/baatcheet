package arush.baatcheet.view

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import arush.baatcheet.R
import arush.baatcheet.presenter.ProfileSectionPresenter
import arush.baatcheet.view.ui.theme.BaatcheetTheme
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter

class ProfileActivity : ComponentActivity() {
    private val reqCode = 1000
    private lateinit var filePresenter : ProfileSectionPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filePresenter = ProfileSectionPresenter(applicationContext)
        setContent {
            BaatcheetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ProfilePage(filePresenter){
                        changeDP()
                    }
                }
            }
        }
    }
    private fun changeDP(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        startActivityForResult(intent, reqCode)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK){
            if(requestCode == reqCode){
                if (data != null) {
                    filePresenter.setMyDP(data.data!!)
                }
            }
        }
    }
}

@Composable
fun ProfilePage(filePresenter: ProfileSectionPresenter,callback: ()->(Unit)) {
    val profileDetail = filePresenter.getProfileDetails()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
    ) {
        Text(
            text = "Account",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.lexend_regular))),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally) {
            ProfilePicture(filePresenter, modifier = Modifier
                .size(200.dp) //Pic Size
                .clip(CircleShape)
                .clickable { callback() }
                .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileDetails(
                label = "Name:",
                value = profileDetail[0],
                icon = Icons.Default.Person
            )

            Divider(modifier = Modifier
                .fillMaxWidth()
                .height(2.dp), color = Color.Gray)

            ProfileDetails(
                label = "Number: ",
                value = profileDetail[1],
                icon = Icons.Default.Phone
            )

            Divider(modifier = Modifier
                .fillMaxWidth()
                .height(2.dp), color = Color.Gray)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Handle click action for Saved Messages here */ }
            ) {
                SavedMessages(icon = Icons.Default.Star)
            }

            Divider(modifier = Modifier
                .fillMaxWidth()
                .height(2.dp), color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { /*OnClickLogic Here*/ },
                modifier = Modifier
                    .border(2.dp, Color(0xFF808080),
                        shape = RoundedCornerShape(8.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF311B92)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Edit Profile",
                    color = Color(0xFF311B92),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.lexend_regular))
                )
            }
        }
    }
}



@Composable
fun ProfileDetails(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(36.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = value,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.lexend_regular))
            )
        }
    }
}

@Composable
fun SavedMessages(icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(36.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .weight(1f) // Taking remaining space
                .padding(8.dp)
        ) {
            Text(
                text = "Saved Messages",
                fontSize = 22.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.lexend_regular))
            )
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ProfilePicture(profileSectionPresenter: ProfileSectionPresenter,modifier: Modifier) {
    Image(
        painter = rememberImagePainter(data = profileSectionPresenter.getMyDp()),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}

//@Preview(showBackground = true)
//@Composable
//fun ProfilePagePreview() {
//    BaatcheetTheme {
//        ProfilePage(ProfileSectionPresenter(LocalContext.current)){}
//    }
//}