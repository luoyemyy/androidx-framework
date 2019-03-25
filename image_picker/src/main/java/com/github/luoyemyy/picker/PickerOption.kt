package com.github.luoyemyy.picker

class PickerOption(var fileProvider: String) {

    var pickerType: Int = 0 // 0 album camera 1 album 2 camera

    var minSelect: Int = 1
    var maxSelect: Int = 1

    var cropType: Int = 0               // 0 不裁剪 1 按尺寸 2 按比例
    var cropRequire: Boolean = true    // 是否必须裁剪
    var cropSize: Int = 0               //px 如果大于imageView的最小边，则取最小边
    var cropPercent: Float = 0f         //相对于imageView的最小边的比例
    var cropRatio: Float = 0f           //x:y 比例

    var compress: Boolean = false
    var compressMaxLength: Int = 0
}