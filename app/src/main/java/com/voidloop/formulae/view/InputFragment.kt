package com.voidloop.formulae.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.google.android.material.snackbar.Snackbar
import com.voidloop.formulae.R
import com.voidloop.formulae.data.DataSource
import com.voidloop.formulae.utils.getViewModel
import com.voidloop.formulae.view.FormulaRenderFragment.Companion.IMAGE_DATA
import kotlinx.android.synthetic.main.fragment_input.*


class InputFragment : Fragment() {

    private val formulaeViewModel by lazy {
        getViewModel { FormulaeViewModel(DataSource(requireContext())) }
    }

    private val searchAdapter by lazy {
        FormulaArrayAdapter(
            requireContext(),
            formulaeViewModel,
            onItemSelected = {
                field_formula.setText(it.input)
                field_formula.dismissDropDown()
                renderFormula(it.formula)
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (field_formula as AutoCompleteTextView).apply {
            setAdapter(searchAdapter)
        }

        buttonRender.setOnClickListener {
            layout_formula_field_wrapper.isErrorEnabled = false

            when {
                field_formula.text.toString().trim()
                    .isEmpty() -> layout_formula_field_wrapper.error =
                    "Please enter a formula"

                else -> {
                    formulaeViewModel.renderFormula(
                        field_formula.text.toString().trim(),
                    )
                }
            }
        }

        formulaeViewModel.formulaeViewContract.observe(viewLifecycleOwner) {
            it?.getContentIfNotHandled()?.let { data ->
                when (data) {
                    is FormulaeViewContract.ProgressDisplay -> {
                        if (data.display) progress_bar.visibility =
                            View.VISIBLE else progress_bar.visibility =
                            View.GONE

                        // Enable/Disable data views
                        buttonRender.isVisible = !data.display
                        layout_formula_field_wrapper.isEnabled = !data.display
                    }

                    is FormulaeViewContract.MessageDisplay -> {
                        displayMessage(data.message)
                    }

                    is FormulaeViewContract.RenderFormula -> {
                        renderFormula(data.imageData)
                    }
                }
            }
        }
    }

    private fun displayMessage(message: String) {
        val snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(Color.RED)
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }

    private fun renderFormula(imageData: String) {
        // Ensure imageData is a valid svg
        try {
            SVG.getFromString(imageData)
        } catch (e: SVGParseException) {
            displayMessage("Failed to render formula. Data may be malformed.")
            return
        }

        // Put event in bundle to display in detail view
        val bundle = bundleOf(IMAGE_DATA to imageData)
        findNavController().navigate(
            R.id.action_inputFragment_to_FormulaRenderFragment,
            bundle
        )
    }
}