package com.example.maplink.ui.components

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat
import com.example.maplink.R
import org.maplibre.android.annotations.Icon
import org.maplibre.android.annotations.IconFactory

object FriendMarkerGenerator {

    fun create(
        context: Context,
        name: String,
        online: Boolean
    ): Icon {

        val size = 160

        val bitmap = Bitmap.createBitmap(
            size,
            size,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }

        canvas.drawCircle(
            size / 2f,
            size / 2f,
            58f,
            circlePaint
        )

        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            strokeWidth = 4f
            style = Paint.Style.STROKE
        }

        canvas.drawCircle(
            size / 2f,
            size / 2f,
            62f,
            borderPaint
        )
        circlePaint.setShadowLayer(
            10f,
            0f,
            3f,
            Color.argb(80, 0, 0, 0)
        )

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
            textSize = 54f
            typeface = Typeface.DEFAULT_BOLD
        }

        val initial =
            if (name.isNotBlank())
                name.first().uppercase()
            else
                "?"

        val y =
            size / 2f -
                    (textPaint.descent() + textPaint.ascent()) / 2

        canvas.drawText(
            initial,
            size / 2f,
            y,
            textPaint
        )

        val statusPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color =
                if (online)
                    Color.parseColor("#00C853")
                else
                    Color.GRAY

            style = Paint.Style.FILL
        }

        val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }

        canvas.drawCircle(
            112f,
            48f,
            14f,
            whitePaint
        )
        return IconFactory.getInstance(context)
            .fromBitmap(bitmap)
    }
}