package arush.baatcheet.model

import android.net.Uri
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONArray
import org.json.JSONObject

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
        val timeStamp = ""
        var message: JSONArray? = null
        val msgObject = JSONObject()
        database.child(receiver).child("messageList").child(sender).child("messages")
            .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    message = dataSnapshot.getValue(JSONArray::class.java)
                } else {
                    message = JSONArray()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("DBError", databaseError.message)
            }
        })

        msgObject.put(timeStamp, msg)
        message!!.put(msgObject)

        database.child(receiver).child("messageList").child(sender).child("messages").setValue(message)
    }
}