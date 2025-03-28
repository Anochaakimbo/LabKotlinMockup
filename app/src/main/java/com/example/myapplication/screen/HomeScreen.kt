package com.example.myapplication.screen


import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.myapplication.api.OrderAPI
import com.example.myapplication.database.Order
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun HomeScreen(navController: NavHostController) {
    var orderItemsList = remember { mutableStateListOf<Order>() }
    val contextForToast = LocalContext.current.applicationContext

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.DESTROYED -> {}
            Lifecycle.State.INITIALIZED -> {}
            Lifecycle.State.CREATED -> {}
            Lifecycle.State.STARTED -> {}
            Lifecycle.State.RESUMED -> {
                showallData(orderItemsList, contextForToast)
            }
        }
    }
    Column {
        Spacer(modifier = Modifier.height(height = 20.dp))
        Row(Modifier.fillMaxWidth()
            .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Column(modifier = Modifier.weight(0.85f))
            {
                Text(text = "Order Lists:", fontSize = 25.sp)
            }
            Button(onClick = { navController.navigate(Screen.Insert.route)
            }){
                Text("Add Order")
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            var itemClick = Order(0,"","","",0,"","")
            itemsIndexed(
                items = orderItemsList,
            ){index,item ->
                Card(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(200.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(corner = CornerSize(16.dp)),
                    onClick = {
                        Toast.makeText(
                            contextForToast,"Click on ${item.customerName}.",
                            Toast.LENGTH_SHORT).show()

                    }
                ){
                    Row(Modifier.fillMaxWidth().height(Dp(200f)).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Name : ${item.customerName}\n" +
                                    "Sweet Level : ${item.sweetLevel}\n" +
                                    "Topping : ${item.topping}\n" +
                                    "Cup Size : ${item.cupSize}\n Price : ${item.price} bath\n"+
                            "Cup Quantity : ${item.cupQuantity}",
                            fontSize = 20.sp
                        )
                        TextButton(
                            onClick = {
                                itemClick = item
                                navController.currentBackStackEntry?.savedStateHandle?.set("data",
                                    Order(item.id,item.customerName,item.cupSize,item.topping,item.price,item.sweetLevel,item.cupQuantity)
                                )
                                navController.navigate(Screen.Edit.route)
                            }
                        ) {
                            Text(text= "Edit/Delete")
                        }

                    }
                }
            }
        }
    }
}

fun showallData(orderItemsList:MutableList<Order>,context : Context){
    val createClient = OrderAPI.create()
    createClient.retrieveOrder()
        .enqueue(object : Callback<List<Order>> {
            override fun onResponse(call : Call<List<Order>>,
                                    response: Response<List<Order>>
            ){
                orderItemsList.clear()

                response.body()?.forEach{
                    android.util.Log.d("OrderAPI", "Order: ${it.customerName}, Price: ${it.price}")
                    orderItemsList.add(Order(it.id,it.customerName,it.cupSize,it.topping,it.price,it.sweetLevel,it.cupQuantity))
                }
            }
            override fun onFailure(call : Call<List<Order>>, t : Throwable){
                Toast.makeText(context,"Error onFailure" + t.message,
                    Toast.LENGTH_LONG).show()
            }
        })
}