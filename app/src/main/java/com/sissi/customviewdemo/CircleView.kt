package com.sissi.customviewdemo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class CircleView : View {

    private var radiusList = mutableListOf<Float>()
    private var colorList = mutableListOf<Int>()

    private lateinit var context:Context

    private lateinit var paint:Paint

    private fun init(context: Context, attrs:AttributeSet?){
        this.context = context
        paint = Paint()
        paint.style=Paint.Style.FILL
        paint.isAntiAlias=true

        if (attrs!=null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleView)
            radiusList.add(typedArray.getDimension(R.styleable.CircleView_radius, 1f))
            radiusList.add(typedArray.getDimension(R.styleable.CircleView_radius2, 1f))
            radiusList.add(typedArray.getDimension(R.styleable.CircleView_radius3, 1f))
            radiusList.add(typedArray.getDimension(R.styleable.CircleView_radius4, 1f))
            colorList.add(typedArray.getColor(R.styleable.CircleView_color, Color.BLACK))
            colorList.add(typedArray.getColor(R.styleable.CircleView_color2, Color.BLACK))
            colorList.add(typedArray.getColor(R.styleable.CircleView_color3, Color.BLACK))
            colorList.add(typedArray.getColor(R.styleable.CircleView_color4, Color.BLACK))
        }
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs:AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs:AttributeSet?, defStyleAttr:Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }


    // 默认该控件充满屏幕，我们希望该控件的大小为同该圆形直径一样大的一个正方形。
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w: Int = (radiusList.last()*2).toInt()
        val h = w
        val width = resolveSize(w, widthMeasureSpec)
        val height = resolveSize(h, heightMeasureSpec)
        val finalSize = min(width, height)
        println("w=$w, h=$h, width=$width, height=$height, finalSize=$finalSize")
        setMeasuredDimension(finalSize, finalSize)
    }

    //仅 ViewGroup 需要重写
    //由于没有子 View 需要布局，所以不用重写该方法
//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//    }

    //重写绘制阶段相关方法（onDraw() 绘制主体、dispatchDraw() 绘制子 View 和 onDrawForeground() 绘制前景，背景只能通过android:background设置）；
    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.LTGRAY)
        radiusList.reversed().forEachIndexed { index, radius ->
            val color = colorList[radiusList.size-1-index]
            println("radius=$radius, color=$color, width=$width, height=$height")
            paint.setColor(color)
            canvas.drawCircle(width/2f, height/2f, radius, paint)
        }
    }

}
