package com.example.interviewform2

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import java.util.Random
import kotlin.math.sin

class BubbleAnimationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bubbles = mutableListOf<Bubble>()
    private val random = Random()

    // Colores del gradiente
    private val gradientColors = intArrayOf(
        Color.parseColor("#6366F1"),
        Color.parseColor("#8B5CF6")
    )

    data class Bubble(
        var x: Float,
        var y: Float,
        val radius: Float,
        val speed: Float,
        val alpha: Int
    )

    init {
        // Crear burbujas iniciales
        repeat(8) {
            bubbles.add(createRandomBubble())
        }

        // Animar burbujas
        startBubbleAnimation()
    }

    private fun createRandomBubble(): Bubble {
        return Bubble(
            x = random.nextFloat() * width,
            y = height + 100f,
            radius = (20 + random.nextInt(40)).toFloat(),
            speed = (2 + random.nextInt(4)).toFloat(),
            alpha = 30 + random.nextInt(50)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas ?: return

        // Dibujar gradiente de fondo
        val gradient = LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            gradientColors, null, Shader.TileMode.CLAMP
        )
        paint.shader = gradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // Dibujar burbujas
        paint.shader = null
        bubbles.forEach { bubble ->
            paint.color = Color.argb(bubble.alpha, 255, 255, 255)
            canvas.drawCircle(bubble.x, bubble.y, bubble.radius, paint)
        }
    }

    private fun startBubbleAnimation() {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = Long.MAX_VALUE
        animator.repeatCount = ValueAnimator.INFINITE
        animator.addUpdateListener {
            updateBubbles()
            invalidate()
        }
        animator.start()
    }

    private fun updateBubbles() {
        bubbles.forEach { bubble ->
            bubble.y -= bubble.speed
            bubble.x += sin(bubble.y * 0.01f) * 2f

            // Reiniciar burbuja si sale de pantalla
            if (bubble.y < -bubble.radius) {
                bubble.y = height + bubble.radius
                bubble.x = random.nextFloat() * width
            }
        }
    }
}