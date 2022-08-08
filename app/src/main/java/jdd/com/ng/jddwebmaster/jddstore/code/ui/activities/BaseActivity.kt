package jdd.com.ng.jddwebmaster.jddstore.code.ui.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import jdd.com.ng.jddwebmaster.jddstore.R

open class BaseActivity : AppCompatActivity() {

    private lateinit var mProgressDialog: Dialog;
    private var doubleClickToExitOnce = false

     fun showSnackBar (message: String, errorMessage: Boolean){
         val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
         val snackView = snackBar.view

         if (errorMessage){
             snackView.setBackgroundColor(ContextCompat.getColor(this, R.color.snackBarError_color))
         } else{
             snackView.setBackgroundColor(ContextCompat.getColor(this, R.color.snackBarSuccess_color))
         }
         snackBar.show()
     }

    fun showProgressDialogue(message: String){
        mProgressDialog = Dialog(this)
        //  val inflate = (this as Activity).layoutInflater.inflate(R.layout.progress_dialogue, null)
        val view = LayoutInflater.from(this).inflate(R.layout.progress_dialogue, null)
        view.findViewById<TextView>(R.id.tv_progress_dialogue).text = message
        mProgressDialog.setContentView(view)
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }

    fun dismissProgressDialog(){
       mProgressDialog.dismiss()
    }

    fun doubleClickToExist(){
        // This is method is to enable the user not exist the app unless cthe exit button is clicked twice
        if (doubleClickToExitOnce){
            super.onBackPressed()
            return
        }
        this.doubleClickToExitOnce = true
        Toast.makeText(this, "Please, click back again to exits", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
           doubleClickToExitOnce = false
        }, 2000)
    }

} // end of the class