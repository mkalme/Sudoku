package com.kalme.sudoku.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import com.kalme.sudoku.game.SudokuGame

class Board(val width: Float, private val offsetY: Float, private val padding: Float, private val view: View, private val numberPad: NumberPad) {
    private var thinLinePaint: Paint = Paint()
    private var thickLinePaint: Paint = Paint()
    private val cellHighlightPaint: Paint = Paint()
    private val uneditableCellPaint: Paint = Paint()
    private val textPaint: Paint = Paint()
    private val mistakeTextPaint: Paint = Paint()

    private val cells = mutableListOf<Cell>()
    private var pressedCell: Cell? = null

    private var isOpen = true

    private val sudoku: SudokuGame = SudokuGame()
    private val uneditableCells = mutableListOf<Cell>()

    init {
        thinLinePaint.color = Color.BLACK
        thinLinePaint.style = Paint.Style.FILL
        thinLinePaint.strokeWidth = 2f
        thinLinePaint.isAntiAlias = false

        thickLinePaint.color = Color.BLACK
        thickLinePaint.style = Paint.Style.FILL
        thickLinePaint.strokeWidth = 6f
        thickLinePaint.isAntiAlias = false

        cellHighlightPaint.color = Color.LTGRAY
        cellHighlightPaint.isAntiAlias = false

        uneditableCellPaint.color = Color.argb(255, 211, 238, 255)
        uneditableCellPaint.isAntiAlias = false

        textPaint.color = Color.BLACK
        textPaint.isAntiAlias = true
        textPaint.textSize = width / 11.46f

        mistakeTextPaint.color = Color.RED
        mistakeTextPaint.isAntiAlias = true
        mistakeTextPaint.textSize = textPaint.textSize

        numberPad.closedListener.add(object: Event {
            override fun onInvoke() {
                isOpen = true
                pressedCell = null
            }
        })

        numberPad.digitSelectedListener.add(object: Event {
            override fun onInvoke() {
                val digit = numberPad.getLastDigit()
                if(pressedCell == null || digit == null) return

                sudoku.setValue(digit, pressedCell?.x!!, pressedCell?.y!!)
            }
        })

        initializeCells()
        fetchUneditableCells()
    }

    fun preGenerateAhead(){
        sudoku.preGenerateAhead()
    }

    fun generateNew(){
        sudoku.generate()
        fetchUneditableCells()

        view.invalidate()
    }

    fun showSolution(){
        sudoku.showSolution()
        view.invalidate()
    }

    fun isOpen(): Boolean {
        return isOpen
    }

    // Keeps track of each cell's position and area on screen in a list
    private fun initializeCells() {
        for(y in 0 until 9){
            for(x in 0 until 9){
                val cell = getCellAreaOnScreen(x, y)
                cells.add(Cell(cell, x, y))
            }
        }
    }

    // All cells are generated, but only some are displayed as hints. This method fetches these cells
    private fun fetchUneditableCells(){
        uneditableCells.clear()

        cells.forEach {
            val value = sudoku.getValue(it.x, it.y)!!
            if(value > 0) uneditableCells.add(it)
        }
    }

    // The square area the board occupies
    private fun getBoardAreaOnScreen(): RectF {
        return RectF(padding, padding + offsetY, width - padding, width - padding + offsetY)
    }

    // The grid is 9x9. This method gets the area on screen that a particular cell takes up
    private fun getCellAreaOnScreen(x: Int, y: Int): RectF {
        val boardRect = getBoardAreaOnScreen()

        val thinStroke = thinLinePaint.strokeWidth
        val thinStrokeHalf = thinStroke / 2
        val thickStroke = thickLinePaint.strokeWidth
        val thickStrokeHalf = thickStroke / 2
        val thirdWidth = (boardRect.width() - thickStroke * 4 - thinStroke * 6) / 9

        boardRect.top += thickStrokeHalf
        boardRect.left += thickStrokeHalf

        val left =  boardRect.left + (boardRect.width() / 3f) * (x / 3) + thickStrokeHalf + thirdWidth * (x % 3) + thinStrokeHalf * (x % 3)
        val top =  boardRect.top + (boardRect.height() / 3f) * (y / 3) + thickStrokeHalf + thirdWidth * (y % 3) + thinStrokeHalf * (y % 3)

        return RectF(left, top, left + thirdWidth, top + thirdWidth)
    }

