package org.example.plotapp.core.common.image

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGBitmapContextCreateImage
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGImageAlphaInfo
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Platform-specific implementation for sharing images on iOS.
 */
class ImageSharerImpl : ImageSharer {

    override suspend fun shareImage(bitmap: ImageBitmap, title: String): Result<Unit> {
        return runCatching {
            val uiImage = bitmap.toUIImage()
            val activityItems = listOf(uiImage)

            suspendCancellableCoroutine { continuation ->
                val controller = UIActivityViewController(
                    activityItems = activityItems,
                    applicationActivities = null,
                )

                controller.completionWithItemsHandler = { _, _, _, _ ->
                    continuation.resume(Unit)
                }

                val rootController = UIApplication.sharedApplication.keyWindow?.rootViewController
                rootController?.presentViewController(
                    controller,
                    animated = true,
                    completion = null,
                )
                    ?: continuation.resumeWithException(IllegalStateException("No root view controller found"))
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
fun ImageBitmap.toUIImage(): UIImage? {
    val width = this.width
    val height = this.height
    val buffer = IntArray(width * height)

    this.readPixels(buffer)

    val colorSpace = CGColorSpaceCreateDeviceRGB()
    val context = CGBitmapContextCreate(
        data = buffer.refTo(0),
        width = width.toULong(),
        height = height.toULong(),
        bitsPerComponent = 8u,
        bytesPerRow = (4 * width).toULong(),
        space = colorSpace,
        bitmapInfo = CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value,
    )

    val cgImage = CGBitmapContextCreateImage(context)
    return cgImage?.let { UIImage.imageWithCGImage(it) }
}
