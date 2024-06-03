package com.test.storyapp.ui.userregister
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.test.storyapp.dataset.UserRegister
import com.test.storyapp.dataset.UserPreferenceDatastore
import com.test.storyapp.netconfig.ApiConfig
import com.test.storyapp.ui.userlogin.SigninViewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignupViewModel(private val pref: UserPreferenceDatastore) : ViewModel() {
    private val Loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = Loading
    val errorMessage = MutableLiveData("")
    val dataMessage = MutableLiveData("")
    private val TAG = SigninViewModel::class.simpleName

    fun userSignUp(name: String, email: String, password: String) {
        Loading.value = true
        val client = ApiConfig.apiService().userSignUp(name, email, password)
        client.enqueue(object : Callback<UserRegister> {
            override fun onResponse(call: Call<UserRegister>, response: Response<UserRegister>) {
                when (response.code()) {
                    400 -> errorMessage.postValue("400")
                    201 -> dataMessage.postValue("201")
                    else -> errorMessage.postValue("ERROR ${response.code()} : ${response.errorBody()}")
                }
                Loading.value = false
            }
            override fun onFailure(call: Call<UserRegister>, t: Throwable) {
                Loading.value = true
                Log.e(TAG, "onFailure Call: ${t.message}")
                errorMessage.postValue(t.message)
            }
        })
    }
}