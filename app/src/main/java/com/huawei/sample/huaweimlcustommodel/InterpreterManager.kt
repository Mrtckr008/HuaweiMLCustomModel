package com.huawei.sample.huaweimlcustommodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.huawei.hms.mlsdk.common.MLException
import com.huawei.hms.mlsdk.custom.*
import com.huawei.sample.huaweimlcustommodel.utils.CropBitMap
import java.io.IOException
import java.lang.ref.WeakReference
import java.text.FieldPosition
import java.util.*
import kotlin.collections.HashMap


class InterpreterManager(
    context: Context,
    modelType: ModelOperator.Model
) {
    private val weakContext: WeakReference<Context>
    private var modelExecutor: MLModelExecutor? = null
    private val modelType: ModelOperator.Model
    private var mModelOperator: ModelOperator? = null
    private var mModelName: String? = null
    private var mModelFullName // .om, .mslite, .ms
            : String? = null

    private lateinit var callbackResult :(HashMap<Int,String>) -> Unit
    private var hashmapData :HashMap<Int,String>?= hashMapOf()

    fun asset(bitmap: Bitmap,position: Int,callback: (analyzeResult:HashMap<Int,String>) -> Unit) {
        if (dumpBitmapInfo(bitmap)) {
            return
        }
        callbackResult=callback
        val localModel = MLCustomLocalModel.Factory(mModelName).setAssetPathFile(mModelFullName).create()
        val settings = MLModelExecutorSettings.Factory(localModel).create()
        try {
            modelExecutor = MLModelExecutor.getInstance(settings)
            executorImpl(bitmap,position)
        } catch (error: MLException) {
            error.printStackTrace()
        }
    }

    private fun dumpBitmapInfo(bitmap: Bitmap?): Boolean {
        if (bitmap == null) {
            return true
        }
        val width = bitmap.width
        val height = bitmap.height
        Log.e(TAG, "bitmap width is $width height $height")
        return false
    }

    private fun processBitMap(bitmap: Bitmap): Bitmap {
        val cropSize = Math.min(bitmap.width, bitmap.height)
        val crop = CropBitMap(cropSize, cropSize)
        val cropBitmap: Bitmap = crop.getCropBitmap(bitmap)
        dumpBitmapInfo(cropBitmap)
        return Bitmap.createScaledBitmap(
            cropBitmap,
            BITMAP_WIDTH,
            BITMAP_HEIGHT,
            false
        )
    }

    private fun executorImpl(bitmap: Bitmap,position: Int) {
        val inputBitmap = processBitMap(bitmap)
        val input: Any = mModelOperator!!.getInput(inputBitmap)
        Log.d(TAG, "interpret pre process")
        var inputs: MLModelInputs? = null
        try {
            inputs = MLModelInputs.Factory().add(input).create()
        } catch (e: MLException) {
            Log.e(TAG, "add inputs failed! " + e.message)
        }
        var inOutSettings: MLModelInputOutputSettings? = null
        try {
            val settingsFactory =
                MLModelInputOutputSettings.Factory()
            settingsFactory.setInputFormat(
                0,
                mModelOperator!!.inputType,
                mModelOperator!!.inputShape
            )
            val outputSettingsList: ArrayList<IntArray> =
                mModelOperator!!.outputShapeList
            for (i in outputSettingsList.indices) {
                settingsFactory.setOutputFormat(
                    i,
                    mModelOperator!!.outputType,
                    outputSettingsList[i]
                )
            }
            inOutSettings = settingsFactory.create()
        } catch (e: MLException) {
            Log.e(
                TAG,
                "set input output format failed! " + e.message
            )
        }
        Log.d(TAG, "interpret start")
        execModel(inputs, inOutSettings,position)
    }

    private fun execModel(
        inputs: MLModelInputs?,
        outputSettings: MLModelInputOutputSettings?,
        position: Int
    ) {
        modelExecutor!!.exec(inputs, outputSettings)
            .addOnSuccessListener { mlModelOutputs ->
                val result: String = mModelOperator!!.resultPostProcess(mlModelOutputs)
                hashmapData?.set(position, result)
                hashmapData?.let { callbackResult.invoke(it) }
                Log.i(TAG, "result: $result")
            }.addOnFailureListener { e ->
                e.printStackTrace()
                Log.e(TAG, "interpret failed, because " + e.message)
            }.addOnCompleteListener {
                try {
                    modelExecutor!!.close()
                } catch (error: IOException) {
                    error.printStackTrace()
                }
            }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val BITMAP_WIDTH = 224 // 128, 224
        private const val BITMAP_HEIGHT = 224 // 128, 224
    }

    private fun initEnvironment() {
        mModelOperator = ModelOperator.create(weakContext.get(), modelType)
        mModelName = mModelOperator!!.getModelName()
        mModelFullName = mModelOperator!!.getModelFullName()
    }

    init {
        this.modelType = modelType
        weakContext = WeakReference(context)
        initEnvironment()
    }
}