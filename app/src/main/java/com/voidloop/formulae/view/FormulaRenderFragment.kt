package com.voidloop.formulae.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.navArgs
import com.caverock.androidsvg.SVG
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.voidloop.formulae.BuildConfig
import com.voidloop.formulae.R
import kotlinx.android.synthetic.main.fragment_formula_render.*
import org.jetbrains.annotations.NotNull
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class FormulaRenderFragment : BottomSheetDialogFragment() {

    private lateinit var imageData: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val safeArgs: FormulaRenderFragmentArgs by navArgs()
        imageData = safeArgs.imageData
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_formula_render, container, false)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.background_bottom_sheet_inset)
        return v
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }

        bottomSheetDialog.behavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    //In the EXPANDED STATE apply a new MaterialShapeDrawable with rounded corners
                    val newMaterialShapeDrawable = createMaterialShapeDrawable(bottomSheet)
                    ViewCompat.setBackground(bottomSheet, newMaterialShapeDrawable)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        return bottomSheetDialog
    }

    @NotNull
    private fun createMaterialShapeDrawable(@NonNull bottomSheet: View): MaterialShapeDrawable {
        val shapeAppearanceModel =
            //Create a ShapeAppearanceModel with the same shapeAppearanceOverlay used in the style
            ShapeAppearanceModel.builder(context, 0, R.style.ShapeAppearanceOverlay_Rounded)
                .build()

        //Create a new MaterialShapeDrawable (you can't use the original MaterialShapeDrawable in the BottomSheet)
        val currentMaterialShapeDrawable = bottomSheet.background as MaterialShapeDrawable
        val newMaterialShapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
        //Copy the attributes in the new MaterialShapeDrawable
        newMaterialShapeDrawable.initializeElevationOverlay(context)
        newMaterialShapeDrawable.fillColor = currentMaterialShapeDrawable.fillColor
        newMaterialShapeDrawable.tintList = currentMaterialShapeDrawable.tintList
        newMaterialShapeDrawable.elevation = currentMaterialShapeDrawable.elevation
        newMaterialShapeDrawable.strokeWidth = currentMaterialShapeDrawable.strokeWidth
        newMaterialShapeDrawable.strokeColor = currentMaterialShapeDrawable.strokeColor
        return newMaterialShapeDrawable
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val svg = SVG.getFromString(imageData)
        svg.setDocumentWidth("100%")
        svg.setDocumentHeight("100%")
        val picture = svg.renderToPicture()
        val drawable: Drawable = PictureDrawable(picture)
        imageFormula.setImageDrawable(drawable)

        buttonShare.setOnClickListener {
            shareImage(drawable.toBitmap())

//            val bitmap = Bitmap.createBitmap(
//                drawable.intrinsicWidth,
//                drawable.intrinsicHeight,
//                Bitmap.Config.ARGB_8888
//            )
//
//            val canvas = Canvas(bitmap)
//            canvas.drawColor(Color.WHITE)
//            drawable.setBounds(0, 0, canvas.width, canvas.height)
//            drawable.draw(canvas)
//
//
//            shareImage(bitmap)
        }
    }

    private fun shareImage(bitmap: Bitmap) {
        try {
            val cachePath = File(requireActivity().cacheDir, "images")
            cachePath.mkdirs()
            val stream =
                FileOutputStream("$cachePath/image.jpg") // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val imagePath = File(requireActivity().cacheDir, "images")
        val newFile = File(imagePath, "image.jpg")
        val contentUri: Uri? =
            FileProvider.getUriForFile(
                requireContext().applicationContext,
                BuildConfig.APPLICATION_ID,
                newFile
            )
        if (contentUri != null) {
            val shareIntent = Intent()
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.setDataAndType(
                contentUri, requireContext().contentResolver.getType(
                    contentUri
                )
            )
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
            shareIntent.type = "image/*"
            startActivity(Intent.createChooser(shareIntent, "Choose an app"))
        }
    }

    companion object {
        const val IMAGE_DATA = "imageData"
    }
}