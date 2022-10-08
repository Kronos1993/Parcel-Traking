package com.kronos.domain.model.user

data class UserModel(
    var name:String = "",
    var lastname:String = "",
    var phone:String = "",
    var email:String = "",
    var address:String = "",
){

    fun isLogged():Boolean{
        return name.isNotEmpty() && lastname.isNotEmpty() && email.isNotEmpty()
    }

}
