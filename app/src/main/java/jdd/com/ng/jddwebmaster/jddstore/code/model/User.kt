package jdd.com.ng.jddwebmaster.jddstore.code.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@kotlinx.parcelize.Parcelize
class User (
    /*
   The class contains the information this app is storing about the user
    */

val id:String="",
val user_firstName:String="",
val user_lastName:String="",
val userEmail:String = "",
val mobileNumber:Long = 0,
val image: String ="",
val user_gender:String = "",
val profileCompleted: Int = 0
): Parcelable




