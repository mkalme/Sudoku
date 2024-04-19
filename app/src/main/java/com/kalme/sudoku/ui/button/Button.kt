package com.kalme.sudoku.ui.button

import com.kalme.sudoku.ui.Cell
import com.kalme.sudoku.ui.Event

data class Button(val cell: Cell, val text: String, val clickEvent: Event)