package com.allo.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import androidx.cardview.widget.CardView


/**
 *
 * @ProjectName:    Contacts
 * @Package:        com.allo.view
 * @ClassName:      RadiusCardView
 * @Description:    CardView 设置单边圆角
 * @Author:         Roy
 * @CreateDate:     2022/8/16 下午2:33
 * @Version:        2.3
 */
class RadiusCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    def: Int = 0
) : CardView(context, attrs, def) {

    var tlRadius = 0f
    var trRadius = 0f
    var brRadius = 0f
    var blRadius = 0f
    val path = Path()

    init {
        attrs?.let {
            radius = 0f
            val array = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.RadiusCardView,
                0,
                0
            )
            tlRadius = array.getDimension(R.styleable.RadiusCardView_topLeftRadius, 0f)
            trRadius = array.getDimension(R.styleable.RadiusCardView_topRightRadius, 0f)
            brRadius = array.getDimension(R.styleable.RadiusCardView_bottomRightRadius, 0f)
            blRadius = array.getDimension(R.styleable.RadiusCardView_bottomLeftRadius, 0f)
            background = ColorDrawable()
        }
    }

    override fun onDraw(canvas: Canvas) {
        val rectF = getRectF()
        val radius = floatArrayOf(tlRadius, tlRadius, trRadius, trRadius, brRadius, brRadius, blRadius, blRadius)
        path.reset()
        path.addRoundRect(rectF, radius, Path.Direction.CW)
        canvas.clipPath(path)
        super.onDraw(canvas)

    }

    private fun getRectF(): RectF {
        val rect = Rect()
        getDrawingRect(rect)
        return RectF(rect)
    }

}