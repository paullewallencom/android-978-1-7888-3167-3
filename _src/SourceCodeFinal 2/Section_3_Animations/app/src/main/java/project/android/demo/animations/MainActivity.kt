package project.android.demo.animations

import android.graphics.*
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cropFactor = 0.95f // crop to 92% of the original size
        val listener = ImageDecoder.OnHeaderDecodedListener { decoder, info, source ->

            val paint = Paint().apply {
                isAntiAlias = true
                color = Color.TRANSPARENT // draw a transparent color to "erase" unwanted areas
                xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC) // replace destination pixels with source pixels
            }

            decoder.setPostProcessor { canvas ->

                val path = Path().apply {
                    fillType = Path.FillType.INVERSE_EVEN_ODD // draw outside of the path
                }

                val width = canvas.width.toFloat()
                val height = canvas.height.toFloat()

                path.addRoundRect(
                    width - width * cropFactor,
                    height - height * cropFactor,
                    width * cropFactor,
                    height * cropFactor,
                    width * cropFactor,
                    height * cropFactor,
                    Path.Direction.CW
                )
                canvas.drawPath(path, paint)
                PixelFormat.TRANSLUCENT // tell the system to allow translucency
            }
        }

        val source = ImageDecoder.createSource(resources, R.drawable.animatedgif)
        val drawable = ImageDecoder.decodeDrawable(source, listener)

        imageView.setImageDrawable(drawable)


        btnStart.setOnClickListener {
            val animatedDrawable = drawable as? AnimatedImageDrawable
            animatedDrawable?.repeatCount = 1
            animatedDrawable?.start()
        }

        btnStop.setOnClickListener {
            val animatedDrawable = drawable as? AnimatedImageDrawable
            animatedDrawable?.stop()
        }
    }
}
