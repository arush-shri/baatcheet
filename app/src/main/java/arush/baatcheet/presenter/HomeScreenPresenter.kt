package arush.baatcheet.presenter

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import arush.baatcheet.model.Cryptography
import arush.baatcheet.model.DatabaseHandler
import arush.baatcheet.model.FileHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import java.security.PublicKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeScreenPresenter(private val context : Context) {
    private val connection = DatabaseHandler()
    private val fileHandler = FileHandler(context)
    private val cryptography = Cryptography()
    private val privateKey = fileHandler.getPrivateKey()
    private lateinit var publicKey: PublicKey

    companion object {
        private var instance: HomeScreenPresenter? = null

        fun getInstance(context: Context): HomeScreenPresenter {
            if (instance == null) {
                instance = HomeScreenPresenter(context)
            }
            return instance!!
        }
    }

    suspend fun getPublicKey(username: String){
        connection.getPublicKey(username).collect{
            publicKey = it
        }
    }
    suspend fun sendMessage(username: String, message:String){
        if(!this::publicKey.isInitialized){
            delay(2000)
        }
        val timeStamp = getCombinedTimestamp()
        val encryptedMessage = cryptography.encryptMessage(message, publicKey)
        connection.sendMessage(Base64.encodeToString(encryptedMessage, Base64.DEFAULT),username, timeStamp)
    }

    fun receiveMessage(username: String) = callbackFlow<ArrayList<HashMap<String, String>>>{
        connection.receiveMessage(username).collect{
            val messageList = ArrayList<HashMap<String,String>>()
            for (message in it){
                messageList.add(hashMapOf("timestamp" to message["timestamp"].toString(), "message" to cryptography.decryptMessage(message["message"].toString(),privateKey)))
            }
            trySend(messageList)
        }
    }

    fun getDecrypted(msg:String) : String{
        return cryptography.decryptMessage(msg,privateKey)
    }
    fun getMessageList(): Flow<Map<String, Map<String, ArrayList<HashMap<String, Any>>>>> {
        return connection.getMessagesList()
    }
    fun getMessageListFile(): Map<String, Map<String, ArrayList<HashMap<String, Any>>>>{
        return fileHandler.getHomeMessage()
    }
    fun setMessageList(messageList: Map<String, Map<String, ArrayList<HashMap<String, Any>>>>){
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

    fun getProfileDetails(): ArrayList<String> {
        return fileHandler.getProfileDetails()
    }

    fun setMyDP(image: Uri){
        DatabaseHandler().updateDP(image)
        fileHandler.storeDP(image)
    }

    private fun getCombinedTimestamp(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss")
        return currentDateTime.format(formatter)
    }
}