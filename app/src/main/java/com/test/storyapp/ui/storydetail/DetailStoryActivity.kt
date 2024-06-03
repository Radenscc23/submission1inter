package com.test.storyapp.ui.storydetail
import com.test.storyapp.R
import com.test.storyapp.databinding.ActivityDetailStoryBinding
import com.test.storyapp.utils.withDateFormat
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var  appBinding: ActivityDetailStoryBinding
    companion object {
        const val STORY_NAME = "name"
        const val CREATED_AT = "create_at"
        const val STORY_DESC = "description"
        const val PHOTO_URL = "photoUrl"
    }
    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appBinding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(appBinding.root)

        supportActionBar?.title = getString(R.string.detail_of_story)

        val url = intent.getStringExtra(PHOTO_URL)
        val story_name = intent.getStringExtra(STORY_NAME)
        val story_created_at = intent.getStringExtra(CREATED_AT)
        val story_desc = intent.getStringExtra(STORY_DESC)

        Glide.with(appBinding.root.context)
            .load(url)
            .into(appBinding.ivDetailPhoto)
        appBinding.tvDetailName.text = story_name
        appBinding.tvDetailCreatedTime.text = story_created_at?.withDateFormat()
        appBinding.tvDetailDescription.text = story_desc

    }
}