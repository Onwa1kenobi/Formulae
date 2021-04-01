package com.voidloop.formulae.view

sealed class FormulaeViewContract {
    // Contract class to display progress bar
    class ProgressDisplay(val display: Boolean) : FormulaeViewContract()

    // Contract class to display message to the user
    class MessageDisplay(val message: String) : FormulaeViewContract()

    // Contract object to notify render success
    class RenderFormula(val imageData: String) : FormulaeViewContract()
}