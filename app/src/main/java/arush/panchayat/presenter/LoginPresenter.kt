package arush.panchayat.presenter

import android.net.Uri
import arush.panchayat.model.LoginModel
import arush.panchayat.view.LoginActivity

class LoginPresenter(context: LoginActivity) {
    private lateinit var loginModel: LoginModel
    private val context = context
    fun login(username: String, phoneNumber: String, imageUri: Uri?){
        loginModel = LoginModel(this,username, phoneNumber, context, imageUri)
    }
    fun verifier(code: String){
        loginModel.verifyCode(code)
    }
    fun verified(){
        context.authCompleted()
    }
}