    fun draw (canvas: Canvas) {
        if(pressedCell != null) canvas.drawRect(pressedCell!!.rect, cellHighlightPaint)

        val thinStroke = thinLinePaint.strokeWidth
        val thickStroke = thickLinePaint.strokeWidth
        val thickStrokeHalf = thickStroke / 2

        val boardRect = getBoardAreaOnScreen()
        val thirdWidth = (boardRect.width() - thickStroke * 4 - thinStroke * 6) / 9

        boardRect.top += thickStrokeHalf
        boardRect.left += thickStrokeHalf
        boardRect.bottom -= thickStrokeHalf
        boardRect.right -= thickStrokeHalf

        uneditableCells.forEach {
            canvas.drawRect(it.rect, uneditableCellPaint)
        }

        for(i in 0 until 4){
            val x = boardRect.left + (boardRect.width() / 3f) * i
            canvas.drawLine(x, boardRect.top - thickStrokeHalf, x, boardRect.bottom + thickStrokeHalf, thickLinePaint)

            val y = boardRect.top + (boardRect.height() / 3f) * i
            canvas.drawLine(boardRect.left - thickStrokeHalf, y, boardRect.right + thickStrokeHalf, y, thickLinePaint)

            if(i > 2) continue
            for(j in 1 until 3){
                val xSmall = x + thickStrokeHalf + thirdWidth * j + thinStroke / 2 * j
                canvas.drawLine(xSmall, boardRect.top, xSmall, boardRect.bottom, thinLinePaint)

                val ySmall = y + thickStrokeHalf + thirdWidth * j + thinStroke / 2 * j
                canvas.drawLine(boardRect.left, ySmall, boardRect.right, ySmall, thinLinePaint)
            }
        }

        for(y in 0 until 9){
            for(x in 0 until 9){
                val cell = getCellAreaOnScreen(x, y)
                val digit = sudoku.getValue(x, y)
                if(digit === null || digit < 1) continue

                canvas.drawText(digit.toString(), cell.left + cell.width() / 4, cell.bottom - cell.width() / 5, if(sudoku.matches(x, y)) textPaint else mistakeTextPaint)
            }
        }
    }

    private fun getCellFromPositionOnScreen(x: Float, y: Float): Cell? {
        for(i in 0 until cells.size){
            if(cells[i].isInside(x, y)) return cells[i]
        }

        return null
    }

    // When a user touches the board a number pad is shown
    fun registerClick(event: MotionEvent){
        if(numberPad.isOpen()) return

        if(event.action == MotionEvent.ACTION_DOWN){
            var cell = getCellFromPositionOnScreen(event.x, event.y)
            if(uneditableCells.contains(cell)) cell = null

            pressedCell = cell

            view.invalidate()
        }else if(event.action == MotionEvent.ACTION_UP){
            val cell = getCellFromPositionOnScreen(event.x, event.y)

            if(pressedCell != null && cell != null) {
                var x = (cell.rect.left + cell.rect.width() / 2) - numberPad.width / 2
                val y = (cell.rect.top + cell.rect.height() / 2) + 20

                val rect = getBoardAreaOnScreen()

                if(x < rect.left) x = rect.left
                if(x + numberPad.width > rect.right) x = rect.right - numberPad.width

                numberPad.setOffset(x.toInt(), y.toInt())
                numberPad.open()
                isOpen = false

                view.invalidate()
            }else{
                pressedCell = null
                view.invalidate()
            }
        }
    }
}