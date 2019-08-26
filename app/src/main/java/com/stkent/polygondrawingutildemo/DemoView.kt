/*
 * Copyright 2019 Stuart Kent
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.stkent.polygondrawingutildemo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.stkent.polygondrawingutil.PolygonDrawingUtil

class DemoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val polygonDrawingUtil = PolygonDrawingUtil()
    private val polygonFillPaint = Paint(ANTI_ALIAS_FLAG)
    private val polygonStrokePaint = Paint(ANTI_ALIAS_FLAG)
    private val strokePath = Path()
    private var strokeWidth = 0f

    var numberOfSides = 3
        set(numberOfSides) {
            field = numberOfSides
            invalidate()
        }

    var cornerRadius = 120f
        set(cornerRadius) {
            field = cornerRadius
            invalidate()
        }

    var polygonRotation = 0f
        set(polygonRotation) {
            field = polygonRotation
            invalidate()
        }

    var scale = 0f
        set(scale) {
            field = scale
            invalidate()
        }

    init {
        strokeWidth = resources.getDimension(R.dimen.stroke_width)

        polygonFillPaint.apply {
            color = ContextCompat.getColor(context, R.color.colorAccentTranslucent)
            style = Paint.Style.FILL
        }

        polygonStrokePaint.apply {
            color = ContextCompat.getColor(context, R.color.colorAccent)
            strokeWidth = this@DemoView.strokeWidth
            style = Paint.Style.STROKE
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = (width / 2).toFloat()
        val centerY = (height / 2).toFloat()
        val radius = scale * (width / 2 - strokeWidth)

        // Method 1 (simpler for direct drawing):
        polygonDrawingUtil.drawPolygon(
            canvas,
            this.numberOfSides,
            centerX,
            centerY,
            radius,
            cornerRadius,
            polygonRotation,
            polygonFillPaint
        )

        // Method 2 (allows polygon Path post-processing if desired):
        polygonDrawingUtil.constructPolygonPath(
            strokePath,
            this.numberOfSides,
            centerX,
            centerY,
            radius,
            cornerRadius,
            polygonRotation
        )

        canvas.drawPath(strokePath, polygonStrokePaint)
    }

}
