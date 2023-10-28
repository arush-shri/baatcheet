package arush.baatcheet.presenter

import android.content.ContentResolver
import arush.baatcheet.model.AddContactModel
import arush.baatcheet.model.ContactItem
import arush.baatcheet.model.DatabaseHandler
import kotlinx.coroutines.flow.Flow

class AddContactPresenter {

    private val connection = DatabaseHandler()

    fun getContactList(contentResolver: ContentResolver):List<ContactItem>{
        return AddContactModel().getContactList(contentResolver)
    }
    fun getDPLink(username: String): Flow<String> {
        return connection.getDPLink(username)
    }
}