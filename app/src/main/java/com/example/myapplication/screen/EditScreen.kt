package com.example.myapplication.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.api.OrderAPI
import com.example.myapplication.database.Order
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(navController: NavHostController) {
    var deleteDialog by remember { mutableStateOf(false) }
    val data = navController.previousBackStackEntry?.savedStateHandle?.get<Order>("data") ?:
    Order(0, "", "", "", 0, "","")
    var id by remember { mutableStateOf(data.id) }
    // State variables
    var orderId by remember { mutableStateOf(data.id) }
    var textFieldCustomerName by remember { mutableStateOf(data.customerName) }
    var textFieldCupQuantity by remember { mutableStateOf(data.cupQuantity) } // Default to 1
    var selectedCup by remember { mutableStateOf(data.cupSize) }
    var selectedSweet by remember { mutableStateOf(data.sweetLevel) }

    // Parse existing toppings from comma-separated string
    val initialToppings = if (data.topping.isNotEmpty()) data.topping.split(",") else emptyList()
    var selectedToppings by remember { mutableStateOf(initialToppings) }

    val toppingList = listOf("Brownie" to 10, "Jelly" to 5)
    val contextForToast = LocalContext.current.applicationContext

    val createClient = OrderAPI.create()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(25.dp))

        // Header
        Text(
            text = "Edit Order",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Customer Name Field
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            value = textFieldCustomerName,
            onValueChange = { textFieldCustomerName = it },
            label = { Text(text = "Customer Name") }
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            value = textFieldCupQuantity,
            onValueChange = { textFieldCupQuantity = it },
            label = { Text(text = "Number of Cups") }
        )

        // Cup Size Selection
        Text(
            text = "Cup Size:",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("S", "M", "L").forEach { size ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedCup = size },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedCup == size,
                        onClick = { selectedCup = size }
                    )
                    Text(
                        text = when(size) {
                            "S" -> "Small (S)"
                            "M" -> "Medium (M)"
                            "L" -> "Large (L)"
                            else -> size
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sweet Level Selection
        Text(
            text = "Sweet Level:",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        SweetDropdownForEdit(initialValue = selectedSweet) { level ->
            selectedSweet = level
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toppings Section
        Text(
            text = "Toppings:",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        CheckboxGroupForEdit(
            items = toppingList.map { it.first },
            initialSelected = initialToppings
        ) { newSelectedItems ->
            selectedToppings = newSelectedItems
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calculate price preview
        val price = calculateEditPrice(
            selectedCup,
            textFieldCupQuantity.toIntOrNull() ?: 1,  // Convert to Int or default to 1
            selectedToppings,
            toppingList
        )

        Text(
            text = "Total Price: ${price} baht",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                modifier = Modifier.width(100.dp),
                onClick = { deleteDialog = true }
            ) {
                Text(text = "Delete")
            }

            Button(
                modifier = Modifier.width(100.dp),
                onClick = {
                    createClient.updateOrder(
                        id,
                        textFieldCustomerName,
                        selectedCup,
                        selectedToppings.joinToString(","),
                        textFieldCupQuantity.ifEmpty { "1" },
                        price,
                        selectedSweet
                    ).enqueue(object : Callback<Order> {
                        override fun onResponse(call: Call<Order>, response: Response<Order>) {
                            if (response.isSuccessful) {
                                Toast.makeText(contextForToast, "Successfully Updated", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(contextForToast, "Failed to Update", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Order>, t: Throwable) {
                            Toast.makeText(contextForToast, "Failed to Update: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                    navController.navigateUp()
                }
            ) {
                Text(text = "Update")
            }

            Button(
                modifier = Modifier.width(100.dp),
                onClick = { navController.navigate(Screen.Home.route) }
            ) {
                Text(text = "Cancel")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }

    // Delete Confirmation Dialog
    if (deleteDialog) {
        AlertDialog(
            onDismissRequest = { deleteDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteDialog = false
                        createClient.deleteOrder(id)
                            .enqueue(object : Callback<Order> {
                                override fun onResponse(call: Call<Order>, response: Response<Order>) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(contextForToast, "Successfully Deleted", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(contextForToast, "Failed to Delete", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<Order>, t: Throwable) {
                                    Toast.makeText(contextForToast, "Failed to Delete: ${t.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                        navController.navigate(Screen.Home.route)
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this order for ${data.customerName}?") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SweetDropdownForEdit(initialValue: String, onSweetSelected: (String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val sweetList = listOf(
        "0%",
        "25%",
        "50%",
        "75%",
        "100%"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedSweet by remember { mutableStateOf(initialValue) }

    ExposedDropdownMenuBox(
        modifier = Modifier.fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
            if (keyboardController != null) {
                keyboardController.hide()
            }
        }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            value = selectedSweet,
            onValueChange = {},
            label = { Text("Sweet level") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            sweetList.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        selectedSweet = selectionOption
                        onSweetSelected(selectionOption)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }

    // Call the callback once initially with the default value
    LaunchedEffect(Unit) {
        onSweetSelected(selectedSweet)
    }
}

@Composable
fun CheckboxGroupForEdit(
    items: List<String>,
    initialSelected: List<String>,
    onSelectionChange: (List<String>) -> Unit
) {
    var selectedItems = remember { mutableStateListOf<String>() }

    // Initialize with the existing selections
    LaunchedEffect(key1 = initialSelected) {
        selectedItems.clear()
        selectedItems.addAll(initialSelected)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        if (selectedItems.contains(item)) {
                            selectedItems.remove(item)
                        } else {
                            selectedItems.add(item)
                        }
                        onSelectionChange(selectedItems.toList())
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selectedItems.contains(item),
                    onCheckedChange = { checked ->
                        if (checked) {
                            selectedItems.add(item)
                        } else {
                            selectedItems.remove(item)
                        }
                        onSelectionChange(selectedItems.toList())
                    }
                )
                Text(
                    text = item,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

fun calculateEditPrice(cupSize: String, quantity: Int, selectedToppings: List<String>, toppingList: List<Pair<String, Int>>): Int {
    val basePrice = when (cupSize) {
        "S" -> 20
        "M" -> 30
        "L" -> 40
        else -> 25
    }
    val toppingPrice = selectedToppings.sumOf { topping -> toppingList.find { it.first == topping }?.second ?: 0 }
    return (basePrice + toppingPrice) * quantity
}