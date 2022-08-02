package jdd.com.ng.jddwebmaster.jddstore.code.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color.RED
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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

            val productAdapter = MyProductListAdapter(requireActivity(), productList, this)
            rv_product_item.adapter = productAdapter
        } else{
            rv_product_item.visibility = View.GONE
            tv_no_item_found.visibility = View.VISIBLE
        }
        }

    fun deleteProduct(productID: String){
       // Toast.makeText(requireActivity(), "You can now delete $productID", Toast.LENGTH_SHORT).show()

        showAlertDialogToDeleteProduct(productID)
    }

    fun deleteProductSuccessfully(){
        dismissProgressDialogue()
        Toast.makeText(requireActivity(), "Product delete successfully...", Toast.LENGTH_SHORT).show()
        getProductListFromFirestore()

    }


    private  fun getProductListFromFirestore(){
        showProgressDialogue("Please Wait...")
        FirestoreClass().getProductDetails(this)
    }


    private fun showAlertDialogToDeleteProduct(productID: String){
        // create an alert dialog
        val builder = AlertDialog.Builder(requireActivity())
        // set the title of the alert dialog
        builder.setTitle("Delete Product")
        // set the alert dialog message
        builder.setMessage("Are you sure you want to delete this product?")
        // set delete icon alert
        // TODO: Change the color of the alert Dialog to RED
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        // setting the positive alert response
        builder.setPositiveButton("Yes"){dialogInterface, _->
            // show progress bar
            showProgressDialogue("Deleting...")
                // call the Firestore method for deleting the product
            FirestoreClass().deleteProduct(this, productID)
            dialogInterface.dismiss()
        }

        // setting the negative response of the alert dialog
        builder.setNegativeButton("No") { dialogInterface, _->
            dialogInterface.dismiss()
        }

        // create the alert dialog
        val alertDialog = builder.create()
        // avoid the user cancelling the alert with positive or negative response
        alertDialog.setCancelable(false)
        // show the alert dialog
        alertDialog.show()
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