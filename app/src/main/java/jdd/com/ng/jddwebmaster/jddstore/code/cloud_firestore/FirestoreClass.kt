package jdd.com.ng.jddwebmaster.jddstore.code.cloud_firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import jdd.com.ng.jddwebmaster.jddstore.code.model.Address
import jdd.com.ng.jddwebmaster.jddstore.code.model.CartItem
import jdd.com.ng.jddwebmaster.jddstore.code.model.Product
import jdd.com.ng.jddwebmaster.jddstore.code.model.User
import jdd.com.ng.jddwebmaster.jddstore.code.ui.activities.*
import jdd.com.ng.jddwebmaster.jddstore.code.ui.fragment.DashboardFragment
import jdd.com.ng.jddwebmaster.jddstore.code.ui.fragment.ProductFragment
import jdd.com.ng.jddwebmaster.jddstore.code.utils.Constant
import jdd.com.ng.jddwebmaster.jddstore.code.utils.Constant.USER_ID


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
                    activity.dismissProgressDialog()
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
                              activity.dismissProgressDialog()
                         }

                         is SettingsActivity->{
                              activity.dismissProgressDialog()
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
                              activity.dismissProgressDialog()
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
                              activity.dismissProgressDialog()
                         }

                         is AddProductActivity->{
                              activity.dismissProgressDialog()
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
                    activity.dismissProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error!!! uploading product to the Firestore", error)
               }

     }

     fun getProductDetails(activity: ProductDetailsActivity, productID: String){
          mFirestore.collection(Constant.PRODUCT).document(productID).get()

               .addOnSuccessListener { document->
                    Log.i(activity.javaClass.simpleName, document.toString())
                    // since we have gotten the product using the .get()--- let's create an object of the product using the document
                    val product = document.toObject(Product::class.java)!!
                    activity.getProductDetailsSuccessful(product)
               }


               .addOnFailureListener { error->
                    activity.dismissProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error!!! getting product details from the Firestore", error)
               }
     }

     fun getProductDetails(fragment: Fragment){
          mFirestore.collection(Constant.PRODUCT).whereEqualTo(USER_ID, getUserCurrentID()).get()

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
                    Log.e(fragment.javaClass.simpleName, "Error!!! Getting product lists from the cloud", error)
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

     fun addCartItemToFirestore(activity: ProductDetailsActivity, addToCart: CartItem){
          mFirestore.collection(Constant.CART_ITEM).document().set(addToCart, SetOptions.merge())

               .addOnSuccessListener {
                    activity.addToCartSuccess()
               }

               .addOnFailureListener { error->
                    activity.dismissProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error!!! creating item in the cart", error)
               }
     }

     fun checkIfItemExistsInCart(activity: ProductDetailsActivity, product_id: String){
          mFirestore.collection(Constant.CART_ITEM).whereEqualTo(Constant.USER_ID, getUserCurrentID())
               .whereEqualTo(Constant.PRODUCT_ID, product_id).get()

               .addOnSuccessListener {document->
                    Log.i(activity.javaClass.simpleName, document.documents.toString())
                    if (document.documents.size > 0){
                         activity.productExistInCart()
                    } else{
                         activity.dismissProgressDialog()
                    }

               }

               .addOnFailureListener {  error->
                    activity.dismissProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error!!! checking if cart exists in the cart", error)
               }
     }

     fun getCartList(activity: Activity){
          // get each user cart item
          mFirestore.collection(Constant.CART_ITEM).whereEqualTo(Constant.USER_ID, getUserCurrentID()).get()

               .addOnSuccessListener { document->
                    Log.i(activity.javaClass.simpleName, document.documents.toString())
                    // get a list of cart items
                    val list: ArrayList<CartItem> = ArrayList()
                    // loop through all of the document
                    for (i in document){
                         // convert each element/document in the loop to an object-- that is, cartItems object
                         val cartItem = i.toObject(CartItem::class.java)
                         cartItem.id = i.id
                         list.add(cartItem)
                    }

                    when(activity){
                         is CartListActivity->{
                              activity.successfullyGetCartItemList(list)
                         }
                    }
               }

               .addOnFailureListener { error->
                    Log.e(activity.javaClass.simpleName, "Error!!!getting cart list items", error)
                    when(activity){
                         is CartListActivity->{
                              activity.dismissProgressDialog()
                         }
                    }
               }
     }

     fun getAllProductList(activity: CartListActivity){
          mFirestore.collection(Constant.PRODUCT).get()

               .addOnSuccessListener { document->
                    Log.i(activity.javaClass.simpleName, document.documents.toString())
                    // create an array list of product
                    val productList: ArrayList<Product> = ArrayList()
                    // run a for loop through the document to get the product list
                    for (i in document){
                         val product = i.toObject(Product::class.java)
                         product.product_id = i.id
                         productList.add(product)
                    }
                    activity.successfullyGetProductListFromFirestore(productList)
               }

               .addOnFailureListener { error->
                    activity.dismissProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error.. getting all product list", error)
               }
     }

     fun removeItemFromCart(context: Context, cart_id: String){
          mFirestore.collection(Constant.CART_ITEM).document(cart_id).delete()

               .addOnSuccessListener {
                    when(context){
                         is CartListActivity->{
                              context.itemRemovedSuccess()
                         }
                    }
               }
               .addOnFailureListener {error->
                    when(context){
                         is CartListActivity->{
                              context.dismissProgressDialog()
                         }
                    }
                    Log.e(context.javaClass.simpleName, "Error.. removing item from cart item list", error)
               }
     }

     fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>){
          mFirestore.collection(Constant.CART_ITEM).document(cart_id).update(itemHashMap)

               .addOnSuccessListener {
                    when(context){
                         is CartListActivity->{
                              context.itemUpdateSuccessfully()
                         }
                    }
               }

               .addOnFailureListener {error->
                    when(context){
                         is CartListActivity->{
                              context.dismissProgressDialog()
                         }
                    }
                    Log.e(context.javaClass.simpleName, "Error.. updating cart item", error)
               }
     }

     fun adduserAddresses(activity: AddAndEditAddressListActivity, addressInfo: Address){
          mFirestore.collection(Constant.ADDRESSESS).document().set(addressInfo, SetOptions.merge())

               .addOnSuccessListener {
                    activity.addUserAddressSuccessfully()
               }

               .addOnFailureListener { error->
                    Log.e(activity.javaClass.simpleName, "Error.. updating user address", error)
               }
     }



}