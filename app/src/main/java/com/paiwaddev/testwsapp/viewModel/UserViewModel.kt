package com.paiwaddev.ahorros.ui.main.goal

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.*
import com.google.android.material.textfield.TextInputEditText
import com.paiwaddev.testwsapp.R
import com.paiwaddev.testwsapp.data.model.Users
import com.paiwaddev.testwsapp.data.repository.UsersRespository
import kotlinx.coroutines.launch

class UsersViewModel(private val context: Context, private val repository: UsersRespository) : ViewModel() {

    fun onUserLogin(user: String, pass: String): LiveData<Users>{
        val users = repository.userLogin(user,pass)
        return users
    }

    fun getUserByID(userID: Long): LiveData<Users> = repository.userByID(userID)

    fun onRegister(regular: Users) = viewModelScope.launch {
        repository.insert(regular)
    }

    fun requireInput(input: TextInputEditText){
        if(TextUtils.isEmpty(input.text))
            input.error = context.resources.getString(R.string.msg_require_edit)
    }

    fun checkCardID(cardID: String): Boolean{
        if(cardID.length != 13){
            return false
        }
        return true
    }

    fun checkPhone(phone: String): Boolean{
        if(phone.length != 10){
            return false
        }
        if(phone.get(0) != '0' && (phone.get(1)!='6' || phone.get(1)!='8' || phone.get(1)!='6')){
            return false
        }
        return true
    }

    fun checkInvalidData(cardID: String, phone: String): LiveData<Boolean> {
        var invalid = MutableLiveData<Boolean>()
        println(checkCardID(cardID))
        if (!checkCardID(cardID)) {
            invalid.value = false
            return invalid
        }
        if (phone.isNotEmpty()) {
            if (!checkPhone(phone)){
                invalid.value = false
                return invalid
            }
        }
        invalid.value = true
        return invalid
    }
}


class UsersViewModelFactory(private val context: Context, private val repository: UsersRespository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsersViewModel(context, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}