package arush.baatcheet.model

import android.util.Log
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
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
        return arrayOf(Base64.encode(publicKey.encoded), privateKey.encoded)
    }

    fun encryptMessage(message:String, publicKey: PublicKey): ByteArray {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(message.toByteArray())
    }

    fun decryptMessage(encryptedText: Any?, privateKey: PrivateKey):String{
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedBytes = cipher.doFinal(encryptedText as ByteArray?)
        return String(decryptedBytes)
    }
}