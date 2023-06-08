package com.daignosis.daignosis.data.repository

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.daignosis.daignosis.data.api.ApiService
import com.daignosis.daignosis.data.model.LoginModel
import com.daignosis.daignosis.data.model.RegisterModel
import com.daignosis.daignosis.data.response.*
import com.daignosis.daignosis.utils.UserPref
import com.daignosis.daignosis.utils.Result
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DaignosisRepository (
    private val apiService: ApiService,
    private val pref: UserPref
){

    fun getToken(): LiveData<Data>{
        return pref.getToken().asLiveData()
    }

    fun logout(){
        MainScope().launch {
            pref.logout()
        }
    }

    fun login (email: String, password: String): LiveData<Result<Boolean>> {
        val login = MutableLiveData<Result<Boolean>>()
        login.value = Result.Loading

        val client = apiService.postLogin(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error){
                        login.value = Result.Success(true)
                        MainScope().launch { pref.saveTokenUser(responseBody.data) }
                    } else {
                        login.value = Result.Error("Error")
                        Log.e(ContentValues.TAG, "onResponse(E): ${response.message()}" )
                    }
                } else {
                    login.value = Result.Error("Error")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                login.value = Result.Error("Error")
                Log.e(ContentValues.TAG, "onFailure(T): ${t.message}")
            }
        })
        return login
    }

    fun register(
        username: String, email: String, password: String
    ):LiveData<Result<Boolean>> {
        val register = MutableLiveData<Result<Boolean>>()
        register.value = Result.Loading

        val client = apiService.postRegister(username, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error){
                        register.value = Result.Success(true)
                    } else {
                        register.value = Result.Error("Error")
                        Log.e(ContentValues.TAG, "onResponse(E): ${response.message()}" )
                    }
                } else {
                    register.value = Result.Error("Error")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                register.value = Result.Error("Error")
                Log.e(ContentValues.TAG, "onFailure(T): ${t.message}")
            }
        })
        return register
    }

    fun forgotPw (username: String): LiveData<Result<Boolean>> {
        val forgot = MutableLiveData<Result<Boolean>>()

        forgot.value = Result.Loading
        val client = apiService.postForgotpw(username)
        client.enqueue(object: Callback<ForgotResponse>{
            override fun onResponse(
                call: Call<ForgotResponse>,
                response: Response<ForgotResponse>,
            ) {
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        forgot.value = Result.Success(true)
                    } else {
                        forgot.value = Result.Error("Error")
                        Log.e(ContentValues.TAG, "onResponse: Fail ${response.message()}" )
                    }
                } else {
                    forgot.value = Result.Error("Error")
                    Log.e(ContentValues.TAG, "onResponse: isError ${response.message()}" )
                }
            }

            override fun onFailure(call: Call<ForgotResponse>, t: Throwable) {
                forgot.value = Result.Error("Error")
                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}" )
            }
        })
        return forgot
    }

    fun getListArticle(): Pair<LiveData<Result<Boolean>>, LiveData<List<DataItem>>>{
        val allArticle = MutableLiveData<List<DataItem>>()
        val progress = MutableLiveData<Result<Boolean>>()

        progress.value = Result.Loading
        val client = apiService.getAllArticle()
        client.enqueue(object: Callback<ArticleResponse> {
            override fun onResponse(
                call: Call<ArticleResponse>,
                response: Response<ArticleResponse>
            ) {
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody != null){
                        allArticle.value = responseBody.data
                        progress.value = Result.Success(true)
                    }else{
                        Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                        progress.value = Result.Error("Error")
                    }
                }
            }

            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                progress.value = Result.Error("Error")
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }

        })
        return Pair(progress,allArticle)
    }

    fun getArticleLimit(): Pair<LiveData<Result<Boolean>>, LiveData<List<DataItem>>>{
        val allArticle = MutableLiveData<List<DataItem>>()
        val progress = MutableLiveData<Result<Boolean>>()

        progress.value = Result.Loading
        val client = apiService.getMainArticle()
        client.enqueue(object: Callback<ArticleResponse> {
            override fun onResponse(
                call: Call<ArticleResponse>,
                response: Response<ArticleResponse>
            ) {
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody != null){
                        allArticle.value = responseBody.data
                        progress.value = Result.Success(true)
                    }else{
                        Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                        progress.value = Result.Error("Error")
                    }
                }
            }

            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                progress.value = Result.Error("Error")
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }

        })
        return Pair(progress,allArticle)
    }

    fun getUserProfile(token: String): Pair<LiveData<Result<Boolean>>, LiveData<ProfileResponse>>{
        val user = MutableLiveData<ProfileResponse>()
        val progress = MutableLiveData<Result<Boolean>>()

        progress.value = Result.Loading
        val client = apiService.getProfileUser("Bearer $token")
        client.enqueue(object: Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody != null){
                        user.postValue(responseBody!!)
                        progress.value = Result.Success(true)
                    }else{
                        Log.e(ContentValues.TAG, "onFailure1: ${response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                progress.value = Result.Error("Error")
                Log.e(ContentValues.TAG, "onFailure2: ${t.message}")
            }
        })
        return Pair(progress, user)
    }
}