package jdd.com.ng.jddwebmaster.jddstore.code.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.cloud_firestore.FirestoreClass
import jdd.com.ng.jddwebmaster.jddstore.code.model.User
import jdd.com.ng.jddwebmaster.jddstore.code.utils.Constant

class LoginActivity : BaseActivity(), View.OnClickListener {

    // Declaring the variables from the XML layout
    private lateinit var loginEmail: TextInputLayout
    private lateinit var loginPassword: TextInputLayout
    private lateinit var tV_login_forgotPassword: Button
    private lateinit var login_button: Button
    private lateinit var tv_signUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // hide the action bar for all devices and show the title bar for some devices
        hideActionBar()

        // TODO: Enable the user to login automatically via the Google account

        // initialize the XML ID
        loginEmail = findViewById(R.id.tv_login_email)
        loginPassword = findViewById(R.id.tv_login_password)
        tV_login_forgotPassword = findViewById(R.id.btn_login_forgotPassword)
        login_button = findViewById(R.id.login_button)
        tv_signUp = findViewById(R.id.btn_signUp_login)

        // initializing the views
        tv_signUp.setOnClickListener(this)
        tV_login_forgotPassword.setOnClickListener(this)
        login_button.setOnClickListener(this)

    } // End of the onCreate method

    private fun hideActionBar() {
        // removing the title bar in different android device
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // hiding the action bar
            supportActionBar?.hide()
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            // hiding the action bar
            supportActionBar?.hide()
        }
    } // end of the hideActionBar method



    private fun validateUserLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(loginEmail.editText?.text.toString().trim { it <= ' ' }) -> {
                loginEmail.error = "Please, enter your registered email address"
                false
            }
            TextUtils.isEmpty(loginPassword.editText?.text.toString().trim { it <= ' ' }) -> {
                loginPassword.error = "Password must be filled"
                false
            }
            else -> {
                true
            } // end of the else statement

        } // end of the when statement
    } // end of the validateLoginDetails function
            //TODO: Remember to chance the screen orientation of all activities before launching


    private  fun loginUser(){
        if (validateUserLoginDetails()) {
            // show progress bar to the user
            showProgressDialogue("Logging In User...")


            // collect the user details, convert to string and trim
            val email = loginEmail.editText?.text.toString().trim { it <= ' ' }
            val password = loginPassword.editText?.text.toString().trim { it <= ' ' }

            // check the user details from the Firebase Console for logging in feedback
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                // if successfully
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        // creating an identifier for each user
                        val user = Firebase.auth.currentUser
                        // when there is a user i.e the list of user is not zero or empty
                        if (user!=null){
                            // checking if the user has verify the register email by clicking on the confirmation link sent to the user registered email
                            val userEmailIsVerify = user.isEmailVerified
                            // when the registered email is verify
                            if (userEmailIsVerify){
                                //TODO: change the confirmation link text
                                FirestoreClass().getUserDetails(this@LoginActivity)
                                Toast.makeText(this@LoginActivity, "Welcome " , Toast.LENGTH_SHORT).show()

                            } else{
                                dismissProgressDialog()
                                //  when the user try to sign in without verifying the registered email via the confirmation link
                                Toast.makeText(this,
                                    "Un-verify email account, check ${loginEmail.editText?.text.toString()} for confirmation link",
                                    Toast.LENGTH_LONG).show()
                            }

                        } else{
                            // when the user is null
                            Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                        }


                    } // end of the if statement
                    else {
                        dismissProgressDialog()
                        // when the task is not successful
                        Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_LONG)
                            .show()
                    }

                }
        }
    }


    fun userLoggedInSuccessfully(user: User){
        dismissProgressDialog()
        // log the user names and email
        Log.i("First Name: ", user.user_firstName)
        Log.i("Last Name: ", user.user_lastName)
        Log.i("Email: ", user.userEmail)

        // check if the user has complete the profile

        if (user.profileCompleted == 0){
            // navigate the user the complete profile activity
            startActivity(Intent(this, UserProfileCompleteActivity::class.java)
                // pass the user data  as a parcelable object via intent
                .putExtra(Constant.EXTRA_USER_DETAILS, user))
        } else{
            // navigate the user to the mainActivity
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        }

        // disable the back button --- avoid the user to go back to the previous activity
        finish()
    }




    override fun onClick(view:View?){
        if (view != null){
            when(view.id){
                R.id.btn_signUp_login->{
                    startActivity(Intent(this, RegisterActivity::class.java))
                }


                R.id.btn_login_forgotPassword->{
                    startActivity(Intent(this, ForgotPasswordActivity::class.java))

                }


                R.id.login_button->{
                    loginUser()
                }


            }
        }
    }


} // End of the Class