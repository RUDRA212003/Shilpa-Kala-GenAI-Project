package com.example.shilpakalashowcase.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.shilpakalashowcase.MainViewModel
import com.example.shilpakalashowcase.data.BrandingMetadata
import com.example.shilpakalashowcase.utils.BrandingUtils
import com.example.shilpakalashowcase.utils.Config
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CameraScreen(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    val user by viewModel.currentUser.collectAsState()
    
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashMode by remember { mutableStateOf(ImageCapture.FLASH_MODE_OFF) }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Camera Preview
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    previewView = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // GUIDED OVERLAY (Canvas)
        CameraOverlay()

        // UI Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Flash Toggle
                IconButton(onClick = {
                    flashMode = if (flashMode == ImageCapture.FLASH_MODE_OFF) 
                        ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
                }) {
                    Icon(
                        if (flashMode == ImageCapture.FLASH_MODE_ON) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = Color.White
                    )
                }

                // Capture Button
                Button(
                    onClick = {
                        takePhoto(
                            context = context,
                            imageCapture = imageCapture,
                            executor = cameraExecutor,
                            onImageCaptured = { bitmap ->
                                // For now, we brand with default metadata and navigate back
                                val metadata = BrandingMetadata(
                                    artisanName = user?.name ?: Config.DEFAULT_ARTISAN_NAME,
                                    productName = "New Creation",
                                    woodType = user?.specialization ?: "Handicraft",
                                    price = "0",
                                    artisanLocation = user?.location ?: "Karnataka"
                                )
                                BrandingUtils.processAndBrandImage(context, bitmap, metadata)
                                (context as? android.app.Activity)?.runOnUiThread {
                                    Toast.makeText(context, "Product Captured & Branded!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                            }
                        )
                    },
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Icon(Icons.Default.Camera, contentDescription = "Capture", tint = Color.Black, modifier = Modifier.size(40.dp))
                }

                // Flip Camera
                IconButton(onClick = {
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK)
                        CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
                }) {
                    Icon(Icons.Default.FlipCameraAndroid, contentDescription = "Flip", tint = Color.White)
                }
            }
        }
    }

    // Initialize Camera
    LaunchedEffect(lensFacing, flashMode) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView?.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder()
            .setFlashMode(flashMode)
            .setTargetRotation(previewView?.display?.rotation ?: 0)
            .build()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (exc: Exception) {
            Log.e("CameraScreen", "Use case binding failed", exc)
        }
    }
}

@Composable
fun CameraOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 4f
        val color = Color.White.copy(alpha = 0.5f)
        
        // Center rectangle guide for product
        val rectSize = size.width * 0.7f
        drawRoundRect(
            color = color,
            topLeft = Offset((size.width - rectSize) / 2, (size.height - rectSize) / 2.5f),
            size = Size(rectSize, rectSize),
            style = Stroke(width = strokeWidth),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f)
        )

        // Rule of thirds / Alignment lines
        drawLine(
            color = color,
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height),
            strokeWidth = 2f
        )
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2.5f + rectSize / 2),
            end = Offset(size.width, size.height / 2.5f + rectSize / 2),
            strokeWidth = 2f
        )
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture?,
    executor: ExecutorService,
    onImageCaptured: (Bitmap) -> Unit
) {
    val capture = imageCapture ?: return

    capture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val bitmap = imageProxyToBitmap(image)
                onImageCaptured(bitmap)
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraScreen", "Photo capture failed: ${exception.message}", exception)
            }
        }
    )
}

private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    
    // Fix rotation
    val matrix = Matrix()
    matrix.postRotate(image.imageInfo.rotationDegrees.toFloat())
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}
