package com.github.luoyemyy.picker

class PickerOption(var fileProvider: String) {

    var pickerType: Int = 0 // 0 album camera 1 album 2 camera

    var minSelect: Int = 1
    var maxSelect: Int = 1

    var cropType: Int = 0               // 0 不裁剪 1 按尺寸 2 按比例
    var cropRequire: Boolean = false
    var cropWidth: Int = 0
    var cropHeight: Int = 0
    var cropPercent: Float = 0f         //相对于imageView的宽度的比例
    var cropRatio: Float = 0f           //x:y 比例
    var cropRatioFixed: Boolean = true  //x:y 比例固定

    var compress: Boolean = false
    var compressWidth: Int = 0
}