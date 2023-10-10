package arush.baatcheet.model

import android.content.Context
import java.io.File
import java.security.KeyPairGenerator
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class FileHandler (private val context: Context){
    private val dir = context.filesDir
    private val subdir = File(dir, "BaatCheet")
    init {
        if(!subdir.exists()){
            subdir.mkdirs()
        }
    }

    fun keyGenCaller() : String{
        return generateKey()
    }
    @OptIn(ExperimentalEncodingApi::class)
    private fun generateKey() :String{
        val genKey = KeyPairGenerator.getInstance("RSA")
        genKey.initialize(1024)
        val keyPair = genKey.genKeyPair()
        val privateKey = keyPair.private
        val publicKey = keyPair.public
        val publicKeyBytes = publicKey.encoded
//        DECODING
//        val publicKeyByte = Base64.decode(baseK)
//        val keySpec = X509EncodedKeySpec(publicKeyBytes)
//        val keyFactory = KeyFactory.getInstance("RSA")
//        val decodedPublicKey = keyFactory.generatePublic(keySpec)
        return Base64.encode(publicKeyBytes)
    }
}