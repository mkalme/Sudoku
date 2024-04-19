package com.kalme.sudoku.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.kalme.sudoku.ui.button.ButtonGroup

// This view acts as a canvas. All controls are custom drawn
// I only did this because I like the look and wanted something unique
// 'Board', 'Numpad' amd 'Buttonroup' are classes that draw their respective contents separately
class SudokuView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var board: Board? = null
    private var numpad: NumberPad? = null
    private var buttonGroup: ButtonGroup? = null

    private var isInitialized = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(!isInitialized) initializeComponent()

        board?.draw(canvas)
        buttonGroup?.draw(canvas)
        numpad?.draw(canvas)
    }

    // The reason I initialize everything after the onDrawn method is called is because 'width' and 'height' properties ar 0 before this View is shown
    private fun initializeComponent(){
        if(numpad == null) numpad = NumberPad((width.toFloat() * 0.55f).toInt(), (width.toFloat() * 0.55f).toInt(), this)
        if(board == null) {
            board = Board(width.toFloat(), 0f, 16f, this, numpad!!)
            board?.preGenerateAhead()
        }
        if(buttonGroup == null){
            buttonGroup = ButtonGroup(this, numpad!!, width)
            addButtons()
        }

        isInitialized = true
    }

    // Adds the custom drawn buttons with their positions on screen
    // No real reason to use these over the standard Android SDK provided ones besides trying something different
    private fun addButtons(){
        val resetWidth = width / 4.11f
        val resetLeft = (width - resetWidth) / 2
        val resetTop = board?.width!! * 1.2f
        buttonGroup?.addButton(RectF(resetLeft, resetTop, resetLeft + resetWidth, resetTop + width / 9), "Reset", object: Event {
            override fun onInvoke() {
                board?.generateNew()
            }
        })

        val solutionWidth = width / 3.2f
        val solutionLeft = (width - solutionWidth) / 2
        val solutionTop = resetTop + width / 7.2f
        buttonGroup?.addButton(RectF(solutionLeft, solutionTop, solutionLeft + solutionWidth, solutionTop + width / 9), "Solution", object: Event {
            override fun onInvoke() {
                board?.showSolution()
            }
        })
    }

    // This is an event driven application. All touch inputs are registered here
    // 'isOpen' means whether or not the control registers a touch event
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val boardOpen = board?.isOpen() ?: false
        val numberPadOpen = numpad?.isOpen()?: false

        if(boardOpen) board?.registerClick(event)
        if(numberPadOpen) numpad?.registerClick(event)

        buttonGroup?.registerClick(event)

        return true
    }
}