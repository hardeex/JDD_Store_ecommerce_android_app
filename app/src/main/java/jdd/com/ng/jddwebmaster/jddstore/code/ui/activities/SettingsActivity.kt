package jdd.com.ng.jddwebmaster.jddstore.code.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.cloud_firestore.FirestoreClass
import jdd.com.ng.jddwebmaster.jddstore.code.model.User
import jdd.com.ng.jddwebmaster.jddstore.code.utils.Constant
import jdd.com.ng.jddwebmaster.jddstore.code.utils.GlideLoader

class SettingsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var header_back_arrow_settings: ImageView
    private lateinit var user_image_settings: ImageView
    private lateinit var btnEdit_settings: Button
    private lateinit var user_gender_settings: TextView
    private lateinit var userEmail_settings: TextView
    private lateinit var user_mobile_number_settings: TextView
    private lateinit var username_settings: TextView
    private lateinit var user_address_settings: TextView
    private lateinit var btnLogout_settings: Button

    private lateinit var mUserDetails: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.hide()
        initiateVariables()

        header_back_arrow_settings.setOnClickListener(this)
        btnLogout_settings.setOnClickListener(this)
        btnEdit_settings.setOnClickListener(this)
    }

    private fun initiateVariables() {
        header_back_arrow_settings = findViewById(R.id.header_back_arrow_settings)
        username_settings = findViewById(R.id.tv_username_settings)
        user_image_settings = findViewById(R.id.user_image_settings)
        btnEdit_settings = findViewById(R.id.btnEdit_settings)
        user_gender_settings = findViewById(R.id.tv_user_gender_settings)
        userEmail_settings = findViewById(R.id.tv_userEmail_settings)
        user_mobile_number_settings = findViewById(R.id.tv_user_mobileNumber_settings)
        user_address_settings = findViewById(R.id.tv_user_address_settings)
        btnLogout_settings = findViewById(R.id.btn_logout_settings)
    }

    fun getUserDetailsSuccessfully(user: User){
        mUserDetails = user
        dismissProgressDialogue()
        // load the user image
        GlideLoader(this).loadUserImage(user.image, user_image_settings)
        // assign the name, email, gender and mobile number
        username_settings.text = "${user.user_firstName} ${user.user_lastName}"
        user_gender_settings.text = user.user_gender
        userEmail_settings.text = user.userEmail
        user_mobile_number_settings.text = "${user.mobileNumber}"

    }

   private fun getUserDetailsFromFirestore() {
        showProgressDialogue("Retrieving Data")
        FirestoreClass().getUserDetails(this)
    }

    override fun onClick(view: View?) {
        if (view != null){
            when(view.id){
                R.id.header_back_arrow_settings->{
                    onBackPressed()
                }

                R.id.btn_logout_settings ->{
                    // to sign out
                    FirebaseAuth.getInstance().signOut()
                    // navigate the user to another activity upon sign out
                    val intent = Intent(this, LoginActivity::class.java)
                    // clear all the layers of activities
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                R.id.btnEdit_settings->{
                    startActivity(Intent(this, UserProfileCompleteActivity::class.java)
                    // pass extra to store and retrieve data
                        .putExtra(Constant.EXTRA_USER_DETAILS, mUserDetails)
                )

                }


            }
        }
    }

    override fun onResume() {
        super.onResume()
        getUserDetailsFromFirestore()
    }
}