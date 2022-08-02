package jdd.com.ng.jddwebmaster.jddstore.code.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val userID: String = "",
    val user_name: String = "",
    val product_title: String = "",
    val product_price: String = "",
    val stock_quantity: String = "",
    val product_description: String = "",
    val product_image: String = "",
    var product_id: String = ""
) : Parcelable
