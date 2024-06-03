package com.test.storyapp.ui.userlogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.test.storyapp.dataset.LoginResult
import com.test.storyapp.dataset.SigninResponse
import com.test.storyapp.dataset.UserPreferenceDatastore
import com.test.storyapp.netconfig.ApiConfig
import android.util.Log
import androidx.lifecycle.*

import kotlinx.coroutines.launch


class SigninViewModel(private val userPreferenceDatastore: UserPreferenceDatastore) : ViewModel() {

    private val Loading = MutableLiveData<Boolean>()
    val isLoadingLive: LiveData<Boolean> = Loading
    val dataError = MutableLiveData("")
    val dataMessage = MutableLiveData("")
    private val TAG = SigninViewModel::class.simpleName
    val result = MutableLiveData<SigninResponse>()

    fun signin(email: String, password: String) {
        Loading.value = true
        val client = ApiConfig.apiService().userSignIn(email, password)
        client.enqueue(object : Callback<SigninResponse> {
            override fun onResponse(call: Call<SigninResponse>, response: Response<SigninResponse>) {
                when (response.code()) {
                    200 -> {
                        result.postValue(response.body())
                        dataMessage.postValue("200")
                    }
                    400 -> dataError.postValue("400")
                    401 -> dataError.postValue("401")
                    else -> dataError.postValue("ERROR ${response.code()} : ${response.message()}")
                }

                Loading.value = false
            }

            override fun onFailure(call: Call<SigninResponse>, t: Throwable) {
                Loading.value = true
                Log.e(TAG, "onFailure Call: ${t.message}")
                dataError.postValue(t.message)
            }
        })
    }

    fun getUserData(): LiveData<LoginResult> { return userPreferenceDatastore.getUserInfo().asLiveData() }

    fun saveUserData(userName: String, userId: String, userToken: String) {
        viewModelScope.launch {
            userPreferenceDatastore.saveInfoUser(userName,userId,userToken)
        }
    }

    fun userSignout() { viewModelScope.launch { userPreferenceDatastore.userSignOut() } }
}