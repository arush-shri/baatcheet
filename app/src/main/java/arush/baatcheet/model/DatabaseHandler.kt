package arush.baatcheet.model

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DatabaseHandler {

    private val database : DatabaseReference = FirebaseDatabase.getInstance().getReference("baatcheet")
    private val storage : FirebaseStorage = FirebaseStorage.getInstance()
    private val userNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()

    fun login(username: String, phoneNumber: String, imageUri: Uri?, publicKey: String) {
        if (imageUri != null) {
            val imageRef = storage.getReference("DP").child(phoneNumber + "DP")
            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        val userDetail = UserDetailModel(username, imageUrl, publicKey)
                        database.child(phoneNumber).setValue(userDetail)
                    }
                        .addOnFailureListener {
                            val userDetail = UserDetailModel(username, "null", publicKey)
                            database.child(phoneNumber).setValue(userDetail)
                        }
                }
        }
    }
    fun getPublicKey(toWhom: String, callback: (String) -> (Unit)){
        database.child(toWhom).child("pubKey").get().addOnSuccessListener {
            callback(it.value.toString())
        }
    }

    fun sendMessage(msg: String, toWhom: String, timeStamp:String){
        //msg bhejte waqt ui me hi krle. Baad me
        var message = ArrayList<HashMap<String, String>>()
        database.child(toWhom).child("messageList").child(userNumber).child("messages")
            .get().addOnSuccessListener {
                if(it.value != null){
                    message = it.value as ArrayList<HashMap<String, String>>
                }
                message.add(hashMapOf("timestamp" to timeStamp, "message" to msg))
                database.child(toWhom).child("messageList").child(userNumber).child("messages").setValue(message)
            }
    }

    private fun getCombinedTimestamp(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss")
        return currentDateTime.format(formatter)
    }

    fun recvMessage() : ArrayList<HashMap<String, String>>{
        var messages = ArrayList<HashMap<String, String>>()
        database.child(userNumber).child("messageList").child(userNumber).child("messages")
            .get().addOnSuccessListener {
                if(it.value != null){
                    messages = it.value as ArrayList<HashMap<String, String>>
                }
            }
        return messages
    }

    private fun uploadData(imageUri: Uri, toWhom: String, timeStamp: String){
        val imageRef = storage.getReference("SentFile").child(userNumber)
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    sendMessage(imageUrl, toWhom, timeStamp)
                }
            }
    }
}