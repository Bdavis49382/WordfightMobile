package com.wordfightmobile.data

data class Player(val name: String = "",
                  var id: String = "",
                  val img: String? = null,
                  val FCMToken: String? = null,
                  val friendCode: String? = null,
                  val friends: List<String> = listOf())
