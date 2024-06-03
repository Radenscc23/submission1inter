package com.test.storyapp.ui.postStory
import com.test.storyapp.R
import java.io.File
import com.test.storyapp.utils.reduceFileImage
import com.test.storyapp.dataset.UserPreferenceDatastore
import com.test.storyapp.databinding.ActivityAddNewStoryBinding
import com.test.storyapp.ui.factory.ViewModelFactory
import com.test.storyapp.ui.main.MainActivity
import com.test.storyapp.ui.main.MainViewModel
import com.test.storyapp.ui.userlogin.SigninViewModel
import com.test.storyapp.utils.rotateBitmap
import com.test.storyapp.utils.uriToFile
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "User")
class AddNewStoryActivity : AppCompatActivity() {
    private lateinit var appBinding: ActivityAddNewStoryBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var signinViewModel: SigninViewModel

    companion object {
        const val X_RESULT = 200
        private const val CODE_PERMISSIONS = 10
        private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onRequestPermissionsResult(
        userRequest: Int,
        userPermission: Array<String>,
        userResults: IntArray
    ) {
        super.onRequestPermissionsResult(userRequest, userPermission, userResults)
        if (userRequest == CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = PERMISSIONS.all { ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appBinding = ActivityAddNewStoryBinding.inflate(layoutInflater)
        setContentView(appBinding.root)

        supportActionBar?.title = getString(R.string.post_new_story)


        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferenceDatastore.instance(dataStore))
        )[MainViewModel::class.java]

        signinViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferenceDatastore.instance(dataStore))
        )[SigninViewModel::class.java]


        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS,
                CODE_PERMISSIONS
            )
        }

        appBinding.btnAddCamera.setOnClickListener { startCameraX() }
        appBinding.btnAddGalery.setOnClickListener { startGallery() }
        appBinding.buttonAdd.setOnClickListener {
            if (filefromGallery != null) {
                if(appBinding.edAddDescription.text.toString().isNotEmpty()) {
                    val file = reduceFileImage(filefromGallery as File)
                    val token =
                        signinViewModel.getUserData().observe(this){ user ->
                            viewModel.addNewStory(user.token, file, appBinding.edAddDescription.text.toString())
                            viewModel.liveDataLoading.observe(this) {
                                showLoading(it)
                            }
                        }
                } else {
                    Toast.makeText(this@AddNewStoryActivity, getString(R.string.story_desc_check), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@AddNewStoryActivity, getString(R.string.story_image_check), Toast.LENGTH_SHORT).show()
            }
        }
    }


        private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            appBinding.progressBarAdd.visibility = View.VISIBLE
        } else {
            appBinding.progressBarAdd.visibility = View.GONE

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraNewStoryActivity::class.java)
        intentCameraX.launch(intent)
    }

    private var filefromGallery: File? = null
    private val intentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            filefromGallery = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(filefromGallery?.path),
                isBackCamera
            )

            appBinding.tvAddImg.setImageBitmap(result)
        }
    }

    private val intentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddNewStoryActivity)
            filefromGallery = myFile

            appBinding.tvAddImg.setImageURI(selectedImg)
        }
    }

    private fun startGallery() {
        val target = Intent()
        target.action = ACTION_GET_CONTENT
        target.type = "image/*"
        val input = Intent.createChooser(target, getString(R.string.choose_picture))
        intentGallery.launch(input)
    }
}
