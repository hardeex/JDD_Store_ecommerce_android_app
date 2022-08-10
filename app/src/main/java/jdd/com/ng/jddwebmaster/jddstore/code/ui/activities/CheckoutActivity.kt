package jdd.com.ng.jddwebmaster.jddstore.code.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.cloud_firestore.FirestoreClass
import jdd.com.ng.jddwebmaster.jddstore.code.model.Address
import jdd.com.ng.jddwebmaster.jddstore.code.model.CartItem
import jdd.com.ng.jddwebmaster.jddstore.code.model.Product
import jdd.com.ng.jddwebmaster.jddstore.code.ui.adapters.CartItemListAdapter
import jdd.com.ng.jddwebmaster.jddstore.code.utils.Constant

class CheckoutActivity : BaseActivity() {

    private lateinit var iv_header_back_arrow: ImageView
    private lateinit var tv_checkout_title: TextView
    private lateinit var tv_product_items: TextView
    private lateinit var tv_selected_address: TextView
    private lateinit var tv_checkout_address_type: TextView
    private lateinit var tv_checkout_full_name: TextView
    private lateinit var tv_checkout_address: TextView
    private lateinit var tv_checkout_additional_note: TextView
    private lateinit var tv_checkout_other_details: TextView
    private lateinit var tv_mobile_number: TextView
    private lateinit var tv_items_receipt: TextView
    private lateinit var tv_checkout_sub_total: TextView
    private lateinit var tv_checkout_shipping_charge: TextView
    private lateinit var tv_checkout_total_amount: TextView
    private lateinit var tv_payment_mode: TextView
    private lateinit var rv_cart_list_items: RecyclerView
    private lateinit var ll_checkout_address_details: LinearLayout
    private lateinit var ll_checkout_place_order: LinearLayout
    private lateinit var btn_place_order: Button

    private var mAddressDetails: Address? = null
    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartItemList: ArrayList<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        supportActionBar?.hide()
        initializeVariable()

        if (intent.hasExtra(Constant.EXTRA_SELECTED_ADDRESS)){
            mAddressDetails = intent.getParcelableExtra<Address>(Constant.EXTRA_SELECTED_ADDRESS)
        }

        if (mAddressDetails != null){
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.additional_phoneNumber}"
            tv_checkout_additional_note.text = mAddressDetails?.additionalNote

            if (mAddressDetails!!.otherDetails.isNotEmpty()){
                tv_checkout_other_details.text = mAddressDetails!!.otherDetails
            }
            tv_mobile_number.text =mAddressDetails?.mobileNumber
        }

        getProductList()
    }

    fun successProductListFromFirestore(productList: ArrayList<Product>){
       // dismissProgressDialog()
        mProductList = productList
        getCartItemList()
    }

    fun successCartItemList(cartList: ArrayList<CartItem>){
        dismissProgressDialog()

        for (product in mProductList){
            for (cartItem in cartList){
                if (product.product_id == cartItem.product_id){
                    cartItem.stock_quantity = product.stock_quantity
                }

            }
        }
        mCartItemList = cartList
        rv_cart_list_items.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        rv_cart_list_items.setHasFixedSize(true)

        val cartListAdapter = CartItemListAdapter(this@CheckoutActivity, mCartItemList, false)
            rv_cart_list_items.adapter = cartListAdapter

    }

    private fun getProductList(){
        showProgressDialogue(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductList(this@CheckoutActivity)
    }

    private fun getCartItemList(){
        FirestoreClass().getCartList(this@CheckoutActivity)
    }

    private fun initializeVariable() {
        iv_header_back_arrow = findViewById(R.id.iv_header_back_arrow_checkout_activity)
        tv_checkout_title = findViewById(R.id.tv_title_checkout_activity)
        tv_product_items = findViewById(R.id.tv_product_items )
        tv_selected_address =findViewById(R.id.tv_selected_address )
        tv_checkout_address_type =findViewById(R.id.tv_checkout_address_type )
        tv_checkout_full_name =findViewById(R.id.tv_checkout_full_name )
        tv_checkout_address =findViewById(R.id. tv_checkout_address )
        tv_checkout_additional_note =findViewById(R.id. tv_checkout_additional_note )
        tv_checkout_other_details =findViewById(R.id.tv_checkout_other_details )
        tv_mobile_number =findViewById(R.id.tv_checkout_mobile_number )
        tv_items_receipt =findViewById(R.id. tv_items_receipt)
        tv_checkout_sub_total =findViewById(R.id.tv_checkout_sub_total )
        tv_checkout_shipping_charge =findViewById(R.id.tv_checkout_shipping_charge )
        tv_checkout_total_amount =findViewById(R.id.tv_checkout_total_amount )
        tv_payment_mode =findViewById(R.id.tv_payment_mode )
        rv_cart_list_items =findViewById(R.id.rv_cart_list_items )
        ll_checkout_address_details =findViewById(R.id.ll_checkout_address_details )
        ll_checkout_place_order =findViewById(R.id.ll_checkout_place_order )
        btn_place_order =findViewById(R.id.btn_place_order )
    }
}