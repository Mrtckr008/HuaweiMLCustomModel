package com.huawei.sample.huaweimlcustommodel.huaweimlutils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.huawei.hms.mlsdk.common.MLException
import com.huawei.hms.mlsdk.custom.*
import java.io.IOException
import kotlin.collections.HashMap


class InterpreterManager(context: Context,modelType: ModelOperator.Model) {
    private var mlModelExecutor: MLModelExecutor? = null
    private var modelOperator: ModelOperator? = null
    private var modelName: String? = null
    private var modelFullName: String? = null // .om, .mslite, .ms
    private lateinit var callbackResult :(HashMap<Int,String>) -> Unit
    private var hashMapData :HashMap<Int,String>?= hashMapOf()

    init {
        modelOperator = ModelOperator.create(context, modelType)
        modelName = modelOperator!!.modelName
        modelFullName = modelOperator!!.modelFullName
    }

    fun createCustomModelFromAssetFile(bitmap: Bitmap, position: Int, callback: (analyzeResult:HashMap<Int,String>) -> Unit) {
        callbackResult=callback
        val localModel = MLCustomLocalModel.Factory(modelName).setAssetPathFile(modelFullName).create()
        val settings = MLModelExecutorSettings.Factory(localModel).create()
        try {
            mlModelExecutor = MLModelExecutor.getInstance(settings)
            createMLModelInputs(bitmap,position)
        } catch (error: MLException) {
            error.printStackTrace()
        }
    }

    private fun createMLModelInputs(bitmap: Bitmap, position: Int) {
        val input: Any? = modelOperator!!.getInput(bitmap)
        Log.d("executorImpl", "interpret pre process")
        var inputs: MLModelInputs? = null
        try {
            inputs = MLModelInputs.Factory().add(input).create()
        } catch (e: MLException) {
            Log.e("executorImpl", "add inputs failed! " + e.message)
        }
        var inOutSettings: MLModelInputOutputSettings? = null
        try {
            val settingsFactory =
                MLModelInputOutputSettings.Factory()
            settingsFactory.setInputFormat(
                0,
                modelOperator!!.inputType(),
                modelOperator!!.inputShape()
            )
            val outputSettingsList: java.util.ArrayList<IntArray> =
                modelOperator!!.outputShapeList()
            for (i in outputSettingsList.indices) {
                settingsFactory.setOutputFormat(
                    i,
                    modelOperator!!.outputType(),
                    outputSettingsList[i]
                )
            }
            inOutSettings = settingsFactory.create()
        } catch (e: MLException) {
            Log.e(
                "executorImpl",
                "set input output format failed! " + e.message
            )
        }
        Log.d("executorImpl", "interpret start")
        performModel(inputs, inOutSettings, position)
    }

    private fun performModel(
        inputs: MLModelInputs?,
        outputSettings: MLModelInputOutputSettings?,
        position: Int
    ) {
        mlModelExecutor!!.exec(inputs, outputSettings)
            .addOnSuccessListener { mlModelOutputs ->
                val result: String = modelOperator!!.resultPostProcess(mlModelOutputs).toString()
                hashMapData?.set(position, result)
                hashMapData?.let { callbackResult.invoke(it) }
                Log.i("executorImpl", "result: $result")
            }.addOnFailureListener { e ->
                e.printStackTrace()
                Log.e("executorImpl", "interpret failed, because " + e.message)
            }.addOnCompleteListener {
                try {
                    mlModelExecutor!!.close()
                } catch (error: IOException) {
                    error.printStackTrace()
                }
            }
    }
}