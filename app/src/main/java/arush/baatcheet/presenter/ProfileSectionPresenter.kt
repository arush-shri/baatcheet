package arush.baatcheet.presenter

import android.content.Context
import android.net.Uri
import arush.baatcheet.model.DatabaseHandler
import arush.baatcheet.model.FileHandler

class ProfileSectionPresenter(private val context: Context) {
    private val fileHandler = FileHandler(context)

    fun getProfileDetails(): ArrayList<String> {
        return fileHandler.getProfileDetails()
    }

    fun getMyDp(): Uri {
        return fileHandler.getMyDP()
    }
    fun setMyDP(image: Uri){
        DatabaseHandler().updateDP(image)
        fileHandler.storeDP(image)
    }
}