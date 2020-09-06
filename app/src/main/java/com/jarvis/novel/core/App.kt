package com.jarvis.novel.core

import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import android.util.DisplayMetrics
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.jarvis.novel.R
import com.jarvis.novel.ui.activity.MainActivity
import java.io.ByteArrayOutputStream
import java.lang.ref.WeakReference
import java.nio.ByteBuffer


class App : Application() {
    var isShowThumbnail = true

    companion object {
        val instance: App = App()
        lateinit var context: Context
        lateinit var mainActivityContext: WeakReference<Context>
    }

    override fun onCreate() {
        super.onCreate()

        context = this
    }

    fun addFragment(fragment: Fragment, containerLayoutId: Int, tag: String? = null, type: String = "replace", addToBackStack: Boolean = false, fm: FragmentManager,
                    isShowAnimation: Boolean = true) {
        val fragmentTransaction: FragmentTransaction = fm.beginTransaction()

         if (isShowAnimation) {
            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
        }

        if (type == "add") {
            fragmentTransaction.add(containerLayoutId, fragment, tag)

            val index = fm.backStackEntryCount - 1
            if (index >= 0) {
                val backEntry = fm.getBackStackEntryAt(index)
                val hideTag = backEntry.name
                val hideFragment = fm.findFragmentByTag(hideTag)

                hideFragment?.let { fragmentTransaction.hide(it) }
            } else {
                fm.findFragmentByTag("main_page")?.let {
                    fragmentTransaction.hide(it)
                }
            }
        } else {
            fragmentTransaction.replace(containerLayoutId, fragment, tag)
        }

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag)
        }
        fragmentTransaction.commit()
        fm.executePendingTransactions()
    }

    fun base64ToBitmap(base64Str: String?): Bitmap? {
        if (base64Str.isNullOrEmpty()) {
            return null
        }

        val decodedString: ByteArray = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        val buf: ByteBuffer = ByteBuffer.wrap(byteArray)
        val imageBytes = ByteArray(buf.remaining())
        buf[imageBytes]
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    fun byteArrayToCompressedBitmap(byteArray: ByteArray, newWidth: Int): Bitmap? {
        val bitmap = byteArrayToBitmap(byteArray) ?: return null
        val scale = 1 - (bitmap.width - newWidth) / bitmap.width.toDouble()
        val newHeight = (bitmap.height * scale).toInt()
        val scaleBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

        val outputStream = ByteArrayOutputStream()

        scaleBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.write(byteArray)
        outputStream.close()

        return byteArrayToBitmap(outputStream.toByteArray())
    }

    fun compressedBitmap(bitmap: Bitmap, newWidth: Int): Bitmap? {
        val scale = 1 - (bitmap.width - newWidth) / bitmap.width.toDouble()
        val newHeight = (bitmap.height * scale).toInt()
        val scaleBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

        val outputStream = ByteArrayOutputStream()

        scaleBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        return scaleBitmap
    }

    @Suppress("DEPRECATION")
    @TargetApi(Build.VERSION_CODES.R)
    fun getScreenWidth(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val activity = mainActivityContext.get() as MainActivity
            activity.windowManager.currentWindowMetrics.bounds.width()
        } else {
            val activity = mainActivityContext.get() as MainActivity
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

    @Suppress("DEPRECATION")
    @TargetApi(Build.VERSION_CODES.R)
    fun getScreenHeight(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val activity = mainActivityContext.get() as MainActivity
            activity.windowManager.currentWindowMetrics.bounds.height()
        } else {
            val activity = mainActivityContext.get() as MainActivity
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }

    fun pixelToDp(px: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (px / scale).toInt()
    }

    fun dpToPixel(dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale).toInt()
    }

    fun setTranslation(view: View, type: String = "translationY", value: Float, duration: Long) {
        val animation = ObjectAnimator.ofFloat(view, type, value)
        animation.duration = duration
        animation.start()
    }
}