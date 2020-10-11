package com.jarvis.novel.ui.activity

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_qr_code.*
import kotlin.properties.Delegates


class QRCodeActivity : BaseActivity() {
    private var mangaVolumeId: String? = null
    private var mangaChapterId: String? = null
    private var lastPosition by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)

        getIntentData()

        generateQRCode()
    }

    private fun getIntentData() {
        mangaVolumeId = intent.getStringExtra("mangaVolumeId")
        mangaChapterId = intent.getStringExtra("mangaChapterId")
        lastPosition = intent.getIntExtra("lastPosition", -1)

        if (mangaVolumeId.isNullOrEmpty() || mangaChapterId.isNullOrEmpty() || lastPosition == -1) {
            finish()
        }
    }

    private fun generateQRCode() {

        val content = "volumeId=${mangaVolumeId}&chapterId=${mangaChapterId}&lastPosition=${lastPosition}"

        val writer = QRCodeWriter()
        val bitMatrix: BitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, App.instance.getScreenWidth(), App.instance.getScreenWidth())
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        img_qrcode.setImageBitmap(bmp)
    }
}