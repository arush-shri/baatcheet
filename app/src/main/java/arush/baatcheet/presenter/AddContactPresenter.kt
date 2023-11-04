package arush.baatcheet.presenter

import android.content.ContentResolver
import android.content.Context
import arush.baatcheet.model.AddContactModel
import arush.baatcheet.model.ContactItem
import arush.baatcheet.model.DatabaseHandler
import arush.baatcheet.model.FileHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class AddContactPresenter {

    private val connection = DatabaseHandler()
    private val addContactModel = AddContactModel()

    fun getContactList(contentResolver: ContentResolver):List<ContactItem>{
        return addContactModel.getContactList(contentResolver)
    }
    fun getDPLink(username: String): Flow<String> {
        return connection.getDPLink(username)
    }

    fun sendInvite(number:String, context: Context){
        addContactModel.sendInvite(number,context)
    }

    suspend fun createGroup(contactList: Set<String>, name: String, context: Context){
        for (contact in contactList){
            val groupName = connection.createGroup(contact, name)
            connection.getPublicKey(name).collect{
                FileHandler(context).storeGroup(groupName, contact, it)
            }
        }
    }

    fun addContact(username: String, context: Context){
        FileHandler(context).addContact(username)
    }
}