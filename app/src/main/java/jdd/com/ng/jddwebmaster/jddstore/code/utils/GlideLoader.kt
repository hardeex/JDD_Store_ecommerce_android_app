package jdd.com.ng.jddwebmaster.jddstore.code.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import jdd.com.ng.jddwebmaster.jddstore.R
import java.io.IOException

class GlideLoader(val context: Context) {

    fun loadUserImage(image: Any, imageView: ImageView){
        try{
            // load the user image in  the imageview
            Glide.with(context) // the context
                .load(Uri.parse(image.toString())) // the image Uri
                .centerCrop() // image scale type
                .placeholder(R.drawable.default_profile_picture) // a default image if the uploaded image failed to load
                .into(imageView) // the allocated view for the uploaded image
        } catch (e: IOException){
            e.printStackTrace()
        }
    }

    fun loadProductImage(image: Any, imageView: ImageView){
        try{
            // load the user image in  the imageview
            Glide.with(context) // the context
                .load(Uri.parse(image.toString())) // the image Uri
                .centerCrop() // image scale type
                .into(imageView) // the allocated view for the uploaded image
        } catch (e: IOException){
            e.printStackTrace()
        }
    }
}