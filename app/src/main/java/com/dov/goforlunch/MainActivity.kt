package com.dov.goforlunch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.util.*
import java.util.Arrays.asList



class MainActivity : BaseActivity() {

    private lateinit var button : Button

    companion object {
        val RC_SIGN_IN = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (currentUser!=null){
            startActivity(Intent(this, ChatActivity::class.java))
            setContentView(R.layout.activity_main)
            setViewItems()
        } else {
            setContentView(R.layout.activity_main)
            setViewItems()
        }
    }

    private fun setViewItems(){

        button = findViewById<Button>(R.id.connect)
        button.setOnClickListener { startActivity(Intent(this, EmailPasswordActivity::class.java)) }
    }


    private fun startSignInActivity() {

       /* val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(
                    providers
                )
                .setIsSmartLockEnabled(false, true)
                .setLogo(android.R.drawable.ic_lock_power_off)
                .build(),
            RC_SIGN_IN
        )*/
    }
}
