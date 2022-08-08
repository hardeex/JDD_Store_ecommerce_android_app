package jdd.com.ng.jddwebmaster.jddstore.code.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import jdd.com.ng.jddwebmaster.jddstore.R

class ForgotPasswordActivity : BaseActivity() {

    private lateinit var tv_emai_forgot_password: TextInputLayout
    private lateinit var btn_forgot_password: Button
    private lateinit var header_back_arrow: ImageView
    private lateinit var btn_go_to_login: Button
    private lateinit var btn_go_to_register: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        supportActionBar?.hide()

        tv_emai_forgot_password = findViewById(R.id.userEmail_forgot_password)
        btn_forgot_password = findViewById(R.id.btn_forgot_password)
        header_back_arrow = findViewById(R.id.header_back_arrow)
        btn_go_to_register= findViewById(R.id.btn_go_to_register_activity)
        btn_go_to_login= findViewById(R.id.btn_go_to_login_activity)


        header_back_arrow.setOnClickListener { onBackPressed() }
        btn_go_to_login.setOnClickListener {  onBackPressed()}


        btn_go_to_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btn_forgot_password.setOnClickListener {
            val email = tv_emai_forgot_password.editText?.text.toString().trim { it<=' ' }
            if (email.isEmpty()){
                tv_emai_forgot_password.error = "Enter your registered email address"
                return@setOnClickListener
            }else{
                showProgressDialogue("Sending Password Rest Link...")
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task->
                        dismissProgressDialog()
                        if (task.isSuccessful){
                            //TODO: change the Firebase text for password reset-- The password reset link
                            Toast.makeText(this, "Email sent successfully to $email for resetting your password...", Toast.LENGTH_LONG).show()
                            // TODO: Use an AlertDialog instead of the toast message for the email sent successfully
                            finish()
                        } else{
                            Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                        }
                    }

            }

        }


    }
}