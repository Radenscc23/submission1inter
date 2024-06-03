package com.test.storyapp.dataset

import com.google.gson.annotations.SerializedName

data class UserRegister(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
