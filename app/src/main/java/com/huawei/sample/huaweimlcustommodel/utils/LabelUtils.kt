/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */
package com.huawei.sample.huaweimlcustommodel.utils

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.util.*

object LabelUtils {
    private const val TAG = "MainActivity"
    fun readLabels(
        context: Context,
        assetFileName: String?
    ): ArrayList<String> {
        val result = ArrayList<String>()
        var inputStream: InputStream? = null
        try {
            inputStream = context.assets.open(assetFileName!!)
            val br =
                BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            try {
                var readString = ""
                while (br.readLine().also {
                    if (it != null) {
                        readString = it
                    }
                    } != null) {
                    result.add(readString)
                }
            }
            catch (e:Exception){}
            br.close()
        } catch (error: IOException) {
            Log.e(TAG, "Asset file doesn't exist: " + error.message)
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (error: IOException) {
                    Log.e(TAG, "close failed: " + error.message)
                }
            }
        }
        return result
    }

    @JvmStatic
    fun processResult(
        labelList: List<String>,
        probabilities: FloatArray
    ): String {
        val localResult: MutableMap<String, Float> =
            HashMap()
        val compare = ValueComparator(localResult)
        for (i in probabilities.indices) {
            localResult[labelList[i]] = probabilities[i]
        }
        val result =
            TreeMap<String, Float>(compare)
        result.putAll(localResult)
        val builder = StringBuilder()
        builder.append(result.firstKey())
        /*
        // This part is so important. If you want get all list that product could be, You have to use this part.
        int total = 0;
        for (Map.Entry<String, Float> entry : result.entrySet()) {
            if (total == 10 || entry.getValue() <= 0) {
                break;
            }
            builder.append(entry.getKey());
            total++;
        }
         */return builder.toString()
    }

    private class ValueComparator internal constructor(var base: Map<String, Float>) :
        Comparator<String?> {
        override fun compare(o1: String?, o2: String?): Int {
            return if (base[o1]!! >= base[o2]!!) {
                -1
            } else {
                1
            }
        }

    }
}