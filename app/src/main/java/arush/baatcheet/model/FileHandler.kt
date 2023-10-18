package arush.baatcheet.model

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import java.io.File
import java.io.FileWriter
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec

class FileHandler (private val context: Context){
    private val dir = context.filesDir
    private val subdir = File(dir, "BaatCheet")
    init {
        if(!subdir.exists()){
            subdir.mkdirs()
        }
    }

    fun keyGenCaller() : String{
        val keyArray = Cryptography().generateKey()
        storePrivateKey(keyArray[1] as ByteArray)
        return keyArray[0].toString()
    }

    private fun storePrivateKey(privateKey: ByteArray){
        val privateFile = File(subdir, "privateKey.key")
        if(!privateFile.exists()){
            privateFile.createNewFile()
        }
        val fileWriter = FileWriter(privateFile, false)

        fileWriter.write("")
        privateFile.writeBytes(privateKey)
        fileWriter.close()
    }
    fun getPrivateKey(){
        val privateFile = File(subdir, "privateKey.key")
        privateFile.readBytes()

//        val keySpec = PKCS8EncodedKeySpec(some);
//        val keyFactory = KeyFactory.getInstance("RSA")
//        Log.d("qwertyG", (keyFactory.generatePrivate(keySpec)).toString())
    }
}