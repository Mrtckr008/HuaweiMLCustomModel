package com.huawei.sample.huaweimlcustommodel

data class Product(
    val id: Int,
    val title: String,
    val image: String,
    val price: Double,
    var category: String
)