package com.sissi.customviewdemo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class XfermodeView : View {

    private lateinit var context:Context

    private lateinit var dstBitmap:Bitmap
    private lateinit var srcBitmap:Bitmap
    private val bitmapSize=800
    private lateinit var paint:Paint

    var xfermode:PorterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        set(value) {
            field = value
            invalidate()
        }

    private fun init(context: Context, attrs:AttributeSet?){
        this.context = context
        paint = Paint(Paint.ANTI_ALIAS_FLAG)

        setLayerType(LAYER_TYPE_HARDWARE, null) // 有些效果需要关闭硬件加速
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w: Int = bitmapSize // 控制view的大小为bitmap大小
        val h = w
        // 结合父view的约束算出最终宽高
        val width = resolveSize(w, widthMeasureSpec)
        val height = resolveSize(h, heightMeasureSpec)
        val finalSize = min(width, height)
        println("w=$w, h=$h, width=$width, height=$height, finalSize=$finalSize")
        // 告诉父view自己期望的宽高
        setMeasuredDimension(finalSize, finalSize)
    }

    override fun onDraw(canvas: Canvas) {

        canvas.drawColor(Color.LTGRAY)  // 绘制不同于activity默认背景色的背景色更容易分辨该控件尺寸

        // 创建两个形状大小相同的Bitmap
        if (!this::srcBitmap.isInitialized){
            srcBitmap = createSrcBitmap()
        }
        if (!this::dstBitmap.isInitialized){
            dstBitmap = createDstBitmap()
        }

        // 使用离屏缓冲，此后的绘制均在独立的layer上，直到restore才写回到默认的layer。
        // 必须使用离屏缓冲，PorterDuffXfermode才会有预期效果，否则整个view的背景及已有绘制均会被当做DST。
        val saved = canvas.saveLayer(null, null)

        canvas.drawBitmap(dstBitmap,0f,0f, paint)

        paint.setXfermode(xfermode)

        canvas.drawBitmap(srcBitmap,0f,0f, paint) // src和dst在相同的位置绘制了相同尺寸的bitmap

        paint.setXfermode(null)

        // 将离屏缓冲的内容合并会默认layer
        canvas.restoreToCount(saved)
    }

    /**
     * 创建目标Bitmap。
     * 直接在onDraw里面drawCircle，drawRect，即便使用了setXfermode和saveLayer也是不能获得期望的效果的。
     * 我们创建两个Bitmap来实现PorterDuffXfermode的各种效果。
     * Google官方文档展示所绘制SRC和DST是Bitmap，Bitmap有透明区域和颜色区域，Bitmap绘制的蓝色矩形和粉色圆形是用 Canvas 绘制的颜色区域宽高。
     * SRC和DST的Bitmap是大小控制了透明区域重叠的，不仅仅包含颜色区域，还有外部的透明区域也要计算进去。描述如下图所示：
     * <img src="../../../../assets/PorterDuffXfermode.png" />
     * 透明区域要足够覆盖到要和它结合绘制的内容，否则得到的结果很可能不是你想要的：
     * <img src="../../../../assets/PorterDuffXfermodeUnexpectedResult.png" />
     * 由上图所示，由于透明区域过小而覆盖不到的地方，将不会收到Xfermode的影响。
     * 为了能够实现官方文档上的效果，我们需要的是把要合成效果的图像抠出来排除被空白区域影响，那要怎么抠出来？
     * 其实就是使用离屏缓冲 canvas.saveLayer() 和 canvas.restoreToCount()。
     * */
    fun createDstBitmap() :Bitmap {
        // 保证src和DST是重叠的，这样才能获得预期效果
        val bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        paint.setColor(Color.RED)
        canvas.drawRect(200f, 200f, 800f, 800f, paint)
        return bitmap
    }

    fun createSrcBitmap() :Bitmap {
        // 保证src和DST是重叠的，这样才能获得预期效果
        val bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        paint.setColor(Color.BLUE)
        canvas.drawCircle(200f, 200f, 200f, paint)
        return bitmap
    }

}
