package com.voidloop.formulae.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voidloop.formulae.data.DataSource
import com.voidloop.formulae.data.Event
import com.voidloop.formulae.data.Result
import com.voidloop.formulae.data.local.FormulaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FormulaeViewModel(private val dataSource: DataSource) : ViewModel() {

    val formulaeViewContract = MutableLiveData<Event<FormulaeViewContract>>()

    fun renderFormula(input: String) {
        formulaeViewContract.value = Event(FormulaeViewContract.ProgressDisplay(true))

        viewModelScope.launch {
            val result = dataSource.normalizeTexFormula(input)
            formulaeViewContract.value = Event(FormulaeViewContract.ProgressDisplay(false))

            when (result) {
                is Result.Failure -> {
                    formulaeViewContract.value =
                        Event(FormulaeViewContract.MessageDisplay(result.message.toString()))
                }

                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        formulaeViewContract.value =
                            Event(FormulaeViewContract.MessageDisplay("Failed to render the formula."))
                    } else {
                        formulaeViewContract.value =
                            Event(FormulaeViewContract.RenderFormula(result.data))
                    }
                }
            }
        }
    }

    suspend fun searchFormulaLocally(input: String): List<FormulaEntity> =
        withContext(Dispatchers.IO) {
            val result = dataSource.fetchLocalList(input)
            return@withContext (result as Result.Success).data
        }
}