package com.example.shilpakalashowcase.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shilpakalashowcase.MainViewModel
import com.example.shilpakalashowcase.data.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: MainViewModel) {
    val settings by viewModel.settings.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text("General", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            
            ListItem(
                headlineContent = { Text("Notifications") },
                trailingContent = {
                    Switch(
                        checked = settings.notificationsEnabled,
                        onCheckedChange = { viewModel.updateSettings(settings.copy(notificationsEnabled = it)) }
                    )
                }
            )
            
            ListItem(
                headlineContent = { Text("Language") },
                supportingContent = { Text(settings.language) },
                trailingContent = { Icon(Icons.Default.Language, null) },
                modifier = Modifier.clickable { /* TODO: Language picker */ }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Account", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            
            ListItem(
                headlineContent = { Text("Switch Account Role") },
                supportingContent = { Text("Current: ${user?.role}") },
                trailingContent = { Icon(Icons.Default.SwitchAccount, null) },
                modifier = Modifier.clickable {
                    val newRole = if (user?.role == UserRole.BUYER) UserRole.SELLER else UserRole.BUYER
                    viewModel.switchAccount(newRole)
                }
            )

            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { 
                    viewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Logout")
            }
        }
    }
}
