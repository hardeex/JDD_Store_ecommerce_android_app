package jdd.com.ng.jddwebmaster.jddstore.code.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jdd.com.ng.jddwebmaster.jddstore.R
import jdd.com.ng.jddwebmaster.jddstore.code.cloud_firestore.FirestoreClass
import jdd.com.ng.jddwebmaster.jddstore.code.model.Product
import jdd.com.ng.jddwebmaster.jddstore.code.ui.activities.AddProductActivity
import jdd.com.ng.jddwebmaster.jddstore.code.ui.activities.SettingsActivity
import jdd.com.ng.jddwebmaster.jddstore.code.ui.adapters.MyProductListAdapter
//import jdd.com.ng.jddwebmaster.jddstore.code.myactivities.databinding.FragmentHomeBinding
//import jdd.com.ng.jddwebmaster.jddstore.databinding.FragmentHomeBinding
import jdd.com.ng.jddwebmaster.jddstore.databinding.FragmentProductBinding

class ProductFragment : BaseFragment() {

    private lateinit var rv_product_item: RecyclerView
    private lateinit var tv_no_item_found:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }



    fun successProductListFromFirestore(productList: ArrayList<Product>){
        dismissProgressDialogue()
        if (productList.size > 0){
            rv_product_item.visibility = View.VISIBLE
            tv_no_item_found.visibility = View.GONE
            rv_product_item.layoutManager = LinearLayoutManager(activity)
            rv_product_item.setHasFixedSize(true)
            val productAdapter = MyProductListAdapter(requireActivity(), productList)
            rv_product_item.adapter = productAdapter
        } else{
            rv_product_item.visibility = View.GONE
            tv_no_item_found.visibility = View.VISIBLE
        }
        }


    private  fun getProductListFromFirestore(){
        showProgressDialogue("Please Wait...")
        FirestoreClass().getProductDetails(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_product_item = view.findViewById(R.id.rv_product_item_productFragment)
        tv_no_item_found = view.findViewById<TextView>(R.id.tv_no_item_found_productFragment)
    }

    override fun onResume() {
        super.onResume()
        getProductListFromFirestore()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_product, container, false)
        return root
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // create the method to activate the menu at the top
        inflater.inflate(R.menu.product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.ic_add_product_menu->{
                startActivity(Intent(activity, AddProductActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}