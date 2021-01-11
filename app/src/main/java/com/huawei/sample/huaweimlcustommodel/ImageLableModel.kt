/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */
package com.huawei.sample.huaweimlcustommodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.huawei.hms.mlsdk.custom.MLModelDataType
import com.huawei.hms.mlsdk.custom.MLModelOutputs
import com.huawei.sample.huaweimlcustommodel.utils.LabelUtils.readLabels
import java.util.*

class ImageLableModel(private val mContext: Context) : ModelOperator() {
    private val outputSize = 0
    private val labelList: List<String>
    override fun getInputType(): Int {
        return MLModelDataType.FLOAT32
    }

    override fun getOutputType(): Int {
        return MLModelDataType.FLOAT32
    }

    override fun getInput(inputBitmap: Bitmap): Any {
        val input =
            Array(1) {
                Array(BITMAP_SIZE) {
                    Array(BITMAP_SIZE) {
                        FloatArray(3)
                    }
                }
            }
        for (h in 0 until BITMAP_SIZE) {
            for (w in 0 until BITMAP_SIZE) {
                val pixel = inputBitmap.getPixel(w, h)
                input[batchNum][h][w][0] =
                    (Color.red(pixel) - IMAGE_MEAN[0]) / IMAGE_STD[0]
                input[batchNum][h][w][1] =
                    (Color.green(pixel) - IMAGE_MEAN[1]) / IMAGE_STD[1]
                input[batchNum][h][w][2] =
                    (Color.blue(pixel) - IMAGE_MEAN[2]) / IMAGE_STD[2]
            }
        }
        return input
    }

    override fun getInputShape(): IntArray {
        return intArrayOf(
            1,
            BITMAP_SIZE,
            BITMAP_SIZE,
            3
        )
    }

    override fun getOutputShapeList(): ArrayList<IntArray> {
        val outputShapeList =
            ArrayList<IntArray>()
        val outputShape = intArrayOf(1, labelList.size)
        outputShapeList.add(outputShape)
        return outputShapeList
    }

    override fun resultPostProcess(output: MLModelOutputs): String {
        val result =
            output.getOutput<Array<FloatArray>>(0)
        val probabilities = result[0]
        return getExecutorResult(labelList, probabilities)
    }

    companion object {
        private const val BITMAP_SIZE = 224
        private val IMAGE_MEAN =
            floatArrayOf(0.485f * 255f, 0.456f * 255f, 0.406f * 255f)
        private val IMAGE_STD =
            floatArrayOf(0.229f * 255f, 0.224f * 255f, 0.225f * 255f)
    }

    init {
        modelName = "mindspore"
        modelFullName = "mindspore" + ".ms"
        modelLabelFile = "labels.txt"
        labelList = readLabels(mContext, modelLabelFile)
    }
}