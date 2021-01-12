package com.huawei.sample.huaweimlcustommodel.huaweimlutils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.huawei.hms.mlsdk.common.MLException
import com.huawei.hms.mlsdk.custom.*
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.HashMap


class InterpreterManager(context: Context, modelType: ModelOperator.Model) {
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

    private fun executorImpl(bitmap: Bitmap,position: Int) {
        val input: Any = mModelOperator!!.getInput(bitmap)
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