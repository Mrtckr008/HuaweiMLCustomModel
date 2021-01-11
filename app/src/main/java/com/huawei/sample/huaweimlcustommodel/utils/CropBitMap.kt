/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */
package com.huawei.sample.huaweimlcustommodel.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect

class CropBitMap(private val targetHeight: Int, private val targetWidth: Int) {
    private val output: Bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
    fun getCropBitmap(input: Bitmap): Bitmap {
        val srcL: Int
        val srcR: Int
        val srcT: Int
        val srcB: Int
        val dstL: Int
        val dstR: Int
        val dstT: Int
        val dstB: Int
        val w = input.width
        val h = input.height
        if (targetWidth > w) { // padding
            srcL = 0
            srcR = w
            dstL = (targetWidth - w) / 2
            dstR = dstL + w
        } else { // cropping
            dstL = 0
            dstR = targetWidth
            srcL = (w - targetWidth) / 2
            srcR = srcL + targetWidth
        }
        if (targetHeight > h) { // padding
            srcT = 0
            srcB = h
            dstT = (targetHeight - h) / 2
            dstB = dstT + h
        } else { // cropping
            dstT = 0
            dstB = targetHeight
            srcT = (h - targetHeight) / 2
            srcB = srcT + targetHeight
        }
        val src = Rect(srcL, srcT, srcR, srcB)
        val dst = Rect(dstL, dstT, dstR, dstB)
        Canvas(output).drawBitmap(input, src, dst, null)
        return output
    }

}