package com.example.shilpakalashowcase.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.shilpakalashowcase.MainViewModel
import com.example.shilpakalashowcase.data.Product
import com.example.shilpakalashowcase.data.UserRole
import com.example.shilpakalashowcase.utils.Config

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, viewModel: MainViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val products by viewModel.products.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("SHILPA-KALA", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp, color = MaterialTheme.colorScheme.primary)
                        Text("Digital Portfolio Assistant", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                            Icon(Icons.Default.Person, "Profile", modifier = Modifier.padding(8.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            if (user?.role == UserRole.SELLER) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate("seller_dashboard") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Add Product") }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.GridView, null) },
                    label = { Text("Showcase") },
                    selected = true,
                    onClick = { }
                )
                
                if (user?.role == UserRole.SELLER) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Collections, null) },
                        label = { Text("Portfolio") },
                        selected = false,
                        onClick = { navController.navigate("portfolio") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.AutoFixHigh, null) }, // Changed icon to represent Assistant
                        label = { Text("Assistant") }, // Renamed from Capture
                        selected = false,
                        onClick = { navController.navigate("seller_dashboard") } // Navigates to assistant dashboard
                    )
                }

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, null) },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { navController.navigate("settings") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            CategoryList()

            if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalItemSpacing = 16.dp
                ) {
                    items(products, key = { it.id }) { product ->
                        ProductCard(product)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryList() {
    val categories = listOf("All", "Sculpture", "Painting", "Drawing", "Pottery", "Metal Art")
    var selected by remember { mutableStateOf("All") }
    
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories.size) { index ->
            val cat = categories[index]
            val isSelected = selected == cat
            FilterChip(
                selected = isSelected,
                onClick = { selected = cat },
                label = { Text(cat) },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
fun ProductCard(product: Product) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp, max = 250.dp)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Surface(
                    modifier = Modifier.padding(12.dp).align(Alignment.TopEnd),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f)
                ) {
                    Text(
                        product.category,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("by ${product.sellerName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "₹${product.price.toInt()}", 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.ExtraBold, 
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Button(
                        onClick = {
                            val phoneNumber = Config.DEFAULT_WHATSAPP_NUMBER
                            val message = "Hi, I'm interested in this art product: ${product.name}"
                            val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}"
                            
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(url)
                                setPackage("com.whatsapp")
                            }
                            
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                try {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                } catch (e2: Exception) {
                                    Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("BUY", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
