package com.example.qr_code

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.fragment_generate.*
import java.io.File
import java.io.File.separator
import java.io.FileOutputStream
import java.io.OutputStream

class GenerateFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_generate, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (context as MainActivity).permission()
        btn_convert.setOnClickListener {
            if (ed_input.text.isEmpty())
                Toast.makeText(context, "請輸入文字", Toast.LENGTH_SHORT).show()
            else {
                val size = (screenSize() * 0.9).toInt()
                var bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                val handler = Handler {
                    img_qr_code.setImageBitmap(bitmap)
                    true
                }
                Thread(Runnable {
                    bitmap = generateQRCode(size)
                    val msg = Message()
                    msg.what = 0
                    handler.sendMessage(msg)
                }).start()
            }
        }
        img_qr_code.setOnClickListener {
            AlertDialog.Builder(context!!)
                .setTitle("儲存到手機")
                .setMessage("要將QR Code儲存到手機嗎?")
                .setNegativeButton("否") { _, _ -> }
                .setPositiveButton("是") { _, _ ->
                    try {
                        val handler = Handler {
                            Toast.makeText(context, "儲存成功", Toast.LENGTH_SHORT).show()
                            true
                        }
                        Thread(Runnable {
                            val bitmap = generateQRCode(1024)
                            saveQRCode(bitmap)
                            val msg = Message()
                            msg.what = 0
                            handler.sendMessage(msg)
                        }).start()
                    } catch (e: Exception) {
                        Toast.makeText(context, "儲存失敗", Toast.LENGTH_SHORT)
                            .show()
                        println("~~GG $e")
                    }
                }
                .show()
        }
    }

    private fun screenSize(): Int {
        resources.displayMetrics.let { displayMetrics ->
            return displayMetrics.widthPixels
        }
    }

    private fun generateQRCode(size: Int): Bitmap {
        val url = "${ed_input.text}"
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(url, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    private fun saveQRCode(bitmap: Bitmap) {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val content = getContentValues()
            content.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/qr_code")
            content.put(MediaStore.Images.Media.IS_PENDING, true)
            val uri: Uri? =
                context!!.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    content
                )
            if (uri != null) {
                saveImageToStream(bitmap, context!!.contentResolver.openOutputStream(uri))
                content.put(MediaStore.Images.Media.IS_PENDING, false)
                context!!.contentResolver.update(uri, content, null, null)
            }
        } else {
            val dir =
                File(Environment.getExternalStorageDirectory().toString() + separator + "qr_code")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val name = System.currentTimeMillis().toString() + ".png"
            val file = File(dir, name)
            saveImageToStream(bitmap, FileOutputStream(file))
            val content = getContentValues()
            content.put(MediaStore.Images.Media.DATA, file.absolutePath)
            context!!.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                content
            )
        }
    }

    private fun getContentValues(): ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
