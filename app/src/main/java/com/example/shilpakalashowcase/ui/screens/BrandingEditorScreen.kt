package com.example.shilpakalashowcase.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shilpakalashowcase.MainViewModel
import com.example.shilpakalashowcase.data.BrandingMetadata

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandingEditorScreen(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val bitmap by viewModel.capturedBitmap.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    var productName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var woodType by remember { mutableStateOf(user?.specialization ?: "") }
    var description by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Branding Editor") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        IconButton(
                            onClick = {
                                bitmap?.let {
                                    isSaving = true
                                    val metadata = BrandingMetadata(
                                        artisanName = user?.name ?: "RUDRESH",
                                        productName = productName,
                                        woodType = woodType,
                                        price = price,
                                        artisanLocation = user?.location ?: "Karnataka"
                                    )
                                    viewModel.saveAndUploadProduct(context, it, metadata) {
                                        isSaving = false
                                        navController.navigate("portfolio") {
                                            popUpTo("dashboard")
                                        }
                                    }
                                }
                            },
                            enabled = productName.isNotEmpty() && price.isNotEmpty()
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            bitmap?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Captured Product",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Product Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = productName,
                onValueChange = { productName = it },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price (₹)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(
                    value = woodType,
                    onValueChange = { woodType = it },
                    label = { Text("Material/Wood") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    bitmap?.let {
                        isSaving = true
                        val metadata = BrandingMetadata(
                            artisanName = user?.name ?: "RUDRESH",
                            productName = productName,
                            woodType = woodType,
                            price = price,
                            artisanLocation = user?.location ?: "Karnataka"
                        )
                        viewModel.saveAndUploadProduct(context, it, metadata) {
                            isSaving = false
                            navController.navigate("portfolio") {
                                popUpTo("dashboard")
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving && productName.isNotEmpty() && price.isNotEmpty(),
                shape = MaterialTheme.shapes.medium
            ) {
                if (isSaving) {
                    Text("Processing...")
                } else {
                    Text("Apply Branding & Save to Portfolio")
                }
            }
        }
    }
}
