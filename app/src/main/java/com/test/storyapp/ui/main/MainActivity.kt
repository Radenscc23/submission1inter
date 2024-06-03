package com.test.storyapp.ui.main
import com.test.storyapp.R
import com.test.storyapp.dataset.ListStoryItem
import com.test.storyapp.dataset.UserPreferenceDatastore
import com.test.storyapp.databinding.ActivityMainBinding
import com.test.storyapp.ui.factory.ViewModelFactory
import com.test.storyapp.ui.postStory.AddNewStoryActivity
import com.test.storyapp.ui.userlogin.SigninActivity
import com.test.storyapp.ui.userlogin.SigninViewModel
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.test.storyapp.ui.adapter.adapter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "User")

class MainActivity : AppCompatActivity() {
    private lateinit var appBinding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var signinViewModel: SigninViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(appBinding.root)

        supportActionBar?.title = getString(R.string.main_dashboard)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferenceDatastore.instance(dataStore))
        )[MainViewModel::class.java]

        signinViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferenceDatastore.instance(dataStore))
        )[SigninViewModel::class.java]

        signinViewModel.getUserData().observe(this){ user->
            if (user.userId.isEmpty()){
                val intent = Intent(this, SigninActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                viewModel.listStory(user.token)
            }
        }

        val layoutManager = LinearLayoutManager(this)
        appBinding.rvListStory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        appBinding.rvListStory.addItemDecoration(itemDecoration)

        viewModel.list.observe(this) { listStory ->
            setReviewData(listStory)
        }
        viewModel.liveDataLoading.observe(this) {
            showLoading(it)
        }

        appBinding.btnAddStory.setOnClickListener {
            val i = Intent(this@MainActivity, AddNewStoryActivity::class.java)
            startActivity(i)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_language -> {
                val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(mIntent)
            }
            R.id.action_logout -> {
                signinViewModel.userSignout() }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setReviewData(listStory: List<ListStoryItem>) { val adapter = adapter(listStory as ArrayList<ListStoryItem>)
        appBinding.rvListStory.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) { appBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE }
}