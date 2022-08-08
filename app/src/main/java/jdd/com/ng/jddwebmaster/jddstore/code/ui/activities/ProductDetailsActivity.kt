package jdd.com.ng.jddwebmaster.jddstore.code.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.cloud_firestore.FirestoreClass
import jdd.com.ng.jddwebmaster.jddstore.code.model.CartItem
import jdd.com.ng.jddwebmaster.jddstore.code.model.Product
import jdd.com.ng.jddwebmaster.jddstore.code.utils.Constant
import jdd.com.ng.jddwebmaster.jddstore.code.utils.GlideLoader

class ProductDetailsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var iv_header_back_arrow: ImageView
    private lateinit var iv_product_details_image: ImageView
    private lateinit var tv_product_details_title: TextView
    private lateinit var tv_product_details_price: TextView
    private lateinit var tv_product_details_description: TextView
    private lateinit var tv_product_details_stock_qtty: TextView
    private lateinit var tv_product_details_quantity: TextView
    private lateinit var btn_add_to_cart: Button
    private lateinit var btn_go_to_cart: Button

    private var mProductID: String = ""
    private lateinit var mProductDetails: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        supportActionBar?.hide()
        initializeVariable()

        iv_header_back_arrow.setOnClickListener(this)
        btn_add_to_cart.setOnClickListener(this)
        btn_go_to_cart.setOnClickListener(this)

        // receive the data in the extra_product_id
        if (intent.hasExtra(Constant.EXTRA_PRODUCT_ID)){
            mProductID = intent.getStringExtra((Constant.EXTRA_PRODUCT_ID))!!
            Log.i("Product ID: ", mProductID)
        }

        var ownerProductId: String = ""
        if (intent.hasExtra(Constant.EXTRA_PRODUCT_OWNER_ID)){
            ownerProductId = intent.getStringExtra(Constant.EXTRA_PRODUCT_OWNER_ID)!!
            Log.i("Owner Product ID: ", mProductID)
        }

        // validate the ownerProductId
        if (FirestoreClass().getUserCurrentID() == ownerProductId){
            btn_add_to_cart.visibility = View.GONE
            btn_go_to_cart.visibility = View.GONE
        } else{
            btn_add_to_cart.visibility = View.VISIBLE
        }

        // load the method frp getting product details
        getProductDetailsFromFirestore()

        // TODO: Enable the owner of the product to edit the product details== HintL userProfileActivity
    }

        fun getProductDetailsSuccessful(product: Product){
            // assign the product variable to the global mProductDetails
            mProductDetails = product

        // load the image and pass other other data of each product
        GlideLoader(this).loadProductImage(product.product_image, iv_product_details_image)
        tv_product_details_title.text = product.product_title
        tv_product_details_price.text = "NGN ${product.product_price}"
        tv_product_details_description.text = product.product_description
        tv_product_details_stock_qtty.text = product.stock_quantity

            if (product.stock_quantity.toInt() == 0){
                dismissProgressDialog()
                btn_add_to_cart.visibility = View.GONE
                tv_product_details_stock_qtty. text = resources.getString(R.string.lbl_out_of_stock)
                tv_product_details_stock_qtty.setTextColor(ContextCompat.getColor(this, R.color.red))
            } else{
                if (FirestoreClass().getUserCurrentID() == product.user_id){
                    dismissProgressDialog()
                } else{
                    FirestoreClass().checkIfItemExistsInCart(this, mProductID)
                }
            }
    }

    fun addToCartSuccess(){
        dismissProgressDialog()
        Toast.makeText(this, "Product added to cart...", Toast.LENGTH_LONG).show()

        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }

    fun productExistInCart(){
        dismissProgressDialog()
        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE

    }

    private fun initializeVariable() {
        iv_header_back_arrow = findViewById(R.id.header_back_arrow_productDetails)
        iv_product_details_image = findViewById(R.id.iv_product_detail_image)
        tv_product_details_title = findViewById(R.id.tv_product_details_title)
        tv_product_details_price = findViewById(R.id.tv_product_details_price)
        tv_product_details_description = findViewById(R.id.tv_product_details_description)
        tv_product_details_stock_qtty = findViewById(R.id.tv_product_details_available_quantity)
        tv_product_details_quantity = findViewById(R.id.tv_product_details_quantity)
        btn_add_to_cart = findViewById(R.id.btn_add_to_cart)
        btn_go_to_cart = findViewById(R.id.btn_go_to_cart)
    }

    private fun getProductDetailsFromFirestore(){
        showProgressDialogue("Please Wait...")
        FirestoreClass().getProductDetails(this, mProductID)
    }

    private fun addToCart(){
        // create a cart object
        val addToCartItem = CartItem(
            // the data to be passed to the cart method
        FirestoreClass().getUserCurrentID(),
            mProductID,
            mProductDetails.product_title,
            mProductDetails.product_price,
            mProductDetails.product_image,
            Constant.DEFAULT_CART_QUANTITY
        )
        showProgressDialogue("Please Wait...")
        FirestoreClass().addCartItemToFirestore(this, addToCartItem)
    }





    override fun onClick(view: View?) {
        if (view !=null){
            when(view.id){
                R.id.header_back_arrow_productDetails->{
                    onBackPressed()
                }

                R.id.btn_add_to_cart->{
                    addToCart()
                }

                R.id.btn_go_to_cart->{
                    startActivity(Intent(this, CartListActivity::class.java))
                }

            }
        }
    }
}