import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object Utils {
    suspend fun downloadImage(context: Context, imageUrl: String, fileName: String) {
        withContext(Dispatchers.IO) {
            try {
                val inputStream =
                    getImageInputStream(imageUrl) ?: throw IOException("Failed to open connection.")
                val savedUri = saveImage(context, inputStream, fileName)
                savedUri?.let { scanFile(context, it) }
            } catch (_: Exception) {
            }
        }
    }

    @Throws(IOException::class)
    private fun getImageInputStream(imageUrl: String): InputStream? {
        val connection = (URL(imageUrl).openConnection() as HttpURLConnection).apply {
            doInput = true
            connect()
        }
        return connection.inputStream
    }

    private fun saveImage(
        context: Context,
        inputStream: InputStream,
        fileName: String
    ): Uri? {
        val resolver = context.contentResolver

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveToMediaStore(resolver, inputStream, fileName)
        } else {
            saveToExternalStorage(context, inputStream, fileName)
        }
    }

    private fun saveToMediaStore(
        resolver: ContentResolver,
        inputStream: InputStream,
        fileName: String
    ): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: throw IOException("Failed to create MediaStore record.")

        resolver.openOutputStream(imageUri)?.use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        contentValues.clear()
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(imageUri, contentValues, null, null)
        return imageUri
    }

    private fun saveToExternalStorage(
        context: Context,
        inputStream: InputStream,
        fileName: String
    ): Uri? {
        val picturesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val imageFile = File(picturesDir, fileName)

        imageFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        scanFile(context, imageFile.absolutePath)
        return Uri.fromFile(imageFile)
    }

    private fun scanFile(context: Context, filePath: String) {
        MediaScannerConnection.scanFile(context, arrayOf(filePath), null) { _, uri -> }
    }

    private fun scanFile(context: Context, uri: Uri) {
        MediaScannerConnection.scanFile(context, arrayOf(uri.toString()), null, null)
    }

}
