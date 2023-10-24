package arush.baatcheet.model

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import arush.baatcheet.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.net.URI
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
    fun storeDP(imageUri: Uri){
        val file = File(subdir, "dp.jpg")
        FileInputStream(File(imageUri.path)).use {input->
            FileOutputStream(file).use {output->
                val buffer = ByteArray(1024)
                var byteRead: Int
                while (input.read(buffer).also { byteRead = it} != -1){
                    output.write(buffer,0,byteRead)
                }
            }
        }
    }
    fun storeDPRes(){
        val drawable = ContextCompat.getDrawable(context, R.drawable.no_dp_logo)
        val bitmap = drawable?.toBitmap()
        if (bitmap != null){
            val file = File(subdir, "dp.jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
        }
    }
    fun getMyDP(): Uri {
        val file = File(subdir, "dp.jpg")
        return Uri.fromFile(file)
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
        val keyBytes = privateFile.readBytes()

        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        keyFactory.generatePrivate(keySpec)
    }

    fun storeSavedMessage(username: String, message: Any?, timestamp: String){
        val data = SaveMessageModel(username, message, timestamp)
        val file = File(subdir, "saveMessage.json")
        val storedData = retrieveSavedMessage()
        if(!file.exists()){
            file.createNewFile()
        }
        val gson = Gson()
        storedData.add(data)

        val jsonData = gson.toJson(storedData)
        val fileWriter = FileWriter(file, false)
        fileWriter.write(jsonData)
        fileWriter.close()
    }

    fun retrieveSavedMessage() : ArrayList<SaveMessageModel>{
        val file = File(subdir, "saveMessage.json")
        if(file.exists()){
            val gson = Gson()
            val jsonData = file.readText()
            val dataList = object : TypeToken<ArrayList<SaveMessageModel>>() {}.type
            return gson.fromJson(jsonData,dataList)
        }
        return ArrayList<SaveMessageModel>()
    }

    fun storeChatMessage(username: String, message: Any?, timestamp: String){
        val file = File(subdir, username+"DM.json")
        if(!file.exists()){
            file.createNewFile()
        }
        val gson = Gson()
        val jsonData = gson.toJson(SaveMessageModel(username,message, timestamp))
        val fileWriter = FileWriter(file, true)
        fileWriter.write(jsonData)
        fileWriter.close()
    }

    fun retrieveChatMessage(username: String) : ArrayList<SaveMessageModel>{
        val file = File(subdir, username+"DM.json")
        val gson = Gson()
        if(file.exists()){
            val messageArray = ArrayList<SaveMessageModel>()
            val jsonData = file.readText()
            val jsonObjects = jsonData.split("}")
            for (message in jsonObjects){
                val orgMessage = gson.fromJson("$message}", SaveMessageModel::class.java)
                messageArray.add(orgMessage)
            }
            return messageArray
        }
        return ArrayList<SaveMessageModel>()
    }
}