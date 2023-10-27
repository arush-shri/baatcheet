package arush.baatcheet.presenter

import android.content.ContentResolver
import arush.baatcheet.model.AddContactModel
import arush.baatcheet.model.ContactItem

class AddContactPresenter {

    fun getContactList(contentResolver: ContentResolver):List<ContactItem>{
        return AddContactModel().getContactList(contentResolver)
    }
}