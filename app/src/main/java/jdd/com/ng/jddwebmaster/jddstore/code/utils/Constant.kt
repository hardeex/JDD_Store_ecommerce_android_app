package jdd.com.ng.jddwebmaster.jddstore.code.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constant {
    const val SUBSCRIBERS: String = "Subscribers"
    const val PRODUCT: String = "products"

    const val JDD_SHARED_PREFERENCE: String = "JDDSharedPreference"
    const val LOGGED_IN_USER: String = "LoggedInUsername"
    const val EXTRA_USER_DETAILS: String = "ExtraUserDetails"

    const val READ_EXTERNAL_STORAGE_PERMISSION_CODE: Int = 2022
    const val PICK_IMAGE_REQUEST_CODE = 1994

    const val MALE: String = "male"
    const val FEMALE: String = "female"
    const val GENDER: String = "user_gender"
    const val MOBILE_NUMBER: String = "mobileNumber"
    const val USER_PROFILE_IMAGE: String = "userProfileImage"
    const val IMAGE: String = "image"
    const val COMPLETE_PROFILE = "profileCompleted"
    const val FIRST_NAME: String = "user_firstName"
    const val LAST_NAME: String = "user_lastName"
    const val PRODUCT_IMAGE: String = "product_image"

    const val USER_ID: String = "userID"


    // create a function that access images on the user device
    fun showUserImageViewerApp(activity: Activity){
        // create an intent for launching user image-viewer apps
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}