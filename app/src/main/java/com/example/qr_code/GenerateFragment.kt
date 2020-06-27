package com.example.qr_code

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.fragment_generate.*

class GenerateFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_generate, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
}
