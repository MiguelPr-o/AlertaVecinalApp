package mx.edu.utng.alertavecinal.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageUtils {

    private const val MAX_IMAGE_SIZE = 1024 // Tamaño máximo en píxeles
    private const val COMPRESSION_QUALITY = 80 // Calidad de compresión (0-100)
    private const val MAX_FILE_SIZE = 2 * 1024 * 1024 // 2MB máximo

    /**
     * Redimensiona una imagen para optimizar su tamaño
     */
    fun resizeImage(bitmap: Bitmap, maxSize: Int = MAX_IMAGE_SIZE): Bitmap {
        var width = bitmap.width
        var height = bitmap.height

        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }

        val ratio = width.toFloat() / height.toFloat()

        if (ratio > 1) {
            width = maxSize
            height = (maxSize / ratio).toInt()
        } else {
            height = maxSize
            width = (maxSize * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    /**
     * Comprime una imagen a JPEG
     */
    fun compressImage(bitmap: Bitmap, quality: Int = COMPRESSION_QUALITY): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return outputStream.toByteArray()
    }

    /**
     * Decodifica y optimiza una imagen desde un input stream
     */
    fun decodeSampledBitmapFromStream(
        inputStream: InputStream,
        reqWidth: Int = MAX_IMAGE_SIZE,
        reqHeight: Int = MAX_IMAGE_SIZE
    ): Bitmap? {
        return try {
            // Primero solo obtenemos las dimensiones
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)

            // Calculamos el sample size
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

            // Decodificamos la imagen con el sample size calculado
            options.inJustDecodeBounds = false
            inputStream.reset()
            BitmapFactory.decodeStream(inputStream, null, options)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Calcula el sample size para reducir la memoria usada
     */
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    /**
     * Corrige la orientación de la imagen basado en EXIF
     */
    fun correctImageOrientation(bitmap: Bitmap, imagePath: String): Bitmap {
        return try {
            val exif = ExifInterface(imagePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
                else -> return bitmap
            }

            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap
        }
    }

    /**
     * Verifica si el archivo de imagen es muy grande
     */
    fun isImageTooLarge(fileSize: Long): Boolean {
        return fileSize > MAX_FILE_SIZE
    }

    /**
     * Obtiene la extensión del archivo desde una URI
     */
    fun getFileExtension(uri: Uri): String {
        val path = uri.toString()
        return path.substring(path.lastIndexOf(".") + 1).lowercase()
    }

    /**
     * Verifica si la extensión del archivo es de imagen válida
     */
    fun isValidImageFormat(extension: String): Boolean {
        return extension in listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    }

    /**
     * Genera un nombre único para la imagen
     */
    fun generateImageName(prefix: String = "report"): String {
        val timestamp = System.currentTimeMillis()
        return "${prefix}_${timestamp}.jpg"
    }

    /**
     * Convierte bytes a Bitmap
     */
    fun bytesToBitmap(bytes: ByteArray): Bitmap? {
        return try {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Convierte Bitmap a bytes
     */
    fun bitmapToBytes(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(format, COMPRESSION_QUALITY, outputStream)
        return outputStream.toByteArray()
    }
}