/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */
package com.huawei.sample.huaweimlcustommodel.huaweimlutils

import android.content.Context
import android.graphics.Bitmap
import com.huawei.hms.mlsdk.custom.MLModelOutputs
import com.huawei.sample.huaweimlcustommodel.huaweimlutils.LabelUtils.processResult
import java.util.ArrayList

abstract class ModelOperator {
    enum class Model {
        LABEL
    }

    var modelName: String? = null
    var modelFullName: String? = null
    var modelLabelFile: String? = null
    var batchNum = 0
    abstract fun inputType(): Int
    abstract fun outputType(): Int
    abstract fun getInput(bmp: Bitmap?): Any?
    abstract fun inputShape(): IntArray?
    abstract fun outputShapeList(): ArrayList<IntArray>
    abstract fun resultPostProcess(output: MLModelOutputs?): String?
    fun getExecutorResult(
        label: List<String?>?,
        result: FloatArray?
    ): String {
        return processResult(label as List<String>, result!!)
    }

    companion object {
        fun create(
            activity: Context?,
            model: Model
        ): ModelOperator {
            return if (model == Model.LABEL) {
                ImageLableModel(activity!!)
            } else {
                throw UnsupportedOperationException()
            }
        }
    }
}