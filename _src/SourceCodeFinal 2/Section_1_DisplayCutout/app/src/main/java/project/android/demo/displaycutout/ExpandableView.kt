package project.android.demo.displaycutout

import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View

@TargetApi(Build.VERSION_CODES.P)
class ExpandableView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint()
    private val paintForCutout = Paint()
    private val path = Path()
    private val pathForCutout = Path()
    private lateinit var cutoutRect: Rect

    private var expanded = false

    init {
        paint.style = Paint.Style.FILL
        paint.color = ContextCompat.getColor(getContext(), R.color.colorPrimaryDark)
        paintForCutout.style = Paint.Style.FILL
        paintForCutout.color = ContextCompat.getColor(getContext(), R.color.colorPrimaryDark)

        setOnApplyWindowInsetsListener { _, insets ->

            val cutout = insets.displayCutout

            if (cutout != null) {

                val cutoutRect = cutout.boundingRects?.first()

                if (cutoutRect != null) {
                    this.cutoutRect = cutoutRect

                    // adjust the height of this view based on the cutout size
                    val layoutParams = this.layoutParams
                    layoutParams.height = cutoutRect.height() * 2
                    this.layoutParams = layoutParams
                }
            }

            insets.consumeSystemWindowInsets()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val rectHeight = height.toFloat() - cutoutRect.height()

        path.reset()
        path.addRect(0f, 0f, width.toFloat(), rectHeight, Path.Direction.CW)

        pathForCutout.reset()
        pathForCutout.moveTo(cutoutRect.left.toFloat(), rectHeight)
        pathForCutout.lineTo(cutoutRect.left.toFloat(), cutoutRect.bottom.toFloat() + rectHeight)
        pathForCutout.lineTo(cutoutRect.right.toFloat(), cutoutRect.bottom.toFloat() + rectHeight)
        pathForCutout.lineTo(cutoutRect.right.toFloat(), rectHeight)

        path.addPath(pathForCutout)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawPath(path, paint)
    }

    fun toggle() {
        expanded = if (expanded) {
            animate(cutoutRect.height() * 3, cutoutRect.height() * 2)
            false
        } else {
            animate(cutoutRect.height() * 2, cutoutRect.height() * 3)
            true
        }
    }

    private fun animate(from: Int, to: Int) {
        val anim = ValueAnimator.ofInt(from, to)
        anim.addUpdateListener { valueAnimator ->
            val height = valueAnimator.animatedValue as Int
            val layoutParams = this.layoutParams
            layoutParams.height = height
            this.layoutParams = layoutParams
        }
        anim.duration = 800
        anim.start()
    }
}
