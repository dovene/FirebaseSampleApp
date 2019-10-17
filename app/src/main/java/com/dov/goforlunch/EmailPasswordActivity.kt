package com.dov.goforlunch

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dov.goforlunch.api.UserHelper
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.email_password.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayOutputStream
import java.util.UUID.randomUUID


class EmailPasswordActivity : AppCompatActivity() {

    private lateinit var create : AppCompatButton
    private lateinit var signIn : AppCompatButton
    private lateinit var signOut : AppCompatButton

    private lateinit var addImage : AppCompatButton

    private lateinit var email : AppCompatEditText
    private lateinit var password : AppCompatEditText

    private var uriImageSelected: Uri? = null

    private lateinit var imageView : AppCompatImageView

    companion object {
        val RC_SIGN_IN = 123
        val TAG ="EmailPass"
         val PERMS = Manifest.permission.READ_EXTERNAL_STORAGE
         const val RC_IMAGE_PERMS = 100
        val RC_CHOOSE_PHOTO = 200

    }

    private lateinit var auth: FirebaseAuth
// ...
// Initialize Firebase Auth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.email_password)
        setViewItems()
        auth = FirebaseAuth.getInstance()
    }

    private fun setViewItems(){
        create = findViewById(R.id.create)
        signIn = findViewById(R.id.signIn)
        signOut = findViewById(R.id.signOut)

        addImage = findViewById(R.id.imageB)

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        imageView = findViewById(R.id.imageV)

        create.setOnClickListener { createUser(email.text.toString(), password.text.toString()) }

        signIn.setOnClickListener { signIn(email.text.toString(), password.text.toString()) }
        signOut.setOnClickListener { signOut() }

        addImage.setOnClickListener { onClickAddFile() }
        sendPic.setOnClickListener { uploadPhotoInFirebaseAndSendMessage() }

    }


    private fun startSignInActivity() {

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

        updateUI(currentUser)
    }

    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                    createUserInFirestore()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }

                // ...
            }
    }

    private  fun signIn(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun signOut(){
        FirebaseAuth.getInstance().signOut()
    }

    fun updateUI(user: FirebaseUser?){
        if (user!=null){
            //goto Main Activity
            //For tests purpose
            createUserInFirestore()

        }else{

        }
    }

    fun getCurrentUser(): FirebaseUser?{
       return auth.currentUser
    }

    // 1 - Http request that create user in firestore
    private fun createUserInFirestore() {

        getCurrentUser()?.let {
            val urlPicture =
                if (it.photoUrl != null) it.photoUrl.toString() else null
            val username = it.displayName
            val uid = it.uid

            UserHelper.createUser(uid, username, urlPicture).addOnFailureListener(this.onFailureListener())
        }

    }

    // --------------------
    // ERROR HANDLER
    // --------------------

    protected fun onFailureListener(): OnFailureListener {
        return OnFailureListener {
            Toast.makeText(
                applicationContext,
                getString(R.string.error_unknown_error),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    @AfterPermissionGranted(RC_IMAGE_PERMS)
    fun onClickAddFile() {
      /*  if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.popup_title_permission_files_access),
                RC_IMAGE_PERMS,
                PERMS
            )
            return
        }
        Toast.makeText(this, "Vous avez le droit d'accéder aux images !", Toast.LENGTH_SHORT).show()*/
        chooseImageFromPhone()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        this.handleResponse(requestCode, resultCode, data)
    }


    // 1 - Upload a picture in Firebase and send a message
    private fun uploadPhotoInFirebaseAndSendMessage() {
        val uuid = randomUUID().toString() // GENERATE UNIQUE STRING
        // A - UPLOAD TO GCS
        val mImageRef = FirebaseStorage.getInstance().getReference(uuid)





        // Get the data from an ImageView as bytes
        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = mImageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
              var pathImageSavedInFirebase = "" //it.getMetadata()?.getDownloadUrl().toString()
                        // B - SAVE MESSAGE IN FIRESTORE
            //MessageHelper.createMessageWithImageForChat(pathImageSavedInFirebase, message, currentChatName, modelCurrentUser).addOnFailureListener(onFailureListener());

            //UserHelper.updateUserPicture(getCurrentUser()?.uid!!, pathImageSavedInFirebase)

           UserHelper.updateUserPictureAndName(getCurrentUser()?.uid!!, pathImageSavedInFirebase, email.text.toString())


        }.addOnFailureListener(this.onFailureListener())
    }

    // --------------------
    // FILE MANAGEMENT
    // --------------------

    private fun chooseImageFromPhone() {
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.popup_title_permission_files_access),
                RC_IMAGE_PERMS,
                PERMS
            )
            return
        }
        // 3 - Launch an "Selection Image" Activity
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, RC_CHOOSE_PHOTO)
    }

    // 4 - Handle activity response (after user has chosen or not a picture)
    private fun handleResponse(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) { //SUCCESS
                this.uriImageSelected = data?.data
                Glide.with(this) //SHOWING PREVIEW OF IMAGE
                    .load(this.uriImageSelected)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageV)
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.toast_title_no_image_chosen),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
