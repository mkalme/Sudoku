package com.kalme.sudoku.game

import kotlin.random.Random

// ChatGPT helped with some methods. E.g.: hideCells(), solvePuzzle()
// At least half was written by hand, particularly the boilerplate
// Prompt: In kotlin I have an array like this: "    private val board = IntArray(9 * 9)". It represents a sudoku board. Write an algorithm that generates the puzzle.

class SudokuGameGenerator {
    private val random = Random(1)

    fun generate(): GameState {
        val realBoard = IntArray(9 * 9)
        val visibleBoard = IntArray(9 * 9)

        realBoard.fill(0)

        for(i in 0..2){
            generateMatrix(i, i, realBoard)
        }

        val output = GameState(realBoard, visibleBoard)

        solvePuzzle(output)

        System.arraycopy(realBoard, 0, visibleBoard, 0, realBoard.size)
        hideCells(output)

        return output
    }

    private fun solvePuzzle(gameState: GameState): Boolean {
        for (y in 0 until 9) {
            for (x in 0 until 9) {
                if (getValue(x, y, gameState.realBoard) == 0) {
                    for (num in 1..9) {
                        if (isColumnValid(x, num, gameState.realBoard) && isRowValid(y, num, gameState.realBoard) && isSubgridValid(x / 3, y / 3, num, gameState.realBoard)) {
                            setValue(num, x, y, gameState.realBoard)

                            if (solvePuzzle(gameState)) {
                                return true
                            }
                            setValue(0, x, y, gameState.realBoard)
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun isRowValid(y: Int, num: Int, board: IntArray): Boolean {
        for(i in 0..8){
            val value = getValue(i, y, board)!!
            if(value == num) return false
        }

        return true
    }

    private fun isColumnValid(x: Int, num: Int, board: IntArray): Boolean {
        for(i in 0..8){
            val value = getValue(x, i, board)!!
            if(value == num) return false
        }

        return true
    }

    private fun isSubgridValid(x: Int, y: Int, num: Int, board: IntArray): Boolean {
        for (row in 0 until 3) {
            for (col in 0 until 3) {
                val value = getValue(x * 3 + col, y * 3 + row, board)!!
                if(value == num) return false
            }
        }
        return true
    }

    private fun generateMatrix(matrixX: Int, matrixY: Int, board: IntArray){
        val uniqueDigits = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

        for(y in 0 until 3){
            for(x in 0 until 3){
                val index = random.nextInt(uniqueDigits.size)
                val digit = uniqueDigits[index]
                setValue( + digit, matrixX * 3 + x, matrixY * 3 + y, board)

                uniqueDigits.removeAt(index)
            }
        }
    }

    private fun hideCells(gameState: GameState) {
        val toHide = 45
        repeat(toHide) {
            var row: Int
            var col: Int
            do {
                row = Random.nextInt(9)
                col = Random.nextInt(9)
            } while (getValue(col, row, gameState.visibleBoard) == 0)
            setValue(0, col, row, gameState.visibleBoard)
        }
    }

    private fun setValue(value: Int, x: Int, y: Int, array: IntArray){
        if(x < 0 || y < 0 || x > 8 || y > 8) return
        array[y * 9 + x] = value
    }

    private fun getValue(x: Int, y: Int, array: IntArray): Int?{
        if(x < 0 || y < 0 || x > 8 || y > 8) return null
        return array[y * 9 + x]
    }
}