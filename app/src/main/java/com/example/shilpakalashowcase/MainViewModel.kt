package com.example.shilpakalashowcase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shilpakalashowcase.data.AppSettings
import com.example.shilpakalashowcase.data.BrandingMetadata
import com.example.shilpakalashowcase.data.OrderHistory
import com.example.shilpakalashowcase.data.Product
import com.example.shilpakalashowcase.data.UserProfile
import com.example.shilpakalashowcase.data.UserRole
import com.example.shilpakalashowcase.utils.BrandingUtils
import com.example.shilpakalashowcase.utils.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    // ---------------------------------------------------
    // USER STATE
    // ---------------------------------------------------

    private val _currentUser =
        MutableStateFlow<UserProfile?>(null)

    val currentUser: StateFlow<UserProfile?> =
        _currentUser.asStateFlow()

    // ---------------------------------------------------
    // APP SETTINGS
    // ---------------------------------------------------

    private val _settings =
        MutableStateFlow(AppSettings())

    val settings: StateFlow<AppSettings> =
        _settings.asStateFlow()

    // ---------------------------------------------------
    // CAPTURED IMAGE
    // ---------------------------------------------------

    private val _capturedBitmap =
        MutableStateFlow<Bitmap?>(null)

    val capturedBitmap: StateFlow<Bitmap?> =
        _capturedBitmap.asStateFlow()

    fun setCapturedBitmap(bitmap: Bitmap?) {
        _capturedBitmap.value = bitmap
    }

    // ---------------------------------------------------
    // LOAD BITMAP FROM GALLERY URI
    // ---------------------------------------------------

    fun loadBitmapFromUri(
        context: Context,
        uri: Uri
    ) {

        try {

            val bitmap = if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.P
            ) {

                val source =
                    ImageDecoder.createSource(
                        context.contentResolver,
                        uri
                    )

                ImageDecoder.decodeBitmap(source)

            } else {

                @Suppress("DEPRECATION")

                MediaStore.Images.Media.getBitmap(
                    context.contentResolver,
                    uri
                )
            }

            _capturedBitmap.value = bitmap

        } catch (e: Exception) {

            e.printStackTrace()

            Toast.makeText(
                context,
                "Failed to load image",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ---------------------------------------------------
    // PRODUCTS
    // ---------------------------------------------------

    private val _products =
        MutableStateFlow<List<Product>>(emptyList())

    val products: StateFlow<List<Product>> =
        _products.asStateFlow()

    // ---------------------------------------------------
    // ORDER HISTORY
    // ---------------------------------------------------

    private val _orderHistory =
        MutableStateFlow<List<OrderHistory>>(emptyList())

    val orderHistory: StateFlow<List<OrderHistory>> =
        _orderHistory.asStateFlow()

    // ---------------------------------------------------
    // LOGIN ERROR
    // ---------------------------------------------------

    private val _loginError =
        MutableStateFlow<String?>(null)

    val loginError: StateFlow<String?> =
        _loginError.asStateFlow()

    // ---------------------------------------------------
    // INIT
    // ---------------------------------------------------

    init {
        loadInitialData()
    }

    // ---------------------------------------------------
    // LOAD DEMO DATA
    // ---------------------------------------------------

    private fun loadInitialData() {

        _products.value = listOf(

            Product(
                id = "1",
                sellerId = "art_01",
                sellerName = "RUDRESH",
                name = "Clay Ganesha",
                description = "Traditional clay art",
                imageUrl = "https://via.placeholder.com/600",
                originalImageUrl = "",
                price = 1250.0,
                woodType = "Clay",
                material = "Clay",
                category = "Sculpture"
            ),

            Product(
                id = "2",
                sellerId = "art_02",
                sellerName = "RUDRESH",
                name = "Modern Canvas",
                description = "Oil on canvas",
                imageUrl = "https://via.placeholder.com/600",
                originalImageUrl = "",
                price = 5400.0,
                woodType = "Canvas",
                material = "Paint",
                category = "Painting"
            )
        )

        _orderHistory.value = listOf(

            OrderHistory(
                id = "order_1",
                userId = "user_1",
                productId = "1",
                productName = "Clay Ganesha",
                artisanName = "RUDRESH",
                amount = 1250.0
            ),

            OrderHistory(
                id = "order_2",
                userId = "user_1",
                productId = "2",
                productName = "Modern Canvas",
                artisanName = "RUDRESH",
                amount = 5400.0
            )
        )
    }

    // ---------------------------------------------------
    // LOGIN
    // ---------------------------------------------------

    fun login(
        email: String,
        role: UserRole,
        onComplete: (Boolean) -> Unit
    ) {

        _loginError.value = null

        viewModelScope.launch {

            try {

                val profile = UserProfile(
                    uid = "user_${email.hashCode()}",
                    name = Config.DEFAULT_ARTISAN_NAME,
                    email = email,
                    role = role
                )

                _currentUser.value = profile

                onComplete(true)

            } catch (e: Exception) {

                Log.e("MainViewModel", "Login Error", e)

                _loginError.value =
                    "Login failed. Please try again."

                onComplete(false)
            }
        }
    }

    // ---------------------------------------------------
    // LOGOUT
    // ---------------------------------------------------

    fun logout() {
        _currentUser.value = null
    }

    // ---------------------------------------------------
    // UPDATE PROFILE
    // ---------------------------------------------------

    fun updateProfile(updatedUser: UserProfile) {
        _currentUser.value = updatedUser
    }

    // ---------------------------------------------------
    // UPDATE SETTINGS
    // ---------------------------------------------------

    fun updateSettings(newSettings: AppSettings) {
        _settings.value = newSettings
    }

    // ---------------------------------------------------
    // SWITCH ROLE
    // ---------------------------------------------------

    fun switchAccount(role: UserRole) {

        _currentUser.value =
            _currentUser.value?.copy(
                role = role
            )
    }

    // ---------------------------------------------------
    // SAVE PRODUCT
    // ---------------------------------------------------

    fun saveAndUploadProduct(
        context: Context,
        bitmap: Bitmap,
        metadata: BrandingMetadata,
        onComplete: () -> Unit
    ) {

        viewModelScope.launch(Dispatchers.IO) {

            try {

                val brandedFile =
                    BrandingUtils.processAndBrandImage(
                        context = context,
                        originalBitmap = bitmap,
                        metadata = metadata
                    )

                withContext(Dispatchers.Main) {

                    if (brandedFile != null) {

                        Toast.makeText(
                            context,
                            "Product branded successfully!",
                            Toast.LENGTH_SHORT
                        ).show()

                        val newProduct = Product(
                            id = System.currentTimeMillis().toString(),
                            sellerId = _currentUser.value?.uid ?: "",
                            sellerName = metadata.artisanName,
                            name = metadata.productName,
                            description = metadata.woodType,
                            imageUrl = brandedFile.absolutePath,
                            originalImageUrl = brandedFile.absolutePath,
                            price = metadata.price.toDoubleOrNull() ?: 0.0,
                            woodType = metadata.woodType,
                            material = metadata.woodType,
                            category = "Handicraft"
                        )

                        _products.value =
                            listOf(newProduct) + _products.value

                        onComplete()

                    } else {

                        Toast.makeText(
                            context,
                            "Branding failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {

                    Toast.makeText(
                        context,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}