package jdd.com.ng.jddwebmaster.jddstore.code.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MyCustomItalicBoldTextView (context: Context, attributeSet: AttributeSet): AppCompatTextView(context, attributeSet) {

    // initiate the font
    init {
        applyFont()
    }

    private fun applyFont() {
        val typeface = Typeface.createFromAsset(context.assets, "Roboto-BoldItalic.ttf")
        setTypeface(typeface)
    }
}