package com.example.myapplication.screen


import android.widget.Space
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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
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


@Composable
fun InsertScreen(navController: NavHostController) {
    var textFieldCustomerName by remember { mutableStateOf("") }
    var textFieldCupQuantity by remember { mutableStateOf("") }
    val toppingList = listOf("Brownie" to 10, "Jelly" to 5)
    val contextForToast = LocalContext.current

    // Selectors
    var selectedCup by remember { mutableStateOf("M") } // Default value
    var selectedSweet by remember { mutableStateOf("50%") } // Default value
    var selectedToppings by remember { mutableStateOf(emptyList<String>()) }

    // UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(25.dp))

        // Header
        Text(
            text = "Insert New Order",
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

        // Cup Quantity Field
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            value = textFieldCupQuantity,
            onValueChange = { textFieldCupQuantity = it },
            label = { Text(text = "Number of Cups") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Cup Size Selection
        Text(
            text = "Cup Size:",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        ImprovedCupSelection { size ->
            selectedCup = size
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sweet Level Dropdown
        Text(
            text = "Sweet Level:",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        ImprovedSweetDropdown { level ->
            selectedSweet = level
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toppings Section
        Text(
            text = "Toppings:",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        ImprovedCheckboxGroup(items = toppingList.map { it.first }) { newSelectedItems ->
            selectedToppings = newSelectedItems
        }

        Spacer(modifier = Modifier.weight(1f))

        // Calculate price preview
        val price = calculatePrice(
            selectedCup,
            textFieldCupQuantity.toIntOrNull() ?: 1,
            selectedToppings,
            toppingList
        )

        Text(
            text = "Total Price: ${price} baht",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Buttons Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                modifier = Modifier.width(140.dp),
                onClick = {
                    val createClient = OrderAPI.create()
                    createClient.insertOrder(
                        textFieldCustomerName,
                        selectedCup,
                        selectedToppings.joinToString(","),
                        textFieldCupQuantity.ifEmpty { "1" },
                        price,
                        selectedSweet
                    ).enqueue(object : Callback<Order> {
                        override fun onResponse(call: Call<Order>, response: Response<Order>) {
                            val message = if (response.isSuccessful) "Successfully inserted" else "Failed to insert"
                            Toast.makeText(contextForToast, message, Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(call: Call<Order>, t: Throwable) {
                            Toast.makeText(contextForToast, "Failed to insert: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                    navController.navigateUp()
                }
            ) {
                Text(text = "Save")
            }

            Button(
                modifier = Modifier.width(140.dp),
                onClick = {
                    navController.navigate(Screen.Home.route)
                }
            ) {
                Text(text = "Cancel")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun ImprovedCupSelection(onCupSelected: (String) -> Unit) {
    var selectedCup by remember { mutableStateOf("M") } // Default to Medium

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
                    .clickable {
                        selectedCup = size
                        onCupSelected(size)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedCup == size,
                    onClick = {
                        selectedCup = size
                        onCupSelected(size)
                    }
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImprovedSweetDropdown(onSweetSelected: (String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val sweetList = listOf(
        "0%",
        "25%",
        "50%",
        "75%",
        "100%"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedSweet by remember { mutableStateOf(sweetList[2]) } // Default to 50%

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
fun ImprovedCheckboxGroup(
    items: List<String>,
    onSelectionChange: (List<String>) -> Unit
) {
    var selectedItems = remember { mutableStateListOf<String>() }

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

fun calculatePrice(cupSize: String, quantity: Int, selectedToppings: List<String>, toppingList: List<Pair<String, Int>>): Int {
    val basePrice = when (cupSize) {
        "S" -> 20
        "M" -> 30
        "L" -> 40
        else -> 25
    }
    val toppingPrice = selectedToppings.sumOf { topping -> toppingList.find { it.first == topping }?.second ?: 0 }
    return (basePrice + toppingPrice) * quantity
}