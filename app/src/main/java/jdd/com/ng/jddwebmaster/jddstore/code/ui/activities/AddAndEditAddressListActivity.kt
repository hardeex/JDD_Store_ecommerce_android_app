package jdd.com.ng.jddwebmaster.jddstore.code.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.google.android.material.textfield.TextInputLayout
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.cloud_firestore.FirestoreClass
import jdd.com.ng.jddwebmaster.jddstore.code.model.Address
import jdd.com.ng.jddwebmaster.jddstore.code.utils.Constant

class AddAndEditAddressListActivity :BaseActivity(), View.OnClickListener {

    private lateinit var iv_header_back_arrow_addAndEditAddress: ImageView
    private lateinit var et_full_name: TextInputLayout
    private lateinit var et_phone_number: TextInputLayout
    private lateinit var et_address: TextInputLayout
    private lateinit var et_zip_code: TextInputLayout
    private lateinit var et_additional_note: TextInputLayout
    private lateinit var til_other_details: TextInputLayout
    private lateinit var et_other_details: TextInputLayout
    private lateinit var rg_type: RadioGroup
    private lateinit var rb_home: RadioButton
    private lateinit var rb_office: RadioButton
    private lateinit var rb_others: RadioButton
    private lateinit var btn_save_addAndEditAddress: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_and_edit_address_list)

        supportActionBar?.hide()
        initiateVariable()

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
        Toast.makeText(this, "Your address is updated successfully...", Toast.LENGTH_SHORT).show()
    }
    private fun initiateVariable() {
        iv_header_back_arrow_addAndEditAddress = findViewById(R.id.iv_header_back_arrow_addAndEditAddressActivity)
        et_full_name = findViewById(R.id.et_full_name)
        et_phone_number = findViewById(R.id.et_phone_number)
        et_address = findViewById(R.id.tv_add_address)
        et_zip_code = findViewById(R.id.et_zip_code)
        et_additional_note = findViewById(R.id.et_additional_note)
        til_other_details = findViewById(R.id.til_other_details)
        et_other_details = findViewById(R.id.et_other_details)
        rb_home = findViewById(R.id.rb_home)
        rb_office = findViewById(R.id.rb_office)
        rb_others = findViewById(R.id.rb_other)
        rg_type = findViewById(R.id.rg_type)
        btn_save_addAndEditAddress = findViewById(R.id.btn_submit_address)
    }

    private fun validateData(): Boolean {
        return when {

            TextUtils.isEmpty(et_full_name.editText?.text.toString().trim { it <= ' ' }) -> {
               et_full_name.error = "Please enter your full name"
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

            TextUtils.isEmpty(et_zip_code.editText?.text.toString().trim { it <= ' ' }) -> {
                et_zip_code.error = "Please enter your zip code"
                false
            }

            rb_others.isChecked && TextUtils.isEmpty(
                et_zip_code.editText?.text.toString().trim { it <= ' ' }) -> {
                et_zip_code.error = "Please enter your zip code"
                false
            }
            else -> {
                true
            }
        }
    }

    private fun saveAddressToFirestore(){
        val fullName: String = et_full_name.editText?.text.toString().trim { it <= ' ' }
        val phoneNumber: String = et_phone_number.editText?.text.toString().trim { it <= ' ' }
        val address: String = et_address.editText?.text.toString().trim { it <= ' ' }
        val zipCode: String = et_zip_code.editText?.text.toString().trim { it <= ' ' }
        val additionalNote: String = et_additional_note.editText?.text.toString().trim { it <= ' ' }
        val otherDetails: String = et_other_details.editText?.text.toString().trim { it <= ' ' }

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

           FirestoreClass().adduserAddresses(this, addressModel)

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