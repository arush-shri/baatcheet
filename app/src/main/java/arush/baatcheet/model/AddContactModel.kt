package arush.baatcheet.model

import android.content.ContentResolver
import android.provider.ContactsContract
import android.util.Log

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
}