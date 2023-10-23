package arush.baatcheet.presenter

import arush.baatcheet.model.DatabaseHandler
import arush.baatcheet.model.SaveMessageModel

class HomeScreenPresenter() {
    private val connection = DatabaseHandler()
    suspend fun getMessageList(): Map<String, Map<String,ArrayList<HashMap<String, String>>>> {
        return connection.getMessagesList()
    }
}