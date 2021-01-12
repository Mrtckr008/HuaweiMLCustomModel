package com.huawei.sample.huaweimlcustommodel.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.huawei.sample.huaweimlcustommodel.model.Product
import com.huawei.sample.huaweimlcustommodel.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        product_recyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        product_recyclerview.adapter =
            ProductAdapter(
                this,
                arrayListOf(
                    Product(
                        id = 1,
                        title = "Adidas Men's Run 90s Mesh Running Shoes",
                        image = "https://i.hizliresim.com/jKkiCV.jpg",
                        price = 52.99,
                        category = ""
                    ),
                    Product(
                        id = 2,
                        title = "Men's Mountain Waterproof Ski Jacket Windproof Rain Jacket Winter Warm Snow Coat",
                        image = "https://i.hizliresim.com/JVk2Qy.jpg",
                        price = 77.99,
                        category = ""
                    ),
                    Product(
                        id = 3,
                        title = "H&M Men's T-Shirt",
                        image = "https://i.hizliresim.com/Obrqbk.jpg",
                        price = 51.99,
                        category = ""
                    ),
                    Product(
                        id = 4,
                        title = "Mavi Men's Jean",
                        image = "https://i.hizliresim.com/hLM3bm.jpg",
                        price = 19.99,
                        category = ""
                    )
                )
            )
    }
}