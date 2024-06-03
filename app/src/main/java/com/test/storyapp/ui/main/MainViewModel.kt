package com.test.storyapp.ui.main
import java.io.File
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.test.storyapp.dataset.UserAddStory
import com.test.storyapp.dataset.ListStoryItem
import com.test.storyapp.dataset.StoryResponse
import com.test.storyapp.dataset.UserPreferenceDatastore
import com.test.storyapp.netconfig.ApiConfig
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel(private val pref: UserPreferenceDatastore) : ViewModel() {

    private val mutablestoryLists = MutableLiveData<List<ListStoryItem>>()
    val list: LiveData<List<ListStoryItem>> = mutablestoryLists

    private val Loading = MutableLiveData<Boolean>()
    val liveDataLoading: LiveData<Boolean> = Loading

    fun listStory(token: String) {

        Loading.value = true
        val client = ApiConfig.apiService().getListStory(bearer = "Bearer $token")
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                Loading.value = false
                if (response.isSuccessful) {
                    mutablestoryLists.value = response.body()?.listStory
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Loading.value = true
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    companion object{
        private const val TAG = "MainViewModel"
    }

    fun addNewStory(token: String, imageFile: File, desc: String) {
        Loading.value = true

        val description = desc.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )

        val userAddStoryCall = ApiConfig.apiService().userAddStoryCall(bearer = "Bearer ${token}", imageMultipart, description)
        userAddStoryCall.enqueue(object : Callback<UserAddStory> {
            override fun onResponse(call: Call<UserAddStory>, userAddStoryResponse: Response<UserAddStory>) {
                Loading.value = false
                when (userAddStoryResponse.code()) {
                    401 -> "${userAddStoryResponse.code()} : Bad Request"
                    403 -> "${userAddStoryResponse.code()} : Forbidden"
                    404 -> "${userAddStoryResponse.code()} : Not Found"
                    else -> "${userAddStoryResponse.code()} : ${userAddStoryResponse.message()}"
                }
            }

            override fun onFailure(call: Call<UserAddStory>, t: Throwable) {
                Loading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }
}