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
import jdd.com.ng.jddwebmaster.jddstore.code.model.*
import jdd.com.ng.jddwebmaster.jddstore.code.ui.activities.*
import jdd.com.ng.jddwebmaster.jddstore.code.ui.fragment.DashboardFragment
import jdd.com.ng.jddwebmaster.jddstore.code.ui.fragment.OrderFragment
import jdd.com.ng.jddwebmaster.jddstore.code.ui.fragment.ProductFragment
import jdd.com.ng.jddwebmaster.jddstore.code.utils.Constant
import jdd.com.ng.jddwebmaster.jddstore.code.utils.Constant.USER_ID


class FirestoreClass {
/*
I'm using Cloud Firestore to enable the views of every individual unique
The first step is to import the Firebase-Firestore and create an instance of Firestore
 */
     private val mFireStore = FirebaseFirestore.getInstance()

     fun uploadUserDetails(activity: RegisterActivity, user:User){
          // This method is to some of the user data to the Google Firestore
          mFireStore.collection(Constant.SUBSCRIBERS).document(user.id).set(user, SetOptions.merge())

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
          mFireStore.collection(Constant.SUBSCRIBERS).document(getUserCurrentID()).get()

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
          mFireStore.collection(Constant.SUBSCRIBERS).document(getUserCurrentID()).update(userHashMap)

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
          mFireStore.collection(Constant.PRODUCT).document().set(productDetails, SetOptions.merge())

               .addOnSuccessListener {
                    activity.productUploadSuccessfully()
               }

               .addOnFailureListener { error->
                    activity.dismissProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error!!! uploading product to the Firestore", error)
               }

     }

     fun getProductDetails(activity: ProductDetailsActivity, productID: String){
          mFireStore.collection(Constant.PRODUCT).document(productID).get()

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
          mFireStore.collection(Constant.PRODUCT).whereEqualTo(USER_ID, getUserCurrentID()).get()

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
          mFireStore.collection(Constant.PRODUCT).get()

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
          mFireStore.collection(Constant.PRODUCT).document(productID).delete()

               .addOnSuccessListener {
                    fragment.deleteProductSuccessfully()
               }


               .addOnFailureListener {error->
                    fragment.dismissProgressDialogue()
                    Log.e(fragment.requireActivity().javaClass.simpleName, "There was an error deleting product" , error)
               }
     }

     fun addCartItemToFirestore(activity: ProductDetailsActivity, addToCart: CartItem){
          mFireStore.collection(Constant.CART_ITEM).document().set(addToCart, SetOptions.merge())

               .addOnSuccessListener {
                    activity.addToCartSuccess()
               }

               .addOnFailureListener { error->
                    activity.dismissProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error!!! creating item in the cart", error)
               }
     }

