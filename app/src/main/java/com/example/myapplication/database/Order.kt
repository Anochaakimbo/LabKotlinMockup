package com.example.myapplication.database

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    @Expose
    @SerializedName("id") val id: Int,

    @Expose
    @SerializedName("customer_name") val customerName: String,

    @Expose
    @SerializedName("cup_size") val cupSize: String,

    @Expose
    @SerializedName("topping") val topping: String,

    @Expose
    @SerializedName("price") val price: Int,

    @Expose
    @SerializedName("sweet_level") val sweetLevel: String,

    @Expose
    @SerializedName("cup_quantity") val cupQuantity: String
) : Parcelable{}