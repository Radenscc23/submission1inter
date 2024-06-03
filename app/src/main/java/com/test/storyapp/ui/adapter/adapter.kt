package com.test.storyapp.ui.adapter
import com.test.storyapp.dataset.ListStoryItem
import com.test.storyapp.databinding.ItemStoryBinding
import com.test.storyapp.ui.storydetail.DetailStoryActivity
import com.test.storyapp.utils.withDateFormat
import com.bumptech.glide.Glide
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView

class adapter(private val listStoryItems: List<ListStoryItem>) : RecyclerView.Adapter<adapter.MyViewHolderStory>() {

    class MyViewHolderStory(private val appBinding: ItemStoryBinding) :
        RecyclerView.ViewHolder(appBinding.root) {
        fun bind(data: ListStoryItem) {
            Glide.with(appBinding.root.context)
                .load(data.photoUrl)
                .into(appBinding.imgItemPhoto)

            appBinding.tvItemName.text = data.name
            appBinding.tvItemCreated.text = data.createdAt.withDateFormat()
            appBinding.tvItemDescription.text = data.description
            itemView.setOnClickListener {
                val appIntent = Intent(itemView.context, DetailStoryActivity::class.java)
                appIntent.putExtra(DetailStoryActivity.STORY_NAME, data.name)
                appIntent.putExtra(DetailStoryActivity.CREATED_AT, data.createdAt)
                appIntent.putExtra(DetailStoryActivity.STORY_DESC, data.description)
                appIntent.putExtra(DetailStoryActivity.PHOTO_URL, data.photoUrl)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        androidx.core.util.Pair(appBinding.imgItemPhoto, "photo"),
                        androidx.core.util.Pair(appBinding.tvItemName, "name"),
                        androidx.core.util.Pair(appBinding.tvItemCreated, "createdate"),
                        androidx.core.util.Pair(appBinding.tvItemDescription, "description"),
                    )
                itemView.context.startActivity(appIntent, optionsCompat.toBundle())
            }
        }
    }

    override fun onBindViewHolder(holder: MyViewHolderStory, position: Int) { val data = listStoryItems[position]
        holder.bind(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderStory {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolderStory(binding)
    }

    override fun getItemCount() = listStoryItems.size

}