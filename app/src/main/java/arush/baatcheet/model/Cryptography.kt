package arush.baatcheet.model

import android.util.Log
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.spec.X509EncodedKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class Cryptography {

    @OptIn(ExperimentalEncodingApi::class)
    fun generateKey() : Array<*>{
        val genKey = KeyPairGenerator.getInstance("RSA")
        genKey.initialize(1024)
        val keyPair = genKey.genKeyPair()
        val privateKey = keyPair.private
        val publicKey = keyPair.public
        Log.d("qwertyM", privateKey.toString())
        return arrayOf(Base64.encode(publicKey.encoded), privateKey.encoded)
    }
    @OptIn(ExperimentalEncodingApi::class)
    fun getPubKey(databaseConnection: DatabaseHandler, toWhom: String){
        databaseConnection.getPublicKey(toWhom){
            val publicKeyBytes = Base64.decode(it)
            val keySpec = X509EncodedKeySpec(publicKeyBytes)
            val keyFactory = KeyFactory.getInstance("RSA")
            val decodedPublicKey = keyFactory.generatePublic(keySpec)
        }
    }
}