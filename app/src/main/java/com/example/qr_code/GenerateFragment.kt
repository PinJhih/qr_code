package com.example.qr_code

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
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
import java.io.FileOutputStream

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
                img_qr_code.setImageBitmap(
                    generateQRCode(size)
                )
            }
        }
        img_qr_code.setOnClickListener {
            AlertDialog.Builder(context!!)
                .setTitle("儲存到手機")
                .setMessage("要將QR Code儲存到手機嗎?")
                .setNegativeButton("否") { _, _ -> }
                .setPositiveButton("是") { _, _ ->
                    try {
                        val bitmap = generateQRCode(1024)
                        saveQRCode(bitmap)
                        Toast.makeText(context, "儲存成功", Toast.LENGTH_SHORT)
                            .show()
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
        val title = "${System.currentTimeMillis()}"
        var file = context!!.getDir("Images", Context.MODE_PRIVATE)
        file = File(file, "$title.jpg")
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.flush()
        out.close()
    }
}
