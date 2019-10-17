package com.dov.goforlunch

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

open class BaseActivity : AppCompatActivity() {


    // --------------------
    // UTILS
    // --------------------

    protected val currentUser: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser

    protected val isCurrentUserLogged: Boolean?
        get() = this.currentUser != null





    // --------------------
    // ERROR HANDLER
    // --------------------

    protected fun onFailureListener(): OnFailureListener {
        return OnFailureListener {
            Toast.makeText(
                getApplicationContext(),
                getString(R.string.error_unknown_error),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
