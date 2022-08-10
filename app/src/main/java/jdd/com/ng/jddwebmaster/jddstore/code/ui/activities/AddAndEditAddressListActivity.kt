package jdd.com.ng.jddwebmaster.jddstore.code.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.google.android.material.textfield.TextInputLayout
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.cloud_firestore.FirestoreClass
import jdd.com.ng.jddwebmaster.jddstore.code.model.Address
import jdd.com.ng.jddwebmaster.jddstore.code.utils.Constant

class AddAndEditAddressListActivity :BaseActivity(), View.OnClickListener {

    //private lateinit var toolBar_title_addressActivity: Toolbar
    private lateinit var iv_header_back_arrow_addAndEditAddress: ImageView
    private lateinit var et_full_name_address: TextInputLayout
    private lateinit var et_phone_number: TextInputLayout
    private lateinit var et_address: TextInputLayout
    private lateinit var et_additional_phoneNumber: TextInputLayout
    private lateinit var et_additional_note: TextInputLayout
    private lateinit var til_other_details: TextInputLayout
    private lateinit var rg_type: RadioGroup
    private lateinit var rb_home: RadioButton
    private lateinit var rb_office: RadioButton
    private lateinit var rb_others: RadioButton
    private lateinit var btn_save_addAndEditAddress: Button
    private lateinit var tv_title: TextView
    private lateinit var et_other_details: EditText

    // create a global variable for the address details
    private var mAddressDetails: Address? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_and_edit_address_list)

        initiateVariable()
        supportActionBar?.hide()

        if (intent.hasExtra(Constant.EXTRA_ADDRESS_DETAILS)){
            mAddressDetails = intent.getParcelableExtra(Constant.EXTRA_ADDRESS_DETAILS)
        }

        if (mAddressDetails !=null){
            if (mAddressDetails!!.id.isNotEmpty()){
                tv_title.text = resources.getString(R.string.title_edit_address)
                btn_save_addAndEditAddress.text = resources.getString(R.string.btn_lbl_update)

                et_full_name_address.editText!!.setText(mAddressDetails?.name)
                et_phone_number.editText!!.setText(mAddressDetails?.mobileNumber)
                et_address.editText!!.setText(mAddressDetails?.address)
                et_additional_phoneNumber.editText!!.setText(mAddressDetails?.additional_phoneNumber)
                et_additional_note.editText!!.setText(mAddressDetails?.additionalNote)

                when (mAddressDetails?.type) {
                    Constant.HOME -> {
                        rb_home.isChecked = true
                    }
                    Constant.OFFICE -> {
                        rb_office.isChecked = true
                    }
                    else -> {
                        rb_others.isChecked = true
                        til_other_details.visibility = View.VISIBLE
                        et_other_details.setText(mAddressDetails?.otherDetails)
                    }
                }
            }
        }




        iv_header_back_arrow_addAndEditAddress.setOnClickListener(this)
        btn_save_addAndEditAddress.setOnClickListener(this)

        rg_type.setOnCheckedChangeListener{_, checkedId ->
            if (checkedId == R.id.rb_other){
                til_other_details.visibility = View.VISIBLE
            } else{
                til_other_details.visibility = View.GONE
            }
        }
    }

    fun addUserAddressSuccessfully(){
        dismissProgressDialog()
        // change the toast message depending if an address is updated or added
        val notifySuccessMessage: String = if (mAddressDetails !=null && mAddressDetails!!.id.isNotEmpty()){
            resources.getString(R.string.address_update)
        }else{
            resources.getString(R.string.address_addedd)
        }
        Toast.makeText(this, notifySuccessMessage, Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }
    private fun initiateVariable() {
       // toolBar_title_addressActivity = findViewById(R.id.toolBar_title_addressActivity)
        iv_header_back_arrow_addAndEditAddress = findViewById(R.id.iv_header_back_arrow_addAndEditAddressActivity)
        et_full_name_address = findViewById(R.id.til_full_name)
        et_phone_number = findViewById(R.id.til_phone_number)
        et_address = findViewById(R.id.til_address)
        et_additional_phoneNumber = findViewById(R.id.til_additional_phone_number)
        et_additional_note = findViewById(R.id.til_additional_note)
        til_other_details = findViewById(R.id.til_other_details)
        tv_title = findViewById(R.id.tv_title_EditAddressActivity)
        et_other_details = findViewById(R.id.et_other_details)
        rb_home = findViewById(R.id.rb_home)
        rb_office = findViewById(R.id.rb_office)
        rb_others = findViewById(R.id.rb_other)
        rg_type = findViewById(R.id.rg_type)
        btn_save_addAndEditAddress = findViewById(R.id.btn_submit_address)
    }

    private fun validateData(): Boolean {
        return when {

            TextUtils.isEmpty(et_full_name_address.editText?.text.toString().trim { it <= ' ' }) -> {
               et_full_name_address.error = "Please enter your full name"
                false
            }

            TextUtils.isEmpty(et_phone_number.editText?.text.toString().trim { it <= ' ' }) -> {
                et_phone_number.error = "Please enter your mobile number"
                false
            }

            TextUtils.isEmpty(et_address.editText?.text.toString().trim { it <= ' ' }) -> {
                et_address.error = "Please enter your address"
                false
            }


//            TextUtils.isEmpty(et_additional_phoneNumber.editText?.text.toString().trim { it <= ' ' }) -> {
//                et_additional_phoneNumber.error = "Please enter an additional mobile number"
//                false
//            }

            rb_others.isChecked && TextUtils.isEmpty(
                til_other_details.editText?.text.toString().trim { it <= ' ' }) -> {
                til_other_details.error = "Please the address header"
                false
            }
            else -> {
                true
            }
        }
    }

    private fun saveAddressToFirestore(){
        val fullName: String = et_full_name_address.editText?.text.toString().trim { it <= ' ' }
        val phoneNumber: String = et_phone_number.editText?.text.toString().trim { it <= ' ' }
        val address: String = et_address.editText?.text.toString().trim { it <= ' ' }
        val zipCode: String = et_additional_phoneNumber.editText?.text.toString().trim { it <= ' ' }
        val additionalNote: String = et_additional_note.editText?.text.toString().trim { it <= ' ' }
        val otherDetails: String = til_other_details.editText?.text.toString().trim { it <= ' ' }

        if (validateData()){
            showProgressDialogue("PleaseWait...")
            val addressTypes: String = when{
                rb_home.isChecked ->{
                    Constant.HOME
                }

                rb_office.isChecked->{
                    Constant.OFFICE
                }

                else->{
                    Constant.OTHER
                }
                
            }

            val addressModel = Address(
                FirestoreClass().getUserCurrentID(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressTypes,
                otherDetails
            )
            // check if the user is updating or editing an address
            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()){
                // the user is updating an address
                FirestoreClass().updateAddress(this, addressModel,mAddressDetails!!.id)
            } else{
                FirestoreClass().adduserAddresses(this, addressModel)
            }


        }
    }
    override fun onClick(view: View?) {
        if(view !=null){
            when(view.id){
                R.id.iv_header_back_arrow_addAndEditAddressActivity->{
                    onBackPressed()
                }

                R.id.btn_submit_address ->{
                    saveAddressToFirestore()
                }

            }
        }

    }
}