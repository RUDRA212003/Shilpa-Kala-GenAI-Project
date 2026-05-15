package com.example.shilpakalashowcase.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import com.example.shilpakalashowcase.data.BrandingMetadata
import java.io.File
import java.io.FileOutputStream

object BrandingUtils {

    /**
     * Processes the art/craft image and adds professional branding overlays:
     * - "Handmade in Karnataka" gold seal
     * - Gradient dark overlay for readability
     * - Luxury price tag (Terracotta)
     * - Artisan signature and Medium info
     */
    fun processAndBrandImage(
        context: Context,
        originalBitmap: Bitmap,
        metadata: BrandingMetadata
    ): File? {

        val width = originalBitmap.width
        val height = originalBitmap.height

        // Create a copy of the bitmap to draw on
        val brandedBitmap = Bitmap.createBitmap(
            width,
            height,
            originalBitmap.config ?: Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(brandedBitmap)

        // 1. Draw original image
        canvas.drawBitmap(originalBitmap, 0f, 0f, null)

        // 2. Gradient overlay at bottom for text contrast
        val gradientPaint = Paint().apply {
            shader = LinearGradient(
                0f,
                height * 0.65f,
                0f,
                height.toFloat(),
                Color.TRANSPARENT,
                Color.BLACK,
                Shader.TileMode.CLAMP
            )
        }

        canvas.drawRect(
            0f,
            height * 0.65f,
            width.toFloat(),
            height.toFloat(),
            gradientPaint
        )

        // 3. Branding Seal (Gold)
        val sealPaint = Paint().apply {
            color = Color.parseColor("#FFD700") // Gold
            textSize = width * 0.045f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC)
            isAntiAlias = true
            setShadowLayer(4f, 2f, 2f, Color.BLACK)
        }

        canvas.drawText(
            Config.BRANDING_LOGO_TEXT,
            50f,
            120f,
            sealPaint
        )

        // 4. Luxury Price Tag (Terracotta themed)
        val priceTagPaint = Paint().apply {
            color = Color.parseColor("#BF360C") // Deep Terracotta
            isAntiAlias = true
        }

        val priceText = "₹${metadata.price}"
        val textPaint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            textSize = width * 0.055f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }

        val priceWidth = textPaint.measureText(priceText) + 60f
        val margin = 50f
        val currentY = height - 100f

        canvas.drawRoundRect(
            width - priceWidth - margin,
            currentY - 90f,
            width - margin,
            currentY + 30f,
            30f,
            30f,
            priceTagPaint
        )

        // Draw Price Text
        canvas.drawText(
            priceText,
            width - priceWidth - margin + 30f,
            currentY,
            textPaint
        )

        // 5. Product Name and Details
        // Product Name (White)
        val namePaint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            textSize = width * 0.07f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }

        canvas.drawText(
            metadata.productName,
            margin,
            currentY - 110f,
            namePaint
        )

        // Artisan + Medium info (Light Grey)
        val detailPaint = Paint().apply {
            color = Color.parseColor("#DDDDDD")
            textSize = width * 0.038f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        // Updated label to "Medium" for broader Art/Craft appeal
        canvas.drawText(
            "Artisan: ${metadata.artisanName} | Medium: ${metadata.woodType}",
            margin,
            currentY - 45f,
            detailPaint
        )

        // 6. Save the branded image to the local filesystem
        return try {
            val file = File(
                context.getExternalFilesDir(null),
                "branded_art_${System.currentTimeMillis()}.jpg"
            )
            val out = FileOutputStream(file)
            brandedBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
            out.flush()
            out.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
