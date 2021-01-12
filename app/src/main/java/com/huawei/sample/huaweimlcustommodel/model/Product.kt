package com.huawei.sample.huaweimlcustommodel.model

data class Product(
    val id: Int,
    val title: String,
    val image: String,
    val price: Double,
    var category: String
)