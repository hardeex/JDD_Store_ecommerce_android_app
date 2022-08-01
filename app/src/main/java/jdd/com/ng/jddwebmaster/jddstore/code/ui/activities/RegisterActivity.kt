package jdd.com.ng.jddwebmaster.jddstore.code.ui.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.cloud_firestore.FirestoreClass
import jdd.com.ng.jddwebmaster.jddstore.code.model.User

class RegisterActivity : BaseActivity(), View.OnClickListener {

    private lateinit var header_back_arrow: ImageView
    private lateinit var user_firstname: TextInputLayout
    private lateinit var user_lastName: TextInputLayout
    private lateinit var userEmail: TextInputLayout
    private lateinit var userPassword: TextInputLayout
    private lateinit var user_confirmPassword: TextInputLayout
    private lateinit var checkBox: CheckBox
    private lateinit var tv_terms_and_conditions: TextView
    private lateinit var btnLogin_register: Button
    private lateinit var btnSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // TODO: 1. Enable the user to register via google account automatically
        initializeVariables()
        supportActionBar?.hide()
        btnSignUp.setOnClickListener(this)
        btnLogin_register.setOnClickListener(this)
        header_back_arrow.setOnClickListener(this)
        tv_terms_and_conditions.setOnClickListener(this)




    } // End of the onCreate Class

    private fun initializeVariables() {
        header_back_arrow = findViewById(R.id.header_back_arrow)
        user_firstname = findViewById(R.id.register_firstName)
        user_lastName = findViewById(R.id.register_lastName)
        userEmail = findViewById(R.id.register_Email)
        userPassword = findViewById(R.id.register_password)
        user_confirmPassword = findViewById(R.id.register_confirm_password)
        checkBox = findViewById(R.id.checkBox_register)
        tv_terms_and_conditions = findViewById(R.id.tv_terms_and_conditions)
        btnSignUp = findViewById(R.id.btn_signUP_register)
        btnLogin_register =  findViewById(R.id.btn_login_register)
    }

    private fun validateUserInput(): Boolean{
        when{
            TextUtils.isEmpty(user_firstname.editText?.text.toString().trim { it<=' ' })->{
                user_firstname.error = resources.getString(R.string.first_name_error)
                return false
            }
            user_firstname.editText?.text.toString().trim { it<=' ' }.length<=3->{
                user_firstname.error = resources.getString(R.string.first_name_short)
                return false
            }
            TextUtils.isEmpty(user_lastName.editText?.text.toString().trim { it<=' ' })->{
                user_lastName.error = resources.getString(R.string.last_name_error)
                return false
            }
            user_lastName.editText?.text.toString().trim { it<=' ' }.length <=3->{
                user_lastName.error = resources.getString(R.string.last_name_short)
                return false
            }
            TextUtils.isEmpty((userEmail.editText?.text.toString().trim { it<=' ' }))->{
                userEmail.error = resources.getString(R.string.field_empty)
                return false
            }

            TextUtils.isEmpty(userPassword.editText?.text.toString().trim { it<=' ' })->{
                userPassword.error = resources.getString(R.string.enter_password)
                return false
            }
            userPassword.editText?.text.toString().trim { it<=' ' }.length <8->{
                userPassword.error = resources.getString(R.string.passworde_short)
                return false
            }
            TextUtils.isEmpty(user_confirmPassword.editText?.text.toString().trim { it<=' ' })->{
                user_confirmPassword.error = resources.getString(R.string.confirm_password)
                return false
            }
            user_confirmPassword.editText?.text.toString().trim { it<=' ' }
                    != userPassword.editText?.text.toString().trim { it<=' ' }->{
                user_confirmPassword.error = resources.getString(R.string.password_mismatch)
                return false
            }
            !checkBox.isChecked->{

                Toast.makeText(this, resources.getString(R.string.agree_terms) , Toast.LENGTH_SHORT).show()
                return false
            }

        }

        return true
    }

    private fun validateUserEmail(email:String):Boolean{
        //TODO: dismiss the error when the user email is validated
        if (TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            userEmail.error = "Invalid Email Format!"
        }
        //userEmail.error = "Invalid Email Format!"
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun registerUser(){
        if (validateUserInput() && validateUserEmail(userEmail.editText?.text.toString().trim { it <= ' ' }) ){

            showProgressDialogue("Loading")

            // converting the user email and password to string
            val email:String = userEmail.editText?.text.toString().trim { it<=' ' }
            val password: String=  userPassword.editText?.text.toString().trim { it<=' ' }


            /* create an instance and user -- creating the user details in the Firebase Auth
            create the user data in the firebase authentication --- the backend
            And, send a confirmation link to the userEmail
             */
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                // after the completion of creating the user email and password... then executes....
                .addOnCompleteListener(
                    OnCompleteListener <AuthResult> { task->

                        // when creating user details is successful
                        if (task.isSuccessful) {
                            Log.i(TAG, "New user Registration is successful")

                            // creating an identifier for each user
                            val user = Firebase.auth.currentUser
                            // sending confirmation link to the user
                            user!!.sendEmailVerification()

                                // after successful completion of sending the verification link
                                .addOnCompleteListener { sendVerificationLink->
                                    // if the sendVerificationLink registration is successful
                                    if (sendVerificationLink.isSuccessful){
                                        Log.i(TAG, " VERIFICATION LINK SENT SUCCESSFUL")
                                        Toast.makeText(this,
                                            "Confirm Your Email: Verification Link sent to $userEmail",
                                            Toast.LENGTH_LONG).show()
                                    } else{
                                        // if the verification link failed to send
                                        Log.e(TAG, "SendEmailVerification", task.exception)
                                        Toast.makeText(this,
                                            sendVerificationLink.exception?.message,
                                            Toast.LENGTH_LONG).show()
                                    }
                                    // the code for when confirmation link was sent successfully
                                    Toast.makeText(this,
                                        "Check ${userEmail.editText?.text.toString()} for verification link and then LOGIN",
                                        Toast.LENGTH_LONG ).show()
                                } // end of verification link code

                            // Firebase to register the user
                            val firebaseUser = task.result!!.user!!

                            // storing the user details
                            val userInfo = User(
                                firebaseUser.uid,
                                user_firstname.editText?.text.toString().trim { it<=' ' },
                                user_lastName.editText?.text.toString().trim { it<=' ' },
                                userEmail.editText?.text.toString().trim { it<=' ' }

                            )
                                // TODO: Make the collection identifier from the Firestore email instead of the uid

                            // registering the user in the cloud
                            FirestoreClass().uploadUserDetails(this@RegisterActivity, userInfo)
                            // to sign the user out of the register activity to the login activity if the registration is successful
                            // TODO: A user interface that allow the user know there is a confirmation link to activate the registered account


                            startActivity(Intent(this, LoginActivity::class.java))

//                            FirebaseAuth.getInstance().signOut()
//                            finish()

                        }else{
                            dismissProgressDialogue()
                            // if the user registration is not successful
                            Log.e(TAG, "Registration NOT successful")
                            Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_LONG)
                                .show()
                        }

                    })
        }
    }

    fun registerUserSuccessfully(){
        //TODO: implement single account--- No user can create more than one account
        dismissProgressDialogue()
        //Toast.makeText(this, "Registration successful...", Toast.LENGTH_SHORT).show()
    }

    override fun onClick(view: View?) {
        if (view != null){
            when(view.id){
                R.id.header_back_arrow->{
                    onBackPressed()
                }

                R.id.btn_login_register->{
                    onBackPressed()
                }

                R.id.tv_terms_and_conditions->{
                    //TODO: Navigate to the Terms and Condition on the Google Document

                }

                R.id.btn_signUP_register->{
                    registerUser()
                }

            } // end of the WHEN statement
        } // end of the IF statement
    }


} // End of the class