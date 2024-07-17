package com.sissi.customviewdemo

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlin.math.min

class AnimateView : View {

    private lateinit var context:Context

    private lateinit var paint:Paint

    var animateType = Type.PropertyAnimation
        set(value) {
            field=value
            when(value){
                Type.PropertyAnimation -> {
                    stopViewAnimation()
                    startPropertyAnimation()
                }
                Type.ViewAnimation -> {
                    stopPropertyAnimation()
                    startViewAnimation()
                }
            }
        }

    var radius = 0f
        set(value) {
            field=value
            // radius变化后需要重新计算view的大小(走onMeasure)，所以需要requestLayout而非invalidate
            requestLayout()
//            println("radius=$radius")
        }

    var color = Color.RED
        set(value) {
            field=value
            invalidate()
//            println("color=$color")
        }

    var alpha = 0
        set(value) {
            field=value
            invalidate()
//            println("alpha=$alpha")
        }

    val propertyAnimator: AnimatorSet by lazy {
        // ObjectAnimator放缩的是某个自定义属性，通过该属性的不断变化不断触发重绘进而达到动画效果。
        // 而ViewPropertyAnimator放缩的是view自身的属性，如宽高、偏移量等，以达到动画效果。
        // 动画效果由xml定义
        AnimatorInflater.loadAnimator(context, R.animator.property_animation).apply {
            setTarget(this@AnimateView)
            addListener(object :AnimatorListener{
                override fun onAnimationStart(animation: Animator) {
                    println("onAnimationStart")
                }

                override fun onAnimationEnd(animation: Animator) {
                    println("onAnimationEnd")
                }

                override fun onAnimationCancel(animation: Animator) {
                    println("onAnimationCancel")
                }

                override fun onAnimationRepeat(animation: Animator) {
                    println("onAnimationRepeat")
                }
            })
        } as AnimatorSet

        // 纯代码定义
//        ObjectAnimator.ofFloat(this, "radius", 0f, 200f).apply {
//            setInterpolator(AccelerateDecelerateInterpolator())
//            setDuration(3000)
//            addListener(object :AnimatorListener{
//                override fun onAnimationStart(animation: Animator) {
//                    println("onAnimationStart")
//                }
//
//                override fun onAnimationEnd(animation: Animator) {
//                    println("onAnimationEnd")
//                }
//
//                override fun onAnimationCancel(animation: Animator) {
//                    println("onAnimationCancel")
//                }
//
//                override fun onAnimationRepeat(animation: Animator) {
//                    println("onAnimationRepeat")
//                }
//            })
//        }
    }

    val viewAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.view_animation)
    }

    enum class Type{
        PropertyAnimation,
        ViewAnimation,
    }

    private fun init(context: Context, attrs:AttributeSet?){
        this.context = context
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style=Paint.Style.FILL
        paint.setColor(Color.BLUE)
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
        val w: Int = (radius*2).toInt()
        val h = w
        val width = resolveSize(w, widthMeasureSpec)
        val height = resolveSize(h, heightMeasureSpec)
        val finalSize = min(width, height)
        setMeasuredDimension(finalSize, finalSize)
//        println("onMeasure w=$w, h=$h, width=$width, height=$height, finalSize=$finalSize")
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.LTGRAY)
        paint.setColor(color)
        paint.alpha = alpha
        canvas.drawCircle(width/2f, height/2f, radius, paint)
//        println("onDraw width=${width} height=${height} radius=$radius")
    }

    // ViewAnimation只能作用于View类；
    // ViewAnimation只是改变View的显示效果并不会真正改变View的属性，比如使用ViewAnimation将按钮从左挪到右后，点击右侧的按钮并不会触发点击事件，因为按钮的真实位置其实还是在左侧。
    private fun startViewAnimation(){
        // xml方式
        this.startAnimation(viewAnimation) // this替换为其他view可以在布局中针对子view应用动画

        // 纯代码方式
//        animate()
////                .translationX(200f)
////                .rotation(180f)
//            .scaleX(2f) // 不同于objectAnimator是放大绘制的圆，PropertyAnimator是直接放大view本身的size
    }

    private fun stopViewAnimation(){
        clearAnimation()
    }

    private fun startPropertyAnimation(){
        propertyAnimator.start()
    }

    private fun stopPropertyAnimation(){
        propertyAnimator.cancel()
    }

}
