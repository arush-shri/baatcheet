package arush.panchayat.model

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DatabaseHandler {
    private val database : DatabaseReference = FirebaseDatabase.getInstance().getReference("baatcheet")
    fun login(username: String, phoneNumber: String)
    {
        val userDetail = UserDetail(username, "null")
        database.child(phoneNumber).setValue(userDetail)
    }
}