package arush.panchayat.model

import android.net.Uri
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class DatabaseHandler {
    private val database : DatabaseReference = FirebaseDatabase.getInstance().getReference("baatcheet")
    private val storage : FirebaseStorage = FirebaseStorage.getInstance()
    fun login(username: String, phoneNumber: String, imageUri: Uri?)
    {
        if (imageUri != null) {
            val imageRef = storage.getReference("DP").child(phoneNumber + System.currentTimeMillis())
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
}