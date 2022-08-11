package jdd.com.ng.jddwebmaster.jddstore.code.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.cloud_firestore.FirestoreClass
import jdd.com.ng.jddwebmaster.jddstore.code.model.Address
import jdd.com.ng.jddwebmaster.jddstore.code.model.CartItem
import jdd.com.ng.jddwebmaster.jddstore.code.model.Order
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
    private var mSubTotal: Double = 0.0
    private var mTotalAmount: Double = 0.0

//    // A global variable for the selected address details.
//    private var mAddressDetails: Address? = null
//
//    // A global variable for the product list.
//    private lateinit var mProductsList: ArrayList<Product>
//
//    // A global variable for the cart list.
//    private lateinit var mCartItemsList: ArrayList<CartItem>
//
//    // TODO Step 3: Create a global variables for SubTotal and Total Amount.
//    // START
//    // A global variable for the SubTotal Amount.
//    private var mSubTotal: Double = 0.0
//
//    // A global variable for the Total Amount.
//    private var mTotalAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        supportActionBar?.hide()
        initializeVariable()

        iv_header_back_arrow.setOnClickListener { onBackPressed() }

//        if (intent.hasExtra(Constant.EXTRA_SELECTED_ADDRESS)){
//            mAddressDetails = intent.getParcelableExtra<Address>(Constant.EXTRA_SELECTED_ADDRESS)
//        }
//
//        if (mAddressDetails != null){
//            tv_checkout_address_type.text = mAddressDetails?.type
//            tv_checkout_full_name.text = mAddressDetails?.name
//            tv_checkout_address.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.additional_phoneNumber}"
//            tv_checkout_additional_note.text = mAddressDetails?.additionalNote
//
//            if (mAddressDetails!!.otherDetails.isNotEmpty()){
//                tv_checkout_other_details.text = mAddressDetails!!.otherDetails
//            }
//            tv_mobile_number.text =mAddressDetails?.mobileNumber
//        }

        if (intent.hasExtra(Constant.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails =
                intent.getParcelableExtra<Address>(Constant.EXTRA_SELECTED_ADDRESS)!!
        }

        if (mAddressDetails != null) {
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.additional_phoneNumber}"
            tv_checkout_additional_note.text = mAddressDetails?.additionalNote

            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                tv_checkout_other_details.text = mAddressDetails?.otherDetails
            }
            tv_mobile_number.text = mAddressDetails?.mobileNumber
        }

        getProductList()

        btn_place_order.setOnClickListener {
            placeOrder()
            //TODO: Payment integration -- paystack
        }
    }

    fun successProductListFromFirestore(productsList: ArrayList<Product>){
       // dismissProgressDialog()
        mProductList = productsList
        getCartItemList()

//        mProductsList = productsList
//
//        getCartItemList()
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

        // placing order
        for (item in mCartItemList){
            val availableQuantity = item.stock_quantity.toInt()
            if (availableQuantity > 0){
                val price = item.product_price.toDouble()
                val quantity = item.cart_quantity.toInt()
                // cal the sub total
                mSubTotal += (price*quantity)
            }
        }

        tv_checkout_sub_total.text = "NGN ${mSubTotal}"
        tv_checkout_shipping_charge.text = "NGN 250"

        // if there are no product-- prevent the user from placing an order
        if (mSubTotal >0){
            ll_checkout_place_order.visibility = View.VISIBLE
            mTotalAmount = mSubTotal + 250
            tv_checkout_total_amount.text = "NGN $mTotalAmount"
        } else{
            ll_checkout_place_order.visibility = View.GONE
        }


//        // Hide progress dialog.
//       dismissProgressDialog()
//
//        for (product in mProductsList) {
//            for (cart in cartList) {
//                if (product.product_id == cart.product_id) {
//                    cart.stock_quantity = product.stock_quantity
//                }
//            }
//        }
//
//        mCartItemsList = cartList
//
//        rv_cart_list_items.layoutManager = LinearLayoutManager(this@CheckoutActivity)
//        rv_cart_list_items.setHasFixedSize(true)
//
//        val cartListAdapter = CartItemListAdapter(this@CheckoutActivity, mCartItemsList, false)
//        rv_cart_list_items.adapter = cartListAdapter
//
//        // TODO Step 4: Replace the subTotal and totalAmount variables with the global variables.
//        // START
//        for (item in mCartItemsList) {
//
//            val availableQuantity = item.stock_quantity.toInt()
//
//            if (availableQuantity > 0) {
//                val price = item.product_price.toDouble()
//                val quantity = item.cart_quantity.toInt()
//
//                mSubTotal += (price * quantity)
//            }
//        }
//
//        tv_checkout_sub_total.text = "$$mSubTotal"
//        // Here we have kept Shipping Charge is fixed as $10 but in your case it may cary. Also, it depends on the location and total amount.
//        tv_checkout_shipping_charge.text = "$250.0"
//
//        if (mSubTotal > 0) {
//            ll_checkout_place_order.visibility = View.VISIBLE
//
//            mTotalAmount = mSubTotal + 250.0
//            tv_checkout_total_amount.text = "$$mTotalAmount"
//        } else {
//            ll_checkout_place_order.visibility = View.GONE
//        }

    }

    fun placeOrderSuccessful(){
       FirestoreClass().updateAllCartDetails(this, mCartItemList)
    }

    fun allDetailsUpdatedSuccessfully() {
        dismissProgressDialog()
        //TODO: Implement payment API
        Toast.makeText(this, "Your order was placed successfully...", Toast.LENGTH_SHORT).show()
        // navigate to dashboard activity and clear all layers of activities
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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

    private fun placeOrder(){
        showProgressDialogue(resources.getString(R.string.place_order))
        if (mAddressDetails != null){
            val order = Order(
                FirestoreClass().getUserCurrentID(),
                mCartItemList,
                mAddressDetails!!,
                "My Order ${System.currentTimeMillis()}", //TODO: Change the name display from MyOrder
                mCartItemList[0].product_image,
                mSubTotal.toString(),
                shipping_charge = "NGN 250",
                mTotalAmount.toString()
            )

            FirestoreClass().placeOrder(this, order)
        }

    }


}