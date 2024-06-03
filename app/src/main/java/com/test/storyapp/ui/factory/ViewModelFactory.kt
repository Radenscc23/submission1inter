package com.test.storyapp.ui.factory
import com.test.storyapp.dataset.UserPreferenceDatastore
import com.test.storyapp.ui.main.MainViewModel
import com.test.storyapp.ui.userlogin.SigninViewModel
import com.test.storyapp.ui.userregister.SignupViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val userPreferenceDatastore: UserPreferenceDatastore) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SigninViewModel::class.java) -> {
                SigninViewModel(userPreferenceDatastore) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(userPreferenceDatastore) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userPreferenceDatastore) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}