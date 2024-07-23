package com.sissi.customviewdemo

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewGroup
import kotlin.math.max


class FlexboxLayout : ViewGroup {

    private lateinit var context:Context
    private val childrenBounds = ArrayList<Rect>()
    private var itemSpace=0
    private var rowSpace=0
    private fun init(context: Context, attrs:AttributeSet?){
        this.context = context
        itemSpace = resources.getDimension(R.dimen.flexboxlayout_padding).toInt()
        rowSpace = resources.getDimension(R.dimen.flexboxlayout_padding).toInt()
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr:Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //2.1 解析 ViewGroup 的父 View 对 ViewGroup 的尺寸要求
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        println("Mode[UNSPECIFIED=${MeasureSpec.UNSPECIFIED}, EXACTLY=${MeasureSpec.EXACTLY}, AT_MOST=${MeasureSpec.AT_MOST}]")
        println("onMeasure: widthMode=$widthMode, widthSize=$widthSize, heightMode=$heightMode, heightSize=$heightSize")

        //2.2 ViewGroup 根据「开发者在 xml 中写的对 ViewGroup 子 View 的尺寸要求」、「自己的父 View（ViewGroup 的父 View）对自己的尺寸要求」和
        //「自己的可用空间」计算出自己对子 View 的尺寸要求，并将该尺寸要求通过子 View 的 measure() 方法传给子 View，让子 View 测量自己（View）的期望尺寸
        var widthUsed = 0
        var heightUsed = paddingTop
        var lineHeight = 0
        var lineWidthUsed = paddingLeft
        val rightLimit = widthSize - paddingRight

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, heightUsed)

            println("child($i/$childCount), widthMode=$widthMode, " +
                    "lineWidthUsed=$lineWidthUsed, heightUsed=$heightUsed, child.measuredWidth=${child.measuredWidth}, " +
                    "child.measuredHeight=${child.measuredHeight}, maxRight=$rightLimit")

            //是否需要换行
            if (widthMode != MeasureSpec.UNSPECIFIED && (lineWidthUsed + child.measuredWidth > rightLimit)) {
                // 这一行的空间不够放下该child，则换行
                lineWidthUsed = paddingLeft // 重置行偏移
                heightUsed += (lineHeight + rowSpace) // 累加已用行高度（所有child高度以及间隙高度）
                lineHeight = 0
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, heightUsed)
            }
            println("child($i/$childCount), widthMode=$widthMode, " +
                    "lineWidthUsed=$lineWidthUsed, heightUsed=$heightUsed, child.measuredWidth=${child.measuredWidth}, " +
                    "child.measuredHeight=${child.measuredHeight}, maxRight=$rightLimit")

            //2.3 ViewGroup 暂时保存子 View 的尺寸，以便布局阶段和绘制阶段使用
            var childBound: Rect
            if (childrenBounds.size <= i) {
                childBound = Rect()
                childrenBounds.add(childBound)
            } else {
                childBound = childrenBounds[i]
            }
            //此处不能用 child.getxxx() 获取子 View 的尺寸值，因为子 View 只是量了尺寸，还没有布局，这些值都是 0
//            childBound.set(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
            childBound.set(lineWidthUsed, heightUsed, lineWidthUsed + child.measuredWidth, heightUsed + child.measuredHeight)
//            childBound[lineWidthUsed, heightUsed, lineWidthUsed + child.measuredWidth] = heightUsed + child.measuredHeight

            lineWidthUsed += child.measuredWidth + itemSpace

            widthUsed = max(lineWidthUsed.toDouble(), widthUsed.toDouble()).toInt()
            lineHeight = max(lineHeight.toDouble(), child.measuredHeight.toDouble()).toInt()
        }

        //2.4 ViewGroup 将「根据子 View 的实际尺寸计算出的自己（ViewGroup）的尺寸」结合「自己父 View 对自己的尺寸要求」进行修正，并通
        //过 setMeasuredDimension() 方法告知父 View 自己的期望尺寸
        val measuredWidth = resolveSize(widthUsed, widthMeasureSpec)
        val measuredHeight = resolveSize((heightUsed + lineHeight + paddingBottom), heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    //2.2.1 在自定义 ViewGroup 中调用 measureChildWithMargins() 方法计算 ViewGroup 对子 View 的尺寸要求时，
    //必须在 ViewGroup 中重写 generateLayoutParams() 方法，因为 measureChildWithMargins() 方法中用到了 MarginLayoutParams，
    //如果不重写 generateLayoutParams() 方法，那调用 measureChildWithMargins() 方法时，MarginLayoutParams 就为 null，
    //所以在自定义 ViewGroup 中调用 measureChildWithMargins() 方法时，必须重写 generateLayoutParams() 方法。
    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            //应用测量阶段计算出的子 View 的尺寸值布局子 View
            val child = getChildAt(i)
            val childBound = childrenBounds[i]
            child.layout(childBound.left, childBound.top, childBound.right, childBound.bottom)
        }
    }

}