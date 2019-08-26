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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import kotlin.math.max

class MainActivity : AppCompatActivity(), OnSeekBarChangeListener {

    private var maxCornerRadius: Float = 0.toFloat()

    private lateinit var demoView: DemoView
    private lateinit var cornerRadiusSeekBar: SeekBar
    private lateinit var rotationSeekBar: SeekBar
    private lateinit var scaleSeekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        maxCornerRadius = resources.getDimension(R.dimen.max_corner_radius)

        initializeButtons()
        initializeSeekBars()
        initializeDemoView()
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        when (seekBar.id) {
            R.id.corner_radius_seek_bar -> updateCornerRadius(progress.toFloat())
            R.id.rotation_seek_bar -> updateRotation(progress.toFloat())
            R.id.scale_seek_bar -> updateScale(progress.toFloat())
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        // This method intentionally left blank.
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        // This method intentionally left blank.
    }

    private fun initializeButtons() {
        findViewById<Button>(R.id.increase_side_count_button).setOnClickListener {
            demoView.numberOfSides = demoView.numberOfSides + 1
        }

        findViewById<Button>(R.id.decrease_side_count_button).setOnClickListener {
            demoView.numberOfSides = max(3, demoView.numberOfSides - 1)
        }
    }

    private fun initializeDemoView() {
        demoView = findViewById(R.id.demo_view)
        updateCornerRadius(cornerRadiusSeekBar.progress.toFloat())
        updateRotation(rotationSeekBar.progress.toFloat())
        updateScale(scaleSeekBar.progress.toFloat())
    }

    private fun initializeSeekBars() {
        cornerRadiusSeekBar = findViewById(R.id.corner_radius_seek_bar)
        rotationSeekBar = findViewById(R.id.rotation_seek_bar)
        scaleSeekBar = findViewById(R.id.scale_seek_bar)

        cornerRadiusSeekBar.setOnSeekBarChangeListener(this)
        rotationSeekBar.setOnSeekBarChangeListener(this)
        scaleSeekBar.setOnSeekBarChangeListener(this)
    }

    private fun updateCornerRadius(cornerRadius: Float) {
        demoView.cornerRadius = cornerRadius / cornerRadiusSeekBar.max * maxCornerRadius
    }

    private fun updateRotation(rotation: Float) {
        demoView.polygonRotation = rotation * 360 / rotationSeekBar.max
    }

    private fun updateScale(scale: Float) {
        demoView.scale = scale / scaleSeekBar.max
    }

}
