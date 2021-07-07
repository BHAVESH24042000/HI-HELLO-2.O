package com.example.hi_hello_2o.models

import com.example.hi_hello_2.models.User

data class Group(
    val name: String? = null,
    val imageUrl: String? = null,
    val thumbImage: String? = null,
    val groupCode: String? = null,
    val groupUID: String? = null,
    val members: MutableList<User> = mutableListOf()

) {


}