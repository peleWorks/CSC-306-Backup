package com.example.apitest.utilities

import android.widget.AdapterView

abstract class SimpleOnItemSelectedListener : AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {
        // default
    }
}