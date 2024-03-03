
package com.capstone.group6.ui.interfaces

import com.google.android.material.bottomsheet.BottomSheetDialog

interface AdapterOnClick {
    fun onClick(item: String,position: Int)
    fun onClickIng(item: String, bottomSheetDialog: BottomSheetDialog,position:Int)
}