package com.example.apitest.utilities

import android.widget.SeekBar

abstract class SimpleOnSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        // default
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        // default
    }
}