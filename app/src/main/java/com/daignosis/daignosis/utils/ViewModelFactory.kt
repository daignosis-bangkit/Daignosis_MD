package com.daignosis.daignosis.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daignosis.daignosis.di.Injection
import com.daignosis.daignosis.ui.forgotpw.ForgotViewModel
import com.daignosis.daignosis.ui.login.LoginViewModel
import com.daignosis.daignosis.ui.register.RegisterViewModel

class ViewModelFactory (private val context: Context) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(ForgotViewModel::class.java) -> {
                ForgotViewModel(Injection.provideRepository(context)) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}