package arush.baatcheet.model

import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast

class AddContactModel {

    fun getContactList(contentResolver: ContentResolver):List<ContactItem>{
        var contactList = mutableListOf<ContactItem>()
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneNumberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val phoneNumber = it.getString(phoneNumberIndex)
                contactList.add(ContactItem(name, phoneNumber.replace(" ", "")))
            }
        }
        return contactList
    }

    fun sendInvite(number:String,context: Context){
        val message = "Hey there! Chatting is more fun with friends. Join me on BaatCheet and let's catch up!"
        val smsManager = context.getSystemService(SmsManager::class.java)
        try {
            smsManager.sendTextMessage(number, null, message, null, null)
            Toast.makeText(context, "Invite Sent", Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception){
            Toast.makeText(context, "Unable to send invite ", Toast.LENGTH_SHORT).show()
        }
    }
}