package jdd.com.ng.jddwebmaster.jddstore.code.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.cloud_firestore.FirestoreClass
import jdd.com.ng.jddwebmaster.jddstore.code.model.CartItem
import jdd.com.ng.jddwebmaster.jddstore.code.model.Product
import jdd.com.ng.jddwebmaster.jddstore.code.ui.adapters.CartItemListAdapter
import jdd.com.ng.jddwebmaster.jddstore.code.utils.Constant

class  CartListActivity : BaseActivity(), View.OnClickListener {

    private lateinit var btn_header_back_arrow: ImageView
    private lateinit var rv_cart_list_activity: RecyclerView
    private lateinit var tv_no_item_found_cart_list_activity: TextView
    private lateinit var tv_subTotal_value_cart_list: TextView
    private lateinit var tv_shipping_charge_value_cart_list: TextView
    private lateinit var tv_total_amount_value_cart_list: TextView
    private lateinit var btn_check_out_cart_list_activity: Button
    private lateinit var ll_check_out_cart_activity: LinearLayout


    // create a global variable for the product list
    private lateinit var mProductList: ArrayList<Product>
    // create a global variable for the cart item
    private lateinit var mCartListItem: ArrayList<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)

        // TODO: change the screen orientation of each activity from the manifest
        // TODO: change textSize of each button to btnTextSize

        supportActionBar?.hide()
        initializeVariable()
        btn_header_back_arrow.setOnClickListener(this)
        btn_check_out_cart_list_activity.setOnClickListener(this)
    }

    private fun initializeVariable() {
        btn_header_back_arrow = findViewById(R.id.header_back_arrow_cartList_activity)
        rv_cart_list_activity = findViewById(R.id.rv_cart_items_list)
        tv_no_item_found_cart_list_activity = findViewById(R.id.tv_no_cart_item_found)
        tv_subTotal_value_cart_list = findViewById(R.id.tv_sub_total)
        tv_shipping_charge_value_cart_list = findViewById(R.id.tv_shipping_charge)
        tv_total_amount_value_cart_list = findViewById(R.id.tv_total_amount)
        btn_check_out_cart_list_activity = findViewById(R.id.btn_checkout)
        ll_check_out_cart_activity = findViewById(R.id.ll_checkout)
    }

    fun successfullyGetCartItemList(cartList: ArrayList<CartItem>){
        dismissProgressDialog()

//        for (i in carList){
//            Log.i("Cart Item Title: ", i.product_title)
//        }

        // determine the number of available product in the stock
        for (product in mProductList){
            for (cartItem in cartList){
                if (product.product_id == cartItem.product_id){
                    cartItem.stock_quantity = product.stock_quantity
                    if (product.stock_quantity.toInt() == 0){
                        cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }

        }

        // initialize the global variable
        mCartListItem = cartList


        // check if there is item(s) in the cart
       if (mCartListItem.size > 0){
           // make the recycler view visible
           rv_cart_list_activity.visibility = View.VISIBLE
           ll_check_out_cart_activity.visibility = View.VISIBLE
           tv_no_item_found_cart_list_activity.visibility = View.GONE

           // set the layout manager for the recycler view
           rv_cart_list_activity.layoutManager = LinearLayoutManager(this)
           rv_cart_list_activity.setHasFixedSize(true)

           // assign adapter to the recycler view
           val cartItemListAdapter = CartItemListAdapter(this, mCartListItem, true)
           rv_cart_list_activity.adapter = cartItemListAdapter

           // set the sub-total amount to double
           var subTotal: Double = 0.0

           // create a for loop that runs through the price and calculate amount of order and price
           for (item in mCartListItem){
               // check for the available product quantity
               val availableQuantity = item.stock_quantity.toInt()
               // check if the available quantity is > 0
               if(availableQuantity > 0){
                   // convert the price to double
                   val price = item.product_price.toDouble()
                   // convert the quantity order to Int]
                   val quantity = item.cart_quantity.toInt()
                   // calculate the sub total
                   subTotal += (price * quantity)
               }

           }
           // assign the subTotal value to the sub_total text
           tv_subTotal_value_cart_list.text ="NGN ${subTotal}"
           // TODO: write the logic for the shipping price
           // set a fixed cost for the shipping cost for now
           tv_shipping_charge_value_cart_list.text = "NGN 250" // TODO: change the shipping charge logic

           if (subTotal > 0){
               ll_check_out_cart_activity.visibility = View.VISIBLE
               val total = subTotal + 250 // TODO: change the logic for the shipping price
               tv_total_amount_value_cart_list.text = "NGN ${total}"
           } else{
               ll_check_out_cart_activity.visibility = View.GONE
           }
       } else{
           rv_cart_list_activity.visibility = View.GONE
           ll_check_out_cart_activity.visibility = View.GONE
           tv_no_item_found_cart_list_activity.visibility = View.VISIBLE
       }
    }

    fun successfullyGetProductListFromFirestore(productList: ArrayList<Product>){
        dismissProgressDialog()
        mProductList = productList
        getCartItemList()
    }

    fun itemRemovedSuccess(){
        dismissProgressDialog()
        Toast.makeText(this, "Item removed successfully...", Toast.LENGTH_SHORT).show()
        getCartItemList()
    }

    fun itemUpdateSuccessfully(){
        dismissProgressDialog()
        getCartItemList()
    }

    private fun getCartItemList(){
        //showProgressDialogue("Please Wait...")
        FirestoreClass().getCartList(this)
    }

    private fun getProductList(){
       showProgressDialogue("Please Wait...")
        FirestoreClass().getAllProductList(this)
    }

    // TODO: create content description for each layout
    // TODO: change the required text and hint to tool:text in layouts --- find an API that will tell what shipping price for each product is?

    override fun onResume() {
        super.onResume()
       // getCartItemList()
        getProductList()
    }

    override fun onClick(view: View?) {
        if (view != null){
            when(view.id){

                R.id.header_back_arrow_cartList_activity->{
                    onBackPressed()
                }

                R.id.btn_checkout->{
                    val intent = Intent(this, AddressListActivity::class.java)
                    intent.putExtra(Constant.EXTRA_SELECT_ADDRESS, true)
                    startActivity(intent)

                }
            }
        }
    }
}