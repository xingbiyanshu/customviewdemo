package com.sissi.customviewdemo

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import kotlin.math.min

class AnimateView : View {

    private lateinit var context:Context

    private lateinit var paint:Paint

    var animateType = Type.PropertyAnimation
        set(value) {
            stopAnimation(field)
            field=value
            when(value){
                Type.PropertyAnimation -> {
                    startPropertyAnimation()
                }
                Type.ViewAnimationTween -> {
                    startViewAnimationTween()
                }
                Type.ViewAnimationFrame -> {
                    startViewAnimationFrame()
                }
            }
        }

    private var radius = 0f
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

    private var alpha = 0
        set(value) {
            field=value
            invalidate()
//            println("alpha=$alpha")
        }

    private var savedBackground: Drawable?=null

    private val propertyAnimator: AnimatorSet by lazy {
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

    private val tweenAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.tween_animation)
    }

    enum class Type {
        PropertyAnimation,
        ViewAnimationTween,
        ViewAnimationFrame,
    }

    private fun init(context: Context, attrs:AttributeSet?){
        this.context = context
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style=Paint.Style.FILL
        paint.setColor(Color.BLUE)
        setOnClickListener {
            // 当使用ViewAnimation时，点击该View可发现不能按预期弹Toast（没有触发onclick）。
            // 因为ViewAnimation只是改变了View的绘制效果，并未改变View本身属性，View的实际位置仍是原始位置。
            Toast.makeText(context, "Hi!", Toast.LENGTH_LONG).show()
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
        if (Type.ViewAnimationFrame == animateType){
            super.onDraw(canvas)
            return
        }
//        canvas.drawColor(Color.LTGRAY)
        paint.setColor(color)
        paint.alpha = alpha
        canvas.drawCircle(width/2f, height/2f, radius, paint)
//        println("onDraw width=${width} height=${height} radius=$radius")
    }


    private fun stopAnimation(type: Type){
        when(type){
            Type.PropertyAnimation-> stopPropertyAnimation()
            Type.ViewAnimationTween -> stopViewAnimationTween()
            Type.ViewAnimationFrame -> stopViewAnimationFrame()
        }
    }

    // ViewAnimation只能作用于View类；
    // ViewAnimation只是改变View的显示效果并不会真正改变View的属性，比如使用ViewAnimation将按钮从左挪到右后，点击右侧的按钮并不会触发点击事件，因为按钮的真实位置其实还是在左侧。
    private fun startViewAnimationTween(){
        // xml方式
        this.startAnimation(tweenAnimation) // this替换为其他view可以在布局中针对子view应用动画

        // 纯代码方式
//        animate()
////                .translationX(200f)
////                .rotation(180f)
//            .scaleX(2f) // 不同于objectAnimator是放大绘制的圆，PropertyAnimator是直接放大view本身的size
    }

    private fun stopViewAnimationTween(){
        clearAnimation()
    }

    private fun startPropertyAnimation(){
        propertyAnimator.start()
    }

    private fun stopPropertyAnimation(){
        propertyAnimator.cancel()
    }

    private fun startViewAnimationFrame(){
        savedBackground = background
        /*
        * 帧动画对于ImageView可以直接设置它的src，对于普通View则通过设置它的background
        * */
        setBackgroundResource(R.anim.frame_animation)
        val bg = background
        if (bg is Animatable){
            bg.start()
        }
    }

    private fun stopViewAnimationFrame(){
        val bg = background
        if (bg is Animatable){
            bg.stop()
        }
        background = savedBackground
    }

}
