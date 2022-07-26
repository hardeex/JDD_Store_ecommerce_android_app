package jdd.com.ng.jddwebmaster.jddstore.code.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MyCustomTextViewBold(context: Context, attributeSet: AttributeSet): AppCompatTextView(context, attributeSet) {

    // initiate the font
    init {
        applyRobotoBold()
    }

    private fun applyRobotoBold() {
        val typeface = Typeface.createFromAsset(context.assets, "Roboto-Bold.ttf")
        setTypeface(typeface)
    }
}