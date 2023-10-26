package arush.baatcheet.presenter

import android.content.Context
import android.net.Uri
import android.util.Log
import arush.baatcheet.model.DatabaseHandler
import arush.baatcheet.model.FileHandler
import kotlinx.coroutines.flow.Flow

class HomeScreenPresenter(private val context : Context) {
    private val connection = DatabaseHandler()
    private val fileHandler = FileHandler(context)
    fun getMessageList(): Flow<Map<String, Map<String, ArrayList<HashMap<String, String>>>>> {
        return connection.getMessagesList()
    }
    fun getMessageListFile(): Map<String, Map<String, ArrayList<HashMap<String, String>>>>{
        return fileHandler.getHomeMessage()
    }
    fun setMessageList(messageList: Map<String, Map<String, ArrayList<HashMap<String, String>>>>){
        fileHandler.storeHomeMessage(messageList)
    }
    fun removeMessages(keyList: List<String>){
        connection.removeList(keyList)
    }
    fun getMyDp(): Uri {
        return fileHandler.getMyDP()
    }
    fun getDPLink(username: String):Flow<String> {
        return connection.getDPLink(username)
    }
}