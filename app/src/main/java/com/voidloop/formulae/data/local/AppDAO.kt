package com.voidloop.formulae.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface FormulaDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFormula(formula: FormulaEntity)

    @Query("SELECT * FROM formulas WHERE input LIKE '%' || :input || '%'")
    fun suggestFormulas(input: String): List<FormulaEntity>

    @Query("SELECT * FROM formulas WHERE input == :input")
    fun searchFormula(input: String): FormulaEntity?
}