     fun checkIfItemExistsInCart(activity: ProductDetailsActivity, product_id: String){
          mFireStore.collection(Constant.CART_ITEM).whereEqualTo(Constant.USER_ID, getUserCurrentID())
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
          mFireStore.collection(Constant.CART_ITEM).whereEqualTo(Constant.USER_ID, getUserCurrentID()).get()

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

                         is CheckoutActivity->{
                              activity.successCartItemList(list)
                         }
                    }
               }

               .addOnFailureListener { error->
                    Log.e(activity.javaClass.simpleName, "Error!!!getting cart list items", error)
                    when(activity){
                         is CartListActivity->{
                              activity.dismissProgressDialog()
                         }

                         is CheckoutActivity->{
                              activity.dismissProgressDialog()
                         }
                    }
               }
     }

     fun getAllProductList(activity: Activity){
          mFireStore.collection(Constant.PRODUCT).get()

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


                    when(activity){
                         is CartListActivity->{
                              activity.successfullyGetProductListFromFirestore(productList)
                         }

                         is CheckoutActivity->{
                              activity.successProductListFromFirestore(productList)
                         }
                    }

               }

               .addOnFailureListener { error->
                    Log.e(activity.javaClass.simpleName, "Error.. getting all product list", error)

                    when(activity){
                         is CartListActivity->{
                              activity.dismissProgressDialog()
                         }

                         is CheckoutActivity->{
                              activity.dismissProgressDialog()
                         }
                    }
               }
     }

     fun removeItemFromCart(context: Context, cart_id: String){
          mFireStore.collection(Constant.CART_ITEM).document(cart_id).delete()

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
          mFireStore.collection(Constant.CART_ITEM).document(cart_id).update(itemHashMap)

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
          mFireStore.collection(Constant.ADDRESSES).document().set(addressInfo, SetOptions.merge())

               .addOnSuccessListener {
                    activity.addUserAddressSuccessfully()
               }

               .addOnFailureListener { error->
                    Log.e(activity.javaClass.simpleName, "Error.. updating user address", error)
               }
     }


     fun getUserAddressList(activity: AddressListActivity){
          mFireStore.collection(Constant.ADDRESSES).whereEqualTo(Constant.USER_ID, getUserCurrentID()).get()

               .addOnSuccessListener {document->
                    Log.i(activity.javaClass.simpleName, document.documents.toString())
                    // create an instance of address list
                    val addressList: ArrayList<Address> = ArrayList()
                    // a loop that runs through the document to generates the address list
                    for (i in document.documents){
                         val address = i.toObject(Address::class.java)!!
                         address.id = i.id
                         addressList.add(address)
                    }
                    activity.addressAddSuccessfullyFromFirestore(addressList)
               }
               .addOnFailureListener {  error->
                    activity.dismissProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error.. getting user address", error)
               }
     }

     fun updateAddress(activity: AddAndEditAddressListActivity, addressInfo: Address, addressId: String) {
          mFireStore.collection(Constant.ADDRESSES).document(addressId)
               // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
               .set(addressInfo, SetOptions.merge())
               .addOnSuccessListener {

                    // Here call a function of base activity for transferring the result to it.
                    activity.addUserAddressSuccessfully()
               }
               .addOnFailureListener { e ->
                    activity.dismissProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while updating the Address.", e)
               }
     }

     fun deleteUserAddress (activity: AddressListActivity, addressId: String){
          mFireStore.collection(Constant.ADDRESSES).document(addressId).delete()

               .addOnSuccessListener {
                    activity.deleteUserAddressSuccessfully()
               }

               .addOnFailureListener { e ->
                    activity.dismissProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while deleting the Address.", e)
               }
     }

     fun placeOrder(activity: CheckoutActivity, order: Order){
          mFireStore.collection(Constant.ORDER). document().set(order, SetOptions.merge())

               .addOnSuccessListener {
                    activity.placeOrderSuccessful()
               }

               .addOnFailureListener {  e ->
                    activity.dismissProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while placing an order.", e)
               }
     }

     fun updateAllCartDetails(activity: CheckoutActivity, cartList: ArrayList<CartItem>) {
//          // create a Firestore batch to allow to do multiple things at the same time
//          val writeBatch = mFireStore.batch()
//
//          for (createCartItem in cartList) {
//               // create a product hashMap of Any
//               val productHashMap = HashMap<String, Any>()
//               // use the product hashMap to add the stock quantity constant and assign value to it
//               productHashMap[Constant.STOCK_QUANTITY] =
//                    (createCartItem.stock_quantity.toInt() - createCartItem.cart_quantity.toInt()).toString()
//               // create a document reference to the product collection
//               val createDocumentReference =
//                    mFireStore.collection(Constant.PRODUCT).document(createCartItem.product_id)
//               // update the document reference using the writeBatch
//               writeBatch.update(createDocumentReference, productHashMap)
//
//               // Delete the list of cart items
//               for (deleteCartItems in cartList) {
//
//                    val deleteDocumentReferences =
//                         mFireStore.collection(Constant.CART_ITEM).document(deleteCartItems.id)
//                    writeBatch.delete(deleteDocumentReferences)
//               }
//
//               // commit or implement the update and deleting batch
//               writeBatch.commit()
//
//                    .addOnSuccessListener {
//
//                         // TODO Step 4: Finally after performing all the operation notify the user with the success result.
//                         // START
//                         activity.allDetailsUpdatedSuccessfully()
//                    }
//
//                    .addOnFailureListener { error ->
//                         activity.dismissProgressDialog()
//                         Log.e(
//                              activity.javaClass.simpleName,
//                              "Error while updating all the details after order placed.",
//                              error
//                         )
//                    }
//
//
//          }


          val writeBatch = mFireStore.batch()

          // Here we will update the product stock in the products collection based to cart quantity.
          for (cart in cartList) {

               val productHashMap = HashMap<String, Any>()

               productHashMap[Constant.STOCK_QUANTITY] =
                    (cart.stock_quantity.toInt() - cart.cart_quantity.toInt()).toString()

               val documentReference = mFireStore.collection(Constant.PRODUCT)
                    .document(cart.product_id)

               writeBatch.update(documentReference, productHashMap)
          }

          // Delete the list of cart items
          for (cart in cartList) {

               val documentReference = mFireStore.collection(Constant.CART_ITEM)
                    .document(cart.id)
               writeBatch.delete(documentReference)
          }

          writeBatch.commit().addOnSuccessListener {

               // TODO Step 4: Finally after performing all the operation notify the user with the success result.
               // START
               activity.allDetailsUpdatedSuccessfully()
               // END

          }.addOnFailureListener { e ->
               // Here call a function of base activity for transferring the result to it.
               activity.dismissProgressDialog()

               Log.e(activity.javaClass.simpleName, "Error while updating all the details after order placed.", e)
          }
     }

     fun getMyOrderList(fragment:OrderFragment){
          mFireStore.collection(Constant.ORDER).whereEqualTo(Constant.USER_ID, getUserCurrentID()).get()

               .addOnSuccessListener { document->
                    // create an arrayList of order
                    val list: ArrayList<Order> = ArrayList()
                    // run through the list of order
                    for (i in document.documents){
                         val orderItem = i.toObject(Order::class.java)!!
                         orderItem.id = i.id
                         list.add(orderItem)
                    }
                    fragment.populateOrderItemList(list)
               }

               .addOnFailureListener {error->
                    fragment.dismissProgressDialogue()
                    Log.e(fragment.javaClass.simpleName, "Error while getting order list from Firestore", error)
               }
     }

     }

