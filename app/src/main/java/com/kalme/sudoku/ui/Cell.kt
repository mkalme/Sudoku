package com.kalme.sudoku.ui

import android.graphics.RectF

// Small helper class used for storing touchable regions on screen with their grid position. Aka cells
data class Cell(val rect: RectF, val x: Int, val y: Int){
    fun isInside(x: Float, y: Float) : Boolean {
        return x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom
    }
}
