package project.android.demo.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.os.Bundle
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val doeGroup = "group-id1"
        val smithGroup = "group-id2"
        val channelId1 = "channel-id1"
        val channelId2 = "channel-id2"
        val channelId3 = "channel-id3"
        val channelId4 = "channel-id4"

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannelGroup(NotificationChannelGroup(doeGroup, "Doe Family Group"))
        manager.createNotificationChannelGroup(NotificationChannelGroup(smithGroup, "Smith Family Group"))

        manager.createNotificationChannel(createNotificationChannel(channelId1, "John Doe", doeGroup))
        manager.createNotificationChannel(createNotificationChannel(channelId2, "Jane Doe", doeGroup))

        manager.createNotificationChannel(createNotificationChannel(channelId3, "John Smith", smithGroup))
        manager.createNotificationChannel(createNotificationChannel(channelId4, "Jane Smith", smithGroup))


        btnSend1.setOnClickListener {
            manager.notify(
                System.currentTimeMillis().toInt(),
                createNotificationWithGroup(doeGroup, channelId1, "John Doe", "Hello")
            )
        }

        btnSend2.setOnClickListener {
            manager.notify(
                System.currentTimeMillis().toInt(),
                createNotificationWithGroup(doeGroup, channelId2, "Jane Doe", "Hello")
            )
        }

        btnSend3.setOnClickListener {
            manager.notify(
                System.currentTimeMillis().toInt(),
                createNotificationWithGroup(smithGroup, channelId3, "John Smith", "Hello")
            )
        }

        btnSend4.setOnClickListener {
            manager.notify(
                System.currentTimeMillis().toInt(),
                createNotificationWithPerson(smithGroup, channelId4, "Jane Smith", "Hello")
            )
        }

        btnSettings1.setOnClickListener {
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, channelId1)
            }
            startActivity(intent)
        }

        btnSettings2.setOnClickListener {
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, channelId2)
            }
            startActivity(intent)
        }

        btnSettings3.setOnClickListener {
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, channelId3)
            }
            startActivity(intent)
        }

        btnSettings4.setOnClickListener {
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, channelId4)
            }
            startActivity(intent)
        }
    }

    private fun createNotificationWithPerson(
        groupId: String,
        channelId: String,
        personName: String,
        messageText: String
    ): Notification {

        val sender = Person.Builder().setName(personName).build()
        val message = Notification.MessagingStyle.Message(messageText, System.currentTimeMillis(), sender)

        val file = File(filesDir, "shared/picture8640286453615262451.png")
        val uri = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.FileProvider", file)

        message.setData("image/", uri)

        val style = Notification.MessagingStyle(sender).addMessage(message)

        return Notification.Builder(this, channelId).setStyle(style).setGroup(groupId)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(this, R.color.colorAccent))
            .setAutoCancel(true).build()
    }


    private fun createNotificationWithGroup(
        groupId: String,
        channelId: String,
        title: String,
        text: String
    ): Notification {

        return Notification.Builder(this, channelId).setGroup(groupId).setContentTitle(title).setContentText(text)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(this, R.color.colorAccent))
            .setAutoCancel(true).build()
    }

    private fun createNotificationChannel(
        channelId: String,
        channelName: String,
        groupId: String
    ): NotificationChannel {

        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        channel.group = groupId
        return channel
    }








    private fun createFile(): File {
        val bitmapFactory = RandomBitmapFactory()
        val randomBitmap = bitmapFactory.createRandomBitmap()

        val sharedFolder = File(filesDir, "shared")
        sharedFolder.mkdirs()

        val sharedFile = File.createTempFile("picture", ".png", sharedFolder)
        sharedFile.createNewFile()

        writeBitmap(sharedFile, randomBitmap)
        return sharedFile
    }

    private fun writeBitmap(
        destination: File,
        bitmap: Bitmap
    ) {
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(destination)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            close(outputStream)
        }
    }

    private fun close(closeable: Closeable?) {
        if (closeable == null) return
        try {
            closeable.close()
        } catch (ignored: IOException) {
        }

    }

    internal inner class RandomBitmapFactory {

        private val BITMAP_SIZE = 256
        private val MAX_STROKE_WIDTH = 20f
        private val STROKE_COUNT = 50

        fun createRandomBitmap(): Bitmap {
            val rnd = Random(System.currentTimeMillis())

            val paint = Paint(ANTI_ALIAS_FLAG)
            val bitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            for (i in 0 until STROKE_COUNT) {
                val startX = rnd.nextInt(BITMAP_SIZE)
                val startY = rnd.nextInt(BITMAP_SIZE)
                val stopX = rnd.nextInt(BITMAP_SIZE)
                val stopY = rnd.nextInt(BITMAP_SIZE)
                val width = rnd.nextFloat() * MAX_STROKE_WIDTH
                val color = rnd.nextInt()

                paint.setColor(color)
                paint.setStrokeWidth(width)

                canvas.drawRect(startX.toFloat(), startY.toFloat(), stopX.toFloat(), stopY.toFloat(), paint)
            }

            return bitmap
        }
    }
}
