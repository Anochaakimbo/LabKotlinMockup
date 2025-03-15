package com.example.myapplication.api

import com.example.myapplication.database.Order
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface OrderAPI {

    @GET("orders")
    fun retrieveOrder(): Call<List<Order>>


    @FormUrlEncoded
    @POST("order")
    fun insertOrder(
        @Field("customer_name") customerName: String,
        @Field("cup_size") cupSize: String,
        @Field("topping") topping: String,
        @Field("cup_quantity") cupQuantity: String,
        @Field("price") price: Int,
        @Field("sweet_level") sweetLevel: String


    )
            : Call<Order>

    @FormUrlEncoded
    @PUT("order/{id}")
    fun updateOrder(
        @Path("id")id:Int,
        @Field("customer_name") customerName: String,
        @Field("cup_size") cupSize: String,
        @Field("topping") topping: String,
        @Field("cup_quantity") cupQuantity: String,
        @Field("price") price: Int,
        @Field("sweet_level") sweetLevel: String
    )
            : Call<Order>

    @DELETE("order/{id}") /// Delete
    fun deleteOrder(
        @Path("id")id:Int): Call<Order>

    companion object {
        fun create(): OrderAPI {
            val orderClient: OrderAPI = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OrderAPI::class.java)
            return orderClient
        }
    }
}
