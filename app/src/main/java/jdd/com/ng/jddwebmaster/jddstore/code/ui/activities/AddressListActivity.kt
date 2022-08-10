package jdd.com.ng.jddwebmaster.jddstore.code.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.cloud_firestore.FirestoreClass
import jdd.com.ng.jddwebmaster.jddstore.code.model.Address
import jdd.com.ng.jddwebmaster.jddstore.code.ui.adapters.AddressAdapter
import jdd.com.ng.jddwebmaster.jddstore.code.utils.Constant
import jdd.com.ng.jddwebmaster.jddstore.code.utils.SwipeToDelete
import jdd.com.ng.jddwebmaster.jddstore.code.utils.SwipeToEdit

class AddressListActivity : BaseActivity(), View.OnClickListener {

    private lateinit var iv_header_back_arrow: ImageView
    private lateinit var tv_add_address: TextView
    private lateinit var tv_no_address_found: TextView
    private lateinit var tv_title: TextView
    private lateinit var rv_address_list: RecyclerView

    private var mSelectAddress: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addreess_list)

        supportActionBar?.hide()
        initiateVariable()

        iv_header_back_arrow.setOnClickListener(this)
        tv_add_address.setOnClickListener(this)
        getUserAddressList()

        //TODO: A UI to inform the user, he/she can edit by swiping right and delete swiping left --- alertDialog
        //TODO: specify the context of each THIS context

        if (intent.hasExtra(Constant.EXTRA_SELECT_ADDRESS)){
            mSelectAddress = intent.getBooleanExtra(Constant.EXTRA_SELECT_ADDRESS, false)
        }
        if (mSelectAddress){
            tv_title.text = resources.getString(R.string.select_address)
        }
    }

    fun addressAddSuccessfullyFromFirestore(addressList: ArrayList<Address>){
        dismissProgressDialog()
        if (addressList.size > 0) {

            rv_address_list.visibility = View.VISIBLE
            tv_no_address_found.visibility = View.GONE

            rv_address_list.layoutManager = LinearLayoutManager(this)
            rv_address_list.setHasFixedSize(true)

            val addressAdapter = AddressAdapter(this, addressList, mSelectAddress)
            rv_address_list.adapter = addressAdapter

           if (!mSelectAddress){
               // set the swipe to delete functionalities
               val editSwipeHandler = object : SwipeToEdit(this) {
                   override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                       val adapter = rv_address_list.adapter as AddressAdapter
                       adapter.notifyEditItem(
                           this@AddressListActivity,
                           viewHolder.adapterPosition
                       )
                   }
               }

               val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
               // add the ditItemTouchHelper to recycler view
               editItemTouchHelper.attachToRecyclerView(rv_address_list)

               val deleteSwipeHandler = object : SwipeToDelete(this) {
                   override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                       showProgressDialogue(resources.getString(R.string.delete))
                       //TODO: alertDialog to inform the user on deleting an address
                       FirestoreClass().deleteUserAddress(this@AddressListActivity, addressList[viewHolder.adapterPosition].id)
                   }
               }

               val deleteTouchHelper = ItemTouchHelper(deleteSwipeHandler)
               deleteTouchHelper.attachToRecyclerView(rv_address_list)
           }

        } else {
            rv_address_list.visibility = View.GONE
            tv_no_address_found.visibility = View.VISIBLE
        }
    }

    fun deleteUserAddressSuccessfully(){
        dismissProgressDialog()
        Toast.makeText(this, resources.getString(R.string.address_delete_successfully), Toast.LENGTH_SHORT).show()
        getUserAddressList()
    }

    private fun getUserAddressList(){
        showProgressDialogue(resources.getString(R.string.please_wait))
        FirestoreClass().getUserAddressList(this)
    }

    private fun initiateVariable() {
        iv_header_back_arrow = findViewById(R.id.iv_header_back_arrow_addressActivity)
        tv_add_address = findViewById(R.id.tv_add_address)
        tv_no_address_found = findViewById(R.id.tv_no_address_found)
        rv_address_list = findViewById(R.id.rv_address_list)
        tv_title = findViewById(R.id.tv_title_addressList_activity)
    }

//    override fun onResume() {
//        super.onResume()
//        getUserAddressList()
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            getUserAddressList()
        }
    }

    override fun onClick(view: View?) {
        if (view !=null){
            when(view.id){

                R.id.iv_header_back_arrow_addressActivity ->{
                    onBackPressed()
                }

                R.id.tv_add_address->{
                   val intent = Intent(this, AddAndEditAddressListActivity::class.java)
                    startActivityForResult(intent, Constant.ADD_ADDRESS_REQUEST_CODE)
                }
            }
        }
    }


}