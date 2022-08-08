package jdd.com.ng.jddwebmaster.jddstore.code.ui.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.cloud_firestore.FirestoreClass
import jdd.com.ng.jddwebmaster.jddstore.code.model.Product
import jdd.com.ng.jddwebmaster.jddstore.code.utils.Constant
import jdd.com.ng.jddwebmaster.jddstore.code.utils.GlideLoader
import java.io.IOException

class AddProductActivity : BaseActivity(), View.OnClickListener {

    private lateinit var iv_header_back_arrow_addProduct: ImageView
    private lateinit var iv_product_image_addProduct: ImageView
    private lateinit var iv_add_product_image_camera_addProduct: ImageView
    private lateinit var tv_product_title_addProduct: TextInputLayout
    private lateinit var tv_product_price_addProduct: TextInputLayout
    private lateinit var tv_product_quantity_addProduct: TextInputLayout
    private lateinit var tv_product_description_addProduct: TextInputLayout
    private lateinit var btnSave_addProduct: Button

    private var mSelectedImageFileURl: Uri? = null
    // the global variable for the product image
    private var mProductImageURL: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        // hide the actionBar
        supportActionBar?.hide()
        initializeVariables()

        iv_header_back_arrow_addProduct.setOnClickListener(this)
        iv_product_image_addProduct.setOnClickListener(this)
        iv_add_product_image_camera_addProduct.setOnClickListener(this)
        btnSave_addProduct.setOnClickListener(this)
    }

    private fun initializeVariables() {
        iv_header_back_arrow_addProduct = findViewById(R.id.iv_header_back_arrow_addProduct)
        iv_add_product_image_camera_addProduct = findViewById(R.id.add_product_camera_addProduct)
        iv_product_image_addProduct = findViewById(R.id.iv_product_image_addProduct)
        tv_product_title_addProduct = findViewById(R.id.tv_product_title_addProduct)
        tv_product_price_addProduct = findViewById(R.id.tv_product_price_addProduct)
        tv_product_quantity_addProduct = findViewById(R.id.tv_product_quantity_addProduct)
        tv_product_description_addProduct = findViewById(R.id.tv_product_description_addProduct)
        btnSave_addProduct = findViewById(R.id.btn_save_addProduct)
    }

    private fun validateProductDetails(): Boolean{
         when{
            mSelectedImageFileURl == null ->{
                Toast.makeText(this, "You did not select a product image... ", Toast.LENGTH_SHORT).show()
                return false
            }
             TextUtils.isEmpty(tv_product_title_addProduct.editText?.text.toString().trim { it<=' ' })->{
                 tv_product_title_addProduct.error = "Please, enter the product title"
                 return false
             }
             TextUtils.isEmpty(tv_product_price_addProduct.editText?.text.toString().trim { it<=' ' })->{
                 tv_product_price_addProduct.error = "Please, enter product price"
                 return false
             }

             TextUtils.isEmpty(tv_product_quantity_addProduct.editText?.text.toString().trim { it<=' ' })->{
                 tv_product_quantity_addProduct.error = "Please, the number of products available for sale"
                 return false
             }
             TextUtils.isEmpty(tv_product_description_addProduct.editText?.text.toString().trim { it<=' ' })->{
                 tv_product_description_addProduct.error = "Please, enter product description"
                 return false
             }

        }

            return true


    }

    private fun uploadProductImageToFirestore(){
        showProgressDialogue("Please Wait...")
        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileURl, Constant.PRODUCT_IMAGE)
    }

    private fun uploadProductDetails(){
        // get the each user details via the sharedPreference
        val username = this.getSharedPreferences(Constant.JDD_SHARED_PREFERENCE, Context.MODE_PRIVATE).getString(Constant.LOGGED_IN_USER, "")!!
        // get the user uploaded product details
        val product= Product(
        FirestoreClass().getUserCurrentID(),
            username,
            tv_product_title_addProduct.editText?.text.toString().trim { it<=' ' },
            tv_product_price_addProduct.editText?.text.toString().trim { it<=' ' },
            tv_product_quantity_addProduct.editText?.text.toString().trim { it<=' ' },
            tv_product_description_addProduct.editText?.text.toString().trim { it<=' ' },
            mProductImageURL
        )

        FirestoreClass().uploadProductDetails(this, product)
    }

    fun productUploadSuccessfully(){
        dismissProgressDialog()
        Toast.makeText(this, "Product uploaded successfully...", Toast.LENGTH_SHORT).show()
        finish()

        // TODO: reduce the uploaded product images and user images --- the objective is to reduce the size the images takes in the firestore/
    }

    fun productImageUploadedSuccessfully(imageUrl: String){
       // dismissProgressDialogue()
        //Toast.makeText(this, "Product Image uploaded successfully", Toast.LENGTH_SHORT).show()
        mProductImageURL = imageUrl
        uploadProductDetails()
    }
    override fun onClick(view: View?) {
        if (view != null){
            when(view.id){
                R.id.iv_header_back_arrow_addProduct->{
                    onBackPressed()
                }

                R.id.add_product_camera_addProduct->{
                    // check if the app have access to the user storage
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Constant.showUserImageViewerApp(this)
                    } else{
                        // request permission from the user to access the user storage device in other to upload images
                        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE ),
                            Constant.READ_EXTERNAL_STORAGE_PERMISSION_CODE)
                    }
                }

                R.id.btn_save_addProduct ->{
                    if (validateProductDetails()){
                        //Toast.makeText(this, "validate successful", Toast.LENGTH_SHORT).show()
                        uploadProductImageToFirestore()
                    }
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

                    iv_add_product_image_camera_addProduct.setImageDrawable(ContextCompat.getDrawable(this,
                        R.drawable.ic_vector_edit_product_image))

                   mSelectedImageFileURl = data.data!!
                    try {
                        GlideLoader(this).loadUserImage(mSelectedImageFileURl!!, iv_product_image_addProduct)
                    } catch (e:IOException){
                        // print the cause of the error
                        e.printStackTrace()
                        Toast.makeText(this, "Error!!! updating product image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
}