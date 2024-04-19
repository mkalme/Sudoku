package com.kalme.sudoku.game

import kotlinx.coroutines.*

// The purpose of this class is to handle all logic not related to UI
class SudokuGame {
    private val generator = SudokuGameGenerator()

    private var gameState: GameState = GameState(IntArray(0), IntArray(0))
    private val preGenerated = mutableListOf<GameState>()
    private val lock = Any()

    init {
        generate()
    }

    // Pre generates the grid so the app does not freeze at start
    fun preGenerateAhead(){
        for(i in 0..1){
            GlobalScope.launch { generateAndAddToList() }
            GlobalScope.launch { generateAndAddToList() }
        }
    }

    private fun generateAndAddToList(){
        val gameState = generator.generate()

        synchronized(lock) {
            preGenerated.add(gameState)
        }
    }

    fun generate(){
        if(preGenerated.size > 0){
            gameState = preGenerated[0]
            preGenerated.removeAt(0)
        }else{
            gameState = generator.generate()
        }
    }

    fun showSolution(){
        System.arraycopy(gameState.realBoard, 0, gameState.visibleBoard, 0, gameState.realBoard.size)
    }

    private fun setValue(value: Int, x: Int, y: Int, array: IntArray){
        if(x < 0 || y < 0 || x > 8 || y > 8) return
        array[y * 9 + x] = value
    }
    fun setValue(value: Int, x: Int, y: Int){
        setValue(value, x, y, gameState.visibleBoard)
    }

    private fun getValue(x: Int, y: Int, array: IntArray): Int?{
        if(x < 0 || y < 0 || x > 8 || y > 8) return null
        return array[y * 9 + x]
    }

    fun getValue(x: Int, y: Int): Int?{
        return getValue(x, y, gameState.visibleBoard)
    }
    fun matches(x: Int, y: Int): Boolean{
        return getValue(x, y, gameState.visibleBoard) == getValue(x, y, gameState.realBoard)
    }
}