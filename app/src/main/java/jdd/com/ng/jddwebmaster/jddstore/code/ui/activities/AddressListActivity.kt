package jdd.com.ng.jddwebmaster.jddstore.code.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jdd.com.ng.jddwebmaster.jddstore.R

class AddressListActivity : BaseActivity(), View.OnClickListener {

    private lateinit var iv_header_back_arrow: ImageView
    private lateinit var tv_add_address: TextView
    private lateinit var tv_no_add_address_found: TextView
    private lateinit var rv_address_list: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addreess_list)

        supportActionBar?.hide()
        initiateVariable()

        iv_header_back_arrow.setOnClickListener(this)
        tv_add_address.setOnClickListener(this)
    }

    private fun initiateVariable() {
        iv_header_back_arrow = findViewById(R.id.iv_header_back_arrow_addressActivity)
        tv_add_address = findViewById(R.id.tv_add_address)
        tv_no_add_address_found = findViewById(R.id.tv_no_address_found)
        rv_address_list = findViewById(R.id.rv_address_list)
    }

    override fun onClick(view: View?) {
        if (view !=null){
            when(view.id){

                R.id.iv_header_back_arrow_addressActivity ->{
                    onBackPressed()
                }

                R.id.tv_add_address->{
                    startActivity(Intent(this, AddAndEditAddressListActivity::class.java))
                }
            }
        }
    }


}