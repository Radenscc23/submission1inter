package com.test.storyapp.ui.userregister
import com.test.storyapp.R
import com.test.storyapp.dataset.UserPreferenceDatastore
import com.test.storyapp.databinding.ActivitySignupBinding
import com.test.storyapp.ui.factory.ViewModelFactory
import com.test.storyapp.ui.userlogin.SigninActivity
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "User")

class SignupActivity : AppCompatActivity() {
    private lateinit var appBinding: ActivitySignupBinding
    private lateinit var viewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appBinding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(appBinding.root)

        setupView()
        setupViewModel()
        setupAction()
        animation()
    }

    private fun animation() {
        val signup1 = ObjectAnimator.ofFloat(appBinding.tvSignup, View.ALPHA, 1f).setDuration(600)
        val signup2 = ObjectAnimator.ofFloat(appBinding.ivSignup, View.ALPHA, 1f).setDuration(600)
        val registerName = ObjectAnimator.ofFloat(appBinding.tvRegisterName, View.ALPHA, 1f).setDuration(600)
        val registerName1 = ObjectAnimator.ofFloat(appBinding.edRegisterName, View.ALPHA, 1f).setDuration(600)
        val registerEmail = ObjectAnimator.ofFloat(appBinding.tvRegisterEmail, View.ALPHA, 1f).setDuration(600)
        val registerEmail1 = ObjectAnimator.ofFloat(appBinding.edRegisterEmail, View.ALPHA, 1f).setDuration(600)
        val registerPassword = ObjectAnimator.ofFloat(appBinding.tvRegisterPassword, View.ALPHA, 1f).setDuration(600)
        val registerPassword1 = ObjectAnimator.ofFloat(appBinding.edRegisterPassword, View.ALPHA, 1f).setDuration(600)
        val userSignUp = ObjectAnimator.ofFloat(appBinding.signupButton, View.ALPHA, 1f).setDuration(600)
        val textView = ObjectAnimator.ofFloat(appBinding.copyrightTextView, View.ALPHA, 1f).setDuration(600)

        AnimatorSet().apply {
            playSequentially(signup1, signup2, registerName, registerName1, registerEmail, registerEmail1, registerPassword, registerPassword1, userSignUp, textView)
            startDelay = 600
        }.start()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferenceDatastore.instance(dataStore))
        )[SignupViewModel::class.java]

        viewModel?.let { signupvm ->
            signupvm.dataMessage.observe(this) { message ->
                if (message == "201") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.app_information)
                    builder.setMessage(R.string.register_done)
                    builder.setIcon(R.drawable.ic_baseline_check_green_24)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                        val intent = Intent(this, SigninActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 2000)
                }
            }
            signupvm.errorMessage.observe(this) { error ->
                if (error == "400") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.app_information)
                    builder.setMessage(R.string.register_not_sucess)
                    builder.setIcon(R.drawable.baseline_do_disturb_24)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                    }, 2000)
                }
            }
            signupvm.loading.observe(this) {
                showLoading(it)
            }

        }
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

    private fun showLoading(isLoading: Boolean) {

        if (isLoading) {
            appBinding.progressBarRegister.visibility = View.VISIBLE
        } else {
            appBinding.progressBarRegister.visibility = View.GONE
        }
    }


    private fun setupAction() {
        appBinding.signupButton.setOnClickListener {
            val name = appBinding.edRegisterName.text.toString()
            val email = appBinding.edRegisterEmail.text.toString()
            val password = appBinding.edRegisterPassword.text.toString()
            when {
                name.isEmpty() -> {
                    appBinding.edRegisterName.error = getString(R.string.input_name)
                }
                email.isEmpty() -> {
                    appBinding.edRegisterEmail.error = getString(R.string.input_email)
                }
                password.isEmpty() -> {
                    appBinding.edRegisterPassword.error = getString(R.string.input_password)
                }
                password.length < 6 -> {
                    appBinding.edRegisterPassword.error = getString(R.string.password_validity)
                }
                else -> {
                    viewModel.userSignUp(name, email, password)
                }
            }
        }
    }
}