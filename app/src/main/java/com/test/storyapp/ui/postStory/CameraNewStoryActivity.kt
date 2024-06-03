package com.test.storyapp.ui.postStory
import com.test.storyapp.R
import com.test.storyapp.databinding.ActivityCameraNewStoryBinding
import com.test.storyapp.utils.createFile
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast



class CameraNewStoryActivity : AppCompatActivity() {
    private lateinit var appBinding: ActivityCameraNewStoryBinding
    private var chooseCamera: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var getImage: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appBinding = ActivityCameraNewStoryBinding.inflate(layoutInflater)
        setContentView(appBinding.root)

        appBinding.captureImage.setOnClickListener { photo() }
        appBinding.switchCamera.setOnClickListener {
            chooseCamera = if (chooseCamera.equals(CameraSelector.DEFAULT_BACK_CAMERA)) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA
            camera()
        }
    }

    private fun photo() {
        val capture = getImage ?: return
        val value = createFile(application)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(value).build()

        capture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraNewStoryActivity,
                        getString(R.string.picture_not_successful),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val intent = Intent()
                    intent.putExtra("picture", value)
                    intent.putExtra(
                        "isBackCamera",
                        chooseCamera == CameraSelector.DEFAULT_BACK_CAMERA
                    )
                    setResult(AddNewStoryActivity.X_RESULT, intent)
                    finish() } }
        )
    }

    public override fun onResume() {
        super.onResume()
        systemUI()
        camera()
    }

    private fun systemUI() {
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

    private fun camera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val processCameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val useCases = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(appBinding.viewFinder.surfaceProvider)
                }

            getImage = ImageCapture.Builder().build()

            try {
                processCameraProvider.unbindAll()
                processCameraProvider.bindToLifecycle(
                    this,
                    chooseCamera,
                    useCases,
                    getImage
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraNewStoryActivity,
                    R.string.camera_set_fail,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }


}