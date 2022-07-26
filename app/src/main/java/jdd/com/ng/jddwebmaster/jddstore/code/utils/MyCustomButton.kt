package jdd.com.ng.jddwebmaster.jddstore.code.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class MyCustomButton(context: Context, attributeSet: AttributeSet): AppCompatButton(context, attributeSet) {
    // apply Roboto font to the buttons
    init {
        applyButtonFont()
    }

    private fun applyButtonFont() {
        val typeface = Typeface.createFromAsset(context.assets, "Roboto-MediumItalic.ttf")
        setTypeface(typeface)
    }
}