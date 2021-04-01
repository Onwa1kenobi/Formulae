package com.voidloop.formulae.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "formulas")
class FormulaEntity(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val input: String,
        val formula: String,
)