package com.kalme.sudoku.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View

// Class to handle a custom keyboard-like control. Only accepts digits from 1 to 9 as Sudoku does not contain zeros
class NumberPad(val width: Int, private val height: Int, private val view: View) {
    private val cells = mutableListOf<Cell>()
    private var pressedCell: Cell? = null

    private var backgroundBorderPaint: Paint = Paint()
    private var backgroundFillPaint: Paint = Paint()

    private var cellBorderPaint: Paint = Paint()
    private val cellHighlightPaint: Paint = Paint()
    private val textPaint: Paint = Paint()

    private val padding = 16f

    private var isOpen = false
    private var lastDigit: Int? = null

    private var offsetX = 0
    private var offsetY = 0

    val digitSelectedListener = mutableListOf<Event>()
    val closedListener = mutableListOf<Event>()

    fun setOffset(offsetX: Int, offsetY: Int){
        this.offsetX = offsetX
        this.offsetY = offsetY
    }

    private fun invokeListeners(listener: List<Event>){
        listener.forEach {
            it.onInvoke()
        }
    }

    fun open(){
        isOpen = true
    }

    private fun close(){
        if(isOpen) invokeListeners(closedListener)
        isOpen = false
    }

    fun isOpen(): Boolean {
        return isOpen
    }

    init {
        backgroundBorderPaint.color = Color.BLACK
        backgroundBorderPaint.style = Paint.Style.STROKE
        backgroundBorderPaint.strokeWidth = 5f

        backgroundFillPaint.color = Color.WHITE
        backgroundFillPaint.style = Paint.Style.FILL

        cellBorderPaint.color = Color.BLACK
        cellBorderPaint.style = Paint.Style.STROKE
        cellBorderPaint.strokeWidth = 2f
        cellBorderPaint.isAntiAlias = false

        cellHighlightPaint.color = Color.LTGRAY
        cellHighlightPaint.isAntiAlias = false

        textPaint.color = Color.BLACK
        textPaint.isAntiAlias = true
        textPaint.textSize = (getBoardAreaOnScreen().width() / 3) * 0.7f

        initializeCells()
    }

    private fun getBoardAreaOnScreen(): RectF {
        return RectF(0f, 0f, width.toFloat(), width.toFloat())
    }

    private fun getCellAreaOnScreen(x: Int, y: Int): RectF {
        val boardRect = getBoardAreaOnScreen()

        val thirdWidth = (boardRect.width() - padding * 4) / 3

        val left = boardRect.left + padding + x * thirdWidth + x * padding
        val top = boardRect.top + padding + y * thirdWidth + y * padding

        return RectF(left, top, left + thirdWidth, top + thirdWidth)
    }

    // Keeps track of each cell's position and area on screen in a list
    private fun initializeCells() {
        for(y in 0 until 4){
            for(x in 0 until 3){
                val cell = getCellAreaOnScreen(x, y)
                cells.add(Cell(cell, x, y))
            }
        }
    }

    fun draw(canvas: Canvas){
        if(!isOpen) return

        canvas.drawRect(RectF(offsetX.toFloat(), offsetY.toFloat(), (offsetX + width).toFloat(), (offsetY + height).toFloat()), backgroundBorderPaint)
        canvas.drawRect(RectF(offsetX.toFloat(), offsetY.toFloat(), (offsetX + width).toFloat(), (offsetY + height).toFloat()), backgroundFillPaint)

        if(pressedCell != null){
            val pressedRect = pressedCell!!.rect
            val rect = RectF(pressedRect.left, pressedRect.top, pressedRect.right, pressedRect.bottom)

            rect.offset(offsetX.toFloat(), offsetY.toFloat())
            canvas.drawRect(rect, cellHighlightPaint)
        }

        for(y in 0 until 3){
            for(x in 0 until 3){
                val cell = getCellAreaOnScreen(x, y)
                cell.offset(offsetX.toFloat(), offsetY.toFloat())

                canvas.drawRect(cell, cellBorderPaint)

                val digit = getDigitFromGridCoordinates(x, y).toString()
                canvas.drawText(digit, cell.left + cell.width() / 4, cell.bottom - cell.width() / 5, textPaint)
            }
        }
    }

    private fun getCellFromPositionOnScreen(x: Float, y: Float): Cell? {
        for(i in 0 until cells.size){
            if(cells[i].isInside(x, y)) return cells[i]
        }

        return null
    }

    private fun getDigitFromGridCoordinates(x: Int, y: Int): Int{
        return y * 3 + x + 1
    }

    fun registerClick(event: MotionEvent){
        if(!isOpen) return

        if(event.action == MotionEvent.ACTION_DOWN){
            val cell = getCellFromPositionOnScreen(event.x - offsetX, event.y - offsetY)

            if(cell == null){
                val rect = getBoardAreaOnScreen()
                rect.offset(offsetX.toFloat(), offsetY.toFloat())
                if(!rect.contains(event.x, event.y)) close()

                pressedCell = null
                view.invalidate()
            }else{
                pressedCell = cell
                view.invalidate()
            }
        }else if(event.action == MotionEvent.ACTION_UP){
            val cell = getCellFromPositionOnScreen(event.x - offsetX, event.y - offsetY)

            if(pressedCell != null && pressedCell == cell){
                lastDigit = getDigitFromGridCoordinates(pressedCell?.x!!, pressedCell?.y!!)
                pressedCell = null
                invokeListeners(digitSelectedListener)

                close()
                view.invalidate()
            }else{
                pressedCell = null
                view.invalidate()
            }
        }
    }

    fun getLastDigit(): Int?{
        return lastDigit
    }
}