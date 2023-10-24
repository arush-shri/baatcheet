package arush.baatcheet.presenter

import android.content.Context
import android.net.Uri
import android.util.Log
import arush.baatcheet.model.DatabaseHandler
import arush.baatcheet.model.FileHandler
import kotlinx.coroutines.flow.Flow

class HomeScreenPresenter(private val context : Context) {
    private val connection = DatabaseHandler()
    fun getMessageList(): Flow<Map<String, Map<String, ArrayList<HashMap<String, String>>>>> {
        return connection.getMessagesList()
    }
    fun getMyDp(): Uri {
        return FileHandler(context).getMyDP()
    }
    fun getDPLink(username: String):Flow<String> {
        return connection.getDPLink(username)
    }
}