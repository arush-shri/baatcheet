package arush.baatcheet.model

import android.net.Uri
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DatabaseHandler {
    private val database : DatabaseReference = FirebaseDatabase.getInstance().getReference("baatcheet")
    private val storage : FirebaseStorage = FirebaseStorage.getInstance()
    fun login(username: String, phoneNumber: String, imageUri: Uri?)
    {
        if (imageUri != null) {
            val imageRef = storage.getReference("DP").child(phoneNumber + "DP")
            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        val userDetail = UserDetailModel(username, imageUrl)
                        database.child(phoneNumber).setValue(userDetail)
                    }
                        .addOnFailureListener {
                            val userDetail = UserDetailModel(username, "null")
                            database.child(phoneNumber).setValue(userDetail)
                        }
                }
        }
    }
    fun sendMessage(msg: String, sender: String, receiver: String){
        val timeStamp = getCombinedTimestamp()
        var message = ArrayList<HashMap<String, String>>()
        database.child(receiver).child("messageList").child(sender).child("messages")
            .get().addOnSuccessListener {
                if(it.value != null){
                    message = it.value as ArrayList<HashMap<String, String>>
                }
                message.add(hashMapOf("timestamp" to timeStamp, "message" to msg))
                database.child(receiver).child("messageList").child(sender).child("messages").setValue(message)
            }
    }
    private fun getCombinedTimestamp(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss")
        return currentDateTime.format(formatter)
    }
}