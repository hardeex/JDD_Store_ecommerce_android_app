package jdd.com.ng.jddwebmaster.jddstore.code.cloud_firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import jdd.com.ng.jddwebmaster.jddstore.code.model.Product
import jdd.com.ng.jddwebmaster.jddstore.code.model.User
import jdd.com.ng.jddwebmaster.jddstore.code.ui.activities.*
import jdd.com.ng.jddwebmaster.jddstore.code.ui.fragment.DashboardFragment
import jdd.com.ng.jddwebmaster.jddstore.code.ui.fragment.ProductFragment
import jdd.com.ng.jddwebmaster.jddstore.code.utils.Constant
import java.sql.Struct


class FirestoreClass {
/*
I'm using Cloud Firestore to enable the views of every individual unique
The first step is to import the Firebase-Firestore and create an instance of Firestore
 */
     private val mFirestore = FirebaseFirestore.getInstance()

     fun uploadUserDetails(activity: RegisterActivity, user:User){
          // This method is to some of the user data to the Google Firestore
          mFirestore.collection(Constant.SUBSCRIBERS).document(user.id).set(user, SetOptions.merge())

               .addOnSuccessListener {
                    activity.registerUserSuccessfully()
               }

               .addOnFailureListener {
                    activity.dismissProgressDialogue()
               }
     }

      fun getUserCurrentID(): String {
          // This method is to get user ID from the Google Authentication module
          val currentUser = FirebaseAuth.getInstance().currentUser
          var currentUserID = ""
          if (currentUser != null) {
               currentUserID = currentUser.uid
          }
          return currentUserID
     }

     fun getUserDetails(activity: Activity){
          // This method is to retrieve the user details oe info from the Google Firestore and display unique info the each user based on the activity
          mFirestore.collection(Constant.SUBSCRIBERS).document(getUserCurrentID()).get()

               .addOnSuccessListener { document->
                    Log.i(activity.javaClass.simpleName, document.toString())
                    // get the document and convert it to USER object
                    val user = document.toObject(User::class.java)!!


                    // create a sharedPreference variable to avoid accessing Firestore everytime the user login
                    val sharedPreference = activity.getSharedPreferences(Constant.JDD_SHARED_PREFERENCE, Context.MODE_PRIVATE)
                    // create an editor for the sharedPreference
                    val editor: SharedPreferences.Editor = sharedPreference.edit()
                    // pass the user data to the editor
                    editor.putString(Constant.LOGGED_IN_USER, "${user.user_firstName} ${user.user_lastName} ")
                    editor.apply()


                    when(activity){
                         is LoginActivity ->{
                              activity.userLoggedInSuccessfully(user)
                         }

                         is SettingsActivity->{
                              activity.getUserDetailsSuccessfully(user)
                         }
                    }
               }

               .addOnFailureListener { error->
                    when(activity){
                         is LoginActivity ->{
                              activity.dismissProgressDialogue()
                         }

                         is SettingsActivity->{
                              activity.dismissProgressDialogue()
                         }
                    }

                    Log.e(activity.javaClass.simpleName, "Error getting user details", error)
               }
     }

     fun updateUserProfileDetails(activity: Activity, userHashMap: HashMap<String, Any>){
          mFirestore.collection(Constant.SUBSCRIBERS).document(getUserCurrentID()).update(userHashMap)

               .addOnSuccessListener {
                    when(activity){
                         is UserProfileCompleteActivity ->{
                            activity.userProfileUpdatedInFirestoreSuccessfully()
               }


               }
                    Log.i(activity.javaClass.simpleName, "User Details updated successfully")
               }


               .addOnFailureListener { error->
                    when(activity){
                         is UserProfileCompleteActivity ->{
                              activity.dismissProgressDialogue()
                         }
                    }
                    Log.e(activity.javaClass.simpleName, "Error, updating user datails", error)
               }
     }

     fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String){
          // get the storage reference
          val storageReference = FirebaseStorage.getInstance().reference
               .child(imageType + System.currentTimeMillis() + "."
                       + Constant.getFileExtension(activity, imageFileURI))

          storageReference.putFile(imageFileURI!!).addOnSuccessListener { taskSnapShot->
               Log.i("Firebase Image Uri", taskSnapShot.metadata?.reference?.downloadUrl.toString())

               // get the download uri from the task snapShot
               taskSnapShot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener {uri->
                         Log.i("Download image URL ", uri.toString())
                         when(activity){
                              is UserProfileCompleteActivity ->{
                                   activity.imageUploadToCloudStorageSuccessfully(uri.toString())
                              }

                              is AddProductActivity->{
                                   activity.productImageUploadedSuccessfully(imageFileURI.toString())
                              }

                         }
                    }
          }

               .addOnFailureListener{exception->
                    when(activity){
                         is UserProfileCompleteActivity ->{
                              activity.dismissProgressDialogue()
                         }

                         is AddProductActivity->{
                              activity.dismissProgressDialogue()
                         }
                    }
                    Log.e(activity.javaClass.simpleName, exception.message, exception)
               }

     }

     fun uploadProductDetails(activity: AddProductActivity, productDetails: Product){
          mFirestore.collection(Constant.PRODUCT).document().set(productDetails, SetOptions.merge())

               .addOnSuccessListener {
                    activity.productUploadSuccessfully()
               }

               .addOnFailureListener { error->
                    activity.dismissProgressDialogue()
                    Log.e(activity.javaClass.simpleName, "Error!!! uploading product to the Firestore", error)
               }

     }

     fun getProductDetails(fragment: Fragment){
          mFirestore.collection(Constant.PRODUCT).whereEqualTo(Constant.USER_ID, getUserCurrentID()).get()

               .addOnSuccessListener {document->
                    Log.i("Product Details", document.documents.toString())
                    val productList: ArrayList<Product> = ArrayList()
                    // check all the product list in the document
                    for (i in document.documents){
                         // create a product object variable
                         val product = i.toObject(Product::class.java)
                         // the product ID
                         if (product != null) {
                              product.product_id = i.id
                         }
                         if (product != null) {
                              productList.add(product)
                         }
                    }

                    when(fragment){
                         is ProductFragment->{
                              fragment.successProductListFromFirestore(productList)
                         }
                    }
               }
               .addOnFailureListener { error->
                    Log.i(fragment.javaClass.simpleName, "Error!!! Getting product lists from the cloud", error)
               }
     }

     fun getDashboardItemList(fragment: DashboardFragment){
          mFirestore.collection(Constant.PRODUCT).get()

               .addOnSuccessListener { document->
                    Log.i(fragment.javaClass.simpleName, document.documents.toString())
                    // create an array of productList
                    val productList:ArrayList<Product> = ArrayList()

                    // check all the product list in the document
                    for (i in document.documents){
                         // create a product object variable
                         val product = i.toObject(Product::class.java)!!
                         // the product ID
                         product.product_id = i.id
                         productList.add(product)
                    }
                    fragment.successDashboardItemListFromFirestore(productList)
               }
               .addOnFailureListener { error->
                    fragment.dismissProgressDialogue()
                    Log.e(fragment.javaClass.simpleName, "Error!!! Getting dashboard product item list...", error)
               }
     }

     fun deleteProduct(fragment: ProductFragment, productID: String){
          mFirestore.collection(Constant.PRODUCT).document(productID).delete()

               .addOnSuccessListener {
                    fragment.deleteProductSuccessfully()
               }


               .addOnFailureListener {error->
                    fragment.dismissProgressDialogue()
                    Log.e(fragment.requireActivity().javaClass.simpleName, "There was an error deleting product" , error)
               }
     }

}