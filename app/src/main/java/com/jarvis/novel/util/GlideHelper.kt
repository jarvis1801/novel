package com.jarvis.novel.util

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.print.PrintHelper
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.File


class GlideHelper {

    companion object {
        const val CACHE_DIR = "/storage/emulated/0/Android/data/com.jarvis.novel.dev/cache/image_manager_disk_cache/"
    }

    fun preloadImage(context: Context, url: String, signature: String, isLast: Boolean, finishCallback: () -> Unit? = {}, downloadCallback: () -> Unit? = {}) : String {
        GlideApp.with(context)
            .downloadOnly()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .load(url)
            .listener(object : RequestListener<File?> {
                override fun onLoadFailed(e: GlideException?, model: Any, target: Target<File?>, isFirstResource: Boolean): Boolean {
                    downloadCallback()
                    if (isLast) {
                        finishCallback()
                    }

                    return false
                }

                override fun onResourceReady(resource: File?, model: Any, target: Target<File?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {

                    val renameFile = File("$CACHE_DIR$signature.0")

                    val success: Boolean? = resource?.renameTo(renameFile)

                    if (success == true) {
                        resource.delete()
                    }
                    downloadCallback()

                    if (isLast) {
                        finishCallback()
                    }

                    return false
                }
            })
            .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)

            return signature
    }

    fun loadImage(context: Context, url: String, imageView: ImageView, signature: String) {
        GlideApp.with(context)
            .load("$CACHE_DIR$signature.0")
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(imageView)
    }
}