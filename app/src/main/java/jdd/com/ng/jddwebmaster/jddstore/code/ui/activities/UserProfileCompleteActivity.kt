package jdd.com.ng.jddwebmaster.jddstore.code.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.cloud_firestore.FirestoreClass
import jdd.com.ng.jddwebmaster.jddstore.code.model.User
import jdd.com.ng.jddwebmaster.jddstore.code.utils.Constant
import jdd.com.ng.jddwebmaster.jddstore.code.utils.GlideLoader
import java.io.IOException

class UserProfileCompleteActivity : BaseActivity(), View.OnClickListener {

    private lateinit var actionBar: Toolbar
    private lateinit var userProfilePicture: ImageView
    private lateinit var header_back_arrow_userProfile: ImageView
    private lateinit var fl_default_profilePicture: FrameLayout
    private lateinit var userProfile_firstname: TextInputLayout
    private lateinit var userProfile_lastname: TextInputLayout
    private lateinit var userProfile_email: TextInputLayout
    private lateinit var userProfile_phoneNumber: TextInputLayout
    private lateinit var rb_userGender_male: RadioButton
    private lateinit var rb_userGender_female: RadioButton
    private lateinit var btnUserProfile_Save: Button
    private lateinit var tv_complete_userProfile: TextView




    // make it a accessible throughout the class
   private lateinit var mUserDetails: User
   private  var mSelectedImageURI:Uri? = null
    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_complete)

        supportActionBar?.hide()
        //setUpActionBar()
        initiateVariables()
        userProfilePicture.setOnClickListener(this)
        btnUserProfile_Save.setOnClickListener(this)
        header_back_arrow_userProfile.setOnClickListener(this)

        // create an object of type USER
//        var userDetails = User()
        // check for the passed user data
        if (intent.hasExtra(Constant.EXTRA_USER_DETAILS)){
            // pass the data as a parcelable object and check if the data is not null
            mUserDetails = intent.getParcelableExtra(Constant.EXTRA_USER_DETAILS)!!
        }

        // assign the firstname,  lastname and email
        userProfile_firstname.editText?.setText(mUserDetails.user_firstName)
        userProfile_lastname.editText?.setText(mUserDetails.user_lastName)
        userProfile_email.isEnabled = false
        userProfile_email.editText?.setText(mUserDetails.userEmail)

        // check if the profile is completed or not
        if (mUserDetails.profileCompleted == 0){



            tv_complete_userProfile.text = "Complete Profile"
            // make the back arrow invisible
            header_back_arrow_userProfile.visibility = View.GONE

            // don't allow the user to change firstname, lastname and email -- Also, assign the value used by the user during registration
            userProfile_firstname.isEnabled = false
            //userProfile_firstname.editText?.setText(mUserDetails.user_firstName)

            userProfile_lastname.isEnabled = false
            //userProfile_lastname.editText?.setText(mUserDetails.user_lastName)


        } else{
            tv_complete_userProfile.text = "Edit Profile"
            GlideLoader(this).loadUserImage(mUserDetails.image, userProfilePicture)
//            userProfile_firstname.isEnabled = true
//            userProfile_lastname.isEnabled = true

            // assign the user email address
            userProfile_email.isEnabled = false
            userProfile_email.editText?.setText(mUserDetails.userEmail)

            // assign the user mobile number
            if (mUserDetails.mobileNumber != 0L ){
                // assign the user mobile number
                userProfile_phoneNumber.editText?.setText(mUserDetails.mobileNumber.toString())
            }

            // assign the user gender
            if (mUserDetails.user_gender == Constant.MALE){
                rb_userGender_male.isChecked = true
            } else{
                rb_userGender_female.isChecked = true
            }

        }
       // tv_complete_userProfile.text = "Complete Profile"
        // don't allow the user to change firstname, lastname and email -- Also, assign the value used by the user during registration
//        userProfile_firstname.isEnabled = false
//        userProfile_firstname.editText?.setText(mUserDetails.user_firstName)
//
//        userProfile_lastname.isEnabled = false
//        userProfile_lastname.editText?.setText(mUserDetails.user_lastName)

//        userProfile_email.isEnabled = false
//        userProfile_email.editText?.setText(mUserDetails.userEmail)

    }

//    private fun setUpActionBar() {
//        setSupportActionBar(tool_actionbar)
//        val actionBar = supportActionBar
//        if (actionBar != null){
//            actionBar.setDisplayHomeAsUpEnabled(true)
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
//        }
//        toobar_actionBar.
//    }

    private fun initiateVariables() {
        userProfilePicture = findViewById(R.id.userProfilePicture)
        fl_default_profilePicture = findViewById(R.id.FL_defaultProfilePic)
        userProfile_firstname = findViewById(R.id.userProfile_firstname)
        userProfile_lastname = findViewById(R.id.userProfile_lastname)
        userProfile_email = findViewById(R.id.userProfile_email)
        userProfile_phoneNumber = findViewById(R.id.userProfilePhoneNumber)
        rb_userGender_male = findViewById(R.id.userGender_male)
        rb_userGender_female = findViewById(R.id.userGender_female)
        btnUserProfile_Save = findViewById(R.id.btnSave_completeProfile)
        header_back_arrow_userProfile = findViewById(R.id.header_back_arrow_userProfile)
        tv_complete_userProfile = findViewById(R.id.tv_complete_userProfile)
       // actionBar = findViewById(R.id.toolBar_title_userProfile)
    }

    private fun validateUserProfileDetails(): Boolean{
        when{
            TextUtils.isEmpty(userProfile_phoneNumber.editText?.text.toString().trim { it<=' ' })->{
                userProfile_phoneNumber.error = "Please, enter your contact/mobile/phone number"
                return false
            }

           //TODO: validate the user gender
        }
        return true
    }

    private fun updateUserProfileDetails(){
        // create a HashMap for storing and passing the user data
        val userHashMap = HashMap<String, Any>()

        // assign the user firtsname
        val firstname = userProfile_firstname.editText?.text.toString().trim { it<=' ' }
        if (firstname != mUserDetails.user_firstName){
            userHashMap[Constant.FIRST_NAME] = firstname
        }

        // assign the user lastname
        val lastname = userProfile_lastname.editText?.text.toString().trim { it<=' ' }
        if (lastname != mUserDetails.user_lastName){
            userHashMap[Constant.LAST_NAME] = lastname
        }


        val mobileNumber = userProfile_phoneNumber.editText?.text.toString().trim { it<=' ' }

        if (mSelectedImageURI !=null){
            userHashMap[Constant.IMAGE] = mUserProfileImageURL
        }

        val userGender = if (rb_userGender_male.isChecked){
            Constant.MALE
        }else{
            Constant.FEMALE
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobileNumber.toString()){
            userHashMap[Constant.MOBILE_NUMBER] = mobileNumber.toLong()
            //userHashMap[Constant.MOBILE_NUMBER] = "0${mobileNumber.toLong()}"
        }

        if (userGender.isNotEmpty() && userGender != mUserDetails.user_gender){
            userHashMap[Constant.GENDER] = userGender
        }
        userHashMap[Constant.GENDER] = userGender
        userHashMap[Constant.COMPLETE_PROFILE] = 1

        FirestoreClass().updateUserProfileDetails(this, userHashMap)
    }

    fun userProfileUpdatedInFirestoreSuccessfully(){
        dismissProgressDialogue()
        Toast.makeText(this, " Your profile is updated successfully...", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    fun imageUploadToCloudStorageSuccessfully(imageURL:String){
//        dismissProgressDialogue()
//        Toast.makeText(this, "Image uploaded successfully....", Toast.LENGTH_SHORT).show()
        //store the image url in a variable
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }


    override fun onClick(view: View?) {
        if (view != null){
            when(view.id){
                R.id.userProfilePicture->{
                    // when the user clicks on the profile picture-- first, check for permission to access user storage device
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                        // if permission was initially granted --- launch the user image viewer app
                        //Toast.makeText(this, "Permission Granted Already!!", Toast.LENGTH_SHORT).show()
                        Constant.showUserImageViewerApp(this)

                    } else{
                        // Request permission to access user storage device
                        ActivityCompat.requestPermissions(this,
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constant.READ_EXTERNAL_STORAGE_PERMISSION_CODE)
                    }
                }

                R.id.btnSave_completeProfile->{
                    if (validateUserProfileDetails()){
                        showProgressDialogue("Please Wait...")
                        if (mSelectedImageURI !=null){
                            FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageURI, Constant.USER_PROFILE_IMAGE)
                        } else{
                            updateUserProfileDetails()
                        }
                        }
                }

                R.id.header_back_arrow_userProfile->{
                    onBackPressed()
                }


            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // when permission result (acceptance or rejection ) is received
        if (requestCode==Constant.READ_EXTERNAL_STORAGE_PERMISSION_CODE){
            // if permission result is acceptance
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
               // Toast.makeText(this, "Permission Granted successfully!!", Toast.LENGTH_SHORT).show()
                // launch the user image-viewer app
                Constant.showUserImageViewerApp(this)
            } else{
                // if the user do not allow permission
                Toast.makeText(this,
                    "Read Storage Access Permission Denied!! You can give access in your device App settings",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
         // TODO: make change to the deprecated method
        super.onActivityResult(requestCode, resultCode, data)
        // receive the result of the user adding a user profile image
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Constant.PICK_IMAGE_REQUEST_CODE){
                if (data !=null){
                    try {
                        mSelectedImageURI = data.data!!
                       // userProfilePicture.setImageURI(selectedImageFileURI)
                        GlideLoader(this).loadUserImage(mSelectedImageURI!!, userProfilePicture)
                    } catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this, "Error!!! Uploading Profile Picture", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }


}