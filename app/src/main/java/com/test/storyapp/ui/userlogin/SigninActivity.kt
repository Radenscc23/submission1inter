package com.test.storyapp.ui.userlogin
import com.test.storyapp.R
import com.test.storyapp.dataset.UserPreferenceDatastore
import com.test.storyapp.databinding.ActivitySigninBinding
import com.test.storyapp.ui.factory.ViewModelFactory
import com.test.storyapp.ui.main.MainActivity
import com.test.storyapp.ui.userregister.SignupActivity
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "User")

class SigninActivity : AppCompatActivity() {
    private lateinit var appBinding: ActivitySigninBinding
    private lateinit var viewModel: SigninViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appBinding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(appBinding.root)

        setupView()
        setupViewModel()
        setupAction()
        appAnimation()

        appBinding.haveAccountTextView.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun appAnimation() {
        val accountTextView = ObjectAnimator.ofFloat(appBinding.haveAccountTextView, View.ALPHA, 1f).setDuration(600)
        val signin1 = ObjectAnimator.ofFloat(appBinding.tvSignin, View.ALPHA, 1f).setDuration(600)
        val signin2 = ObjectAnimator.ofFloat(appBinding.ivSignin, View.ALPHA, 1f).setDuration(600)
        val loginEmail = ObjectAnimator.ofFloat(appBinding.tvLoginEmail, View.ALPHA, 1f).setDuration(600)
        val loginEmail1 = ObjectAnimator.ofFloat(appBinding.edLoginEmail, View.ALPHA, 1f).setDuration(600)
        val edLoginPassword1 = ObjectAnimator.ofFloat(appBinding.tvEdLoginPassword, View.ALPHA, 1f).setDuration(600)
        val loginPassword = ObjectAnimator.ofFloat(appBinding.edLoginPassword, View.ALPHA, 1f).setDuration(600)
        val userSignIn = ObjectAnimator.ofFloat(appBinding.signinButton, View.ALPHA, 1f).setDuration(600)
        val textView = ObjectAnimator.ofFloat(appBinding.copyrightTextView, View.ALPHA, 1f).setDuration(600)

        AnimatorSet().apply {
            playSequentially(signin1, signin2, loginEmail, loginEmail1, edLoginPassword1, loginPassword, accountTextView, userSignIn, textView)
            startDelay = 600
        }.start()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferenceDatastore.instance(dataStore))
        )[SigninViewModel::class.java]

        viewModel.let { vmsignin ->
            vmsignin.result.observe(this) { signin ->
                // success signin process triggered -> save preferences
                vmsignin.saveUserData(
                    signin.loginResult.name,
                    signin.loginResult.userId,
                    signin.loginResult.token
                )

            }
            vmsignin.dataMessage.observe(this) { message ->
                if (message == "200") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.app_information)
                    builder.setMessage(R.string.login_done)
                    builder.setIcon(R.drawable.ic_baseline_check_green_24)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }, 2000)
                }
            }
            vmsignin.dataError.observe(this) { error ->
                if (error == "400") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.app_information)
                    builder.setMessage(R.string.email_validity)
                    builder.setIcon(R.drawable.baseline_do_disturb_24)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                    }, 2000)
                }
                if (error == "401") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.app_information)
                    builder.setMessage(R.string.user_does_not_exists)
                    builder.setIcon(R.drawable.baseline_do_disturb_24)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                    }, 2000)
                }
            }
            vmsignin.isLoadingLive.observe(this) {
                showLoading(it)
            }
        }
    }

    private fun setupAction() {
        appBinding.signinButton.setOnClickListener {
            val userEmail = appBinding.edLoginEmail.text.toString()
            val userPassword = appBinding.edLoginPassword.text.toString()
            when {
                userEmail.isEmpty() -> {
                    appBinding.edLoginEmail.error = getString(R.string.input_name)
                }
                userPassword.isEmpty() -> {
                    appBinding.edLoginPassword.error = getString(R.string.input_password)
                }
                userPassword.length < 6 -> {
                    appBinding.edLoginPassword.error = getString(R.string.password_validity)
                }

                else -> {
                    viewModel.signin(userEmail, userPassword)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            appBinding.progressBarLogin.visibility = View.VISIBLE
        } else {
            appBinding.progressBarLogin.visibility = View.GONE
        }
    }

}