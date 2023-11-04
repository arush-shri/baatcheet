package arush.baatcheet.model

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class DatabaseHandler {

    private val database : DatabaseReference = FirebaseDatabase.getInstance().getReference("baatcheet")
    private val storage : FirebaseStorage = FirebaseStorage.getInstance()
    private val userNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()

    fun login(username: String, phoneNumber: String, imageUri: Uri?, publicKey: String) {
        Log.d("qwertyLog2", imageUri.toString())
        if (imageUri != null) {
            val imageRef = storage.getReference("DP").child(phoneNumber + "DP")
            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        Log.d("qwertyLog3", imageUri.toString())
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

    fun getMyNum():String{
        return userNumber
    }
    fun updateDP(imageUri: Uri){
        val imageRef = storage.getReference("DP").child(userNumber + "DP")
        imageRef.putFile(imageUri).addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener {
                database.child(userNumber).child("profileDPLink").setValue(it.toString())
            }
        }
    }
    @OptIn(ExperimentalEncodingApi::class)
    fun getPublicKey(toWhom: String) = callbackFlow<PublicKey>{
        database.child(toWhom).child("pubKey").get().addOnSuccessListener {
            val publicKeyBytes = Base64.decode(it.value.toString())
            val keySpec = X509EncodedKeySpec(publicKeyBytes)
            val keyFactory = KeyFactory.getInstance("RSA")
            trySend(keyFactory.generatePublic(keySpec))
        }
        awaitClose {  }
    }

    fun sendMessage(msg: String, toWhom: String, timeStamp:String){
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

    fun receiveMessage(fromWhom: String) = callbackFlow<ArrayList<HashMap<String, Any>>>{
        var messages = ArrayList<HashMap<String, Any>>()
        val dbRef = database.child(userNumber).child("messageList").child(fromWhom).child("messages")
        val valueEventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists() && snapshot.value != null){
                    messages = snapshot.value as ArrayList<HashMap<String, Any>>
                    trySend(messages)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }

        }
        dbRef.addValueEventListener(valueEventListener)
        awaitClose{
            dbRef.removeEventListener(valueEventListener)
        }
    }

    fun getMessagesList() = callbackFlow<Map<String, Map<String, ArrayList<HashMap<String, Any>>>>> {
        val dbReference = database.child(userNumber).child("messageList")
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val data = snapshot.value as Map<String, Map<String, ArrayList<HashMap<String, Any>>>>
//                    Log.d("qwertyD", data.toString())
                    trySend(data).isSuccess
                } else {
                    trySend(emptyMap()).isSuccess
                }
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        dbReference.addValueEventListener(valueEventListener)
        awaitClose {
            dbReference.removeEventListener(valueEventListener)
        }
    }
    fun removeList(username: String){
        database.child(userNumber).child("messageList").child(username).child("messages").setValue(null)
    }

    fun getDPLink(username: String)= callbackFlow<String>{
        database.child(username).child("profileDPLink").get().addOnSuccessListener {
            if(it.value != null){
                trySend(it.value.toString())
            }
        }
        awaitClose { }
    }

    fun createGroup(contact: String, groupName: String): String{
        val uniqueID = database.push().key + groupName
        database.child(contact).child("messageList").child(uniqueID).setValue(emptyList<HashMap<String, String>>())
        return uniqueID
    }
    fun EditProfile(username: String,phoneNumber: String, imageUri: Uri?){
        val imageRef = storage.getReference("DP").child(phoneNumber + "DP")
        if (imageUri != null) {
            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { url ->
                        val imageUrl = url.toString()
                        database.child(phoneNumber).child("profileDPLink").setValue(imageUrl)
                    }
                }
        }
        database.child(phoneNumber).child("name").setValue(username)
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