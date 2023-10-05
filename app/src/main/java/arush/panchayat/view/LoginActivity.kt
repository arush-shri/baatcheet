package arush.panchayat.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import arush.panchayat.MainActivity
import arush.panchayat.databinding.ActivityLoginBinding
import arush.panchayat.model.DatabaseHandler
import arush.panchayat.presenter.LoginPresenter
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()
    private var checker = true

    override fun onStart() {
        super.onStart()

        val user = auth.currentUser
        if (user != null) {
            authCompleted()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root
        setContentView(view)
        val loginPresenter = LoginPresenter(this)

        loginBinding.getOTPButton.setOnClickListener {
            if (checker){
                checker = false
                if (loginBinding.nameInput.text.isNullOrEmpty() || loginBinding.numberInput.text.isNullOrEmpty()) {
                    Toast.makeText(this@LoginActivity, "Name or number missing", Toast.LENGTH_SHORT).show()
                }
                else if(loginBinding.numberInput.text?.length!! != 10 ){
                    Toast.makeText(this@LoginActivity, "Enter 10 digit phone number", Toast.LENGTH_SHORT).show()
                }
                else {
                    loginPresenter.login(loginBinding.nameInput.text.toString(),
                        "+91${loginBinding.numberInput.text.toString()}")
                    loginBinding.otpLayout.isVisible = true
                    loginBinding.getOTPButton.text = "VERIFY"
                    loginBinding.textInputLayout.isEnabled = false
                    loginBinding.textInputLayout2.isEnabled = false
                }
            }
            else{
                if(loginBinding.otpInput.text.isNullOrEmpty()){
                    Toast.makeText(this@LoginActivity, "Enter OTP", Toast.LENGTH_SHORT).show()
                }
                else{
                    loginPresenter.verifier(loginBinding.otpInput.text.toString())
                }
            }
        }
    }
    fun authCompleted(){
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}