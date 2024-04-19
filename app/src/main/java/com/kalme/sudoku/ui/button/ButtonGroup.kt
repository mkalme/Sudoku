package com.kalme.sudoku.ui.button

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import com.kalme.sudoku.ui.Cell
import com.kalme.sudoku.ui.Event
import com.kalme.sudoku.ui.NumberPad

// Class to handle all buttons displayed on screen
class ButtonGroup(private val view: View, private val numberPad: NumberPad, private val width: Int) {
    private val buttons = mutableListOf<Button>()
    private var pressedButton: Button? = null

    private var cellBorderPaint: Paint = Paint()
    private val cellHighlightPaint: Paint = Paint()
    private val textPaint: Paint = Paint()

    init {
        cellBorderPaint.color = Color.BLACK
        cellBorderPaint.style = Paint.Style.STROKE
        cellBorderPaint.strokeWidth = 2f
        cellBorderPaint.isAntiAlias = false

        cellHighlightPaint.color = Color.LTGRAY
        cellHighlightPaint.isAntiAlias = false

        textPaint.color = Color.BLACK
        textPaint.isAntiAlias = true
        textPaint.textSize = width / 14.4f
    }

    fun addButton(rect: RectF, text: String, event: Event){
        val button = Button(Cell(rect, 0, 0), text, event)
        buttons.add(button)
    }

    fun draw(canvas: Canvas){
        buttons.forEach {
            if(it == pressedButton){
                canvas.drawRect(it.cell.rect, cellHighlightPaint)
            }

            canvas.drawRect(it.cell.rect, cellBorderPaint)
            canvas.drawText(it.text, it.cell.rect.left + width / 36, it.cell.rect.bottom - it.cell.rect.height() / 3.6f, textPaint)
        }
    }

    private fun getButtonFromPositionOnScreen(x: Float, y: Float): Button? {
        for(i in 0 until buttons.size){
            if(buttons[i].cell.isInside(x, y)) return buttons[i]
        }

        return null
    }

    // If a user taps on a button, its click event gets invoked
    fun registerClick(event: MotionEvent){
        if(numberPad.isOpen()) return

        if(event.action == MotionEvent.ACTION_DOWN){
            val cell = getButtonFromPositionOnScreen(event.x, event.y)

            if(cell == null){
                pressedButton = null
                view.invalidate()
            }else{
                pressedButton = cell
                view.invalidate()
            }
        }else if(event.action == MotionEvent.ACTION_UP){
            val cell = getButtonFromPositionOnScreen(event.x, event.y)

            if(pressedButton != null && pressedButton == cell){
                cell?.clickEvent?.onInvoke()
            }

            pressedButton = null
            view.invalidate()
        }
    }
}