package com.huawei.sample.huaweimlcustommodel.view


import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.huawei.sample.huaweimlcustommodel.huaweimlutils.InterpreterManager
import com.huawei.sample.huaweimlcustommodel.huaweimlutils.ModelOperator
import com.huawei.sample.huaweimlcustommodel.model.Product
import com.huawei.sample.huaweimlcustommodel.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_list_item.view.*


class ProductAdapter(private val context:Activity, private val productList: ArrayList<Product>) : RecyclerView.Adapter<ProductAdapter.FeedViewHolder>() {
    private var interpreterManager: InterpreterManager? = null
    var modelType: ModelOperator.Model =
        ModelOperator.Model.LABEL
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_list_item, parent, false)
        interpreterManager =
            InterpreterManager(
                context,
                modelType
            )
        return FeedViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        Picasso.get().load(productList[position].image).into(holder.itemView.product_image, object: com.squareup.picasso.Callback {
            override fun onSuccess() {
                if (holder.itemView.product_category.text == "" || holder.itemView.product_category.text == "null") {
                    val bitmapDrawable: BitmapDrawable =
                        holder.itemView.product_image.drawable as BitmapDrawable
                    interpreterManager?.asset(bitmapDrawable.bitmap, position) {
                        productList[position].category = it[position].toString()
                        notifyDataSetChanged()
                    }
                }
            }

            override fun onError(e: Exception?) {
            }
        })
        if(productList[position].category != "" && productList[position].category != "null") {
            holder.itemView.product_category.text = productList[position].category
            holder.itemView.product_category.visibility = View.VISIBLE
        }
        holder.itemView.product_description.text = productList[position].title
        holder.itemView.product_price.text = "Price: ${productList[position].price}$"
    }

    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

