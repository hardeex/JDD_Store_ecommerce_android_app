package jdd.com.ng.jddwebmaster.jddstore.code.ui.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import jdd.com.ng.jddwebmaster.jddstore.R


 open class BaseFragment : Fragment() {
    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    fun showProgressDialogue(message: String){
        mProgressDialog = Dialog(requireActivity())
        //  val inflate = (this as Activity).layoutInflater.inflate(R.layout.progress_dialogue, null)
        val view = LayoutInflater.from(requireActivity()).inflate(R.layout.progress_dialogue, null)
        view.findViewById<TextView>(R.id.tv_progress_dialogue).text = message
        mProgressDialog.setContentView(view)
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }

    fun dismissProgressDialogue(){
        mProgressDialog.dismiss()
    }
}