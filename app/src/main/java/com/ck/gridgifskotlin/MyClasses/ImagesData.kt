package com.ck.gridgifskotlin.MyClasses

class ImagesData {
    var originalImage: String? = null
    var previewImage: String? = null

    constructor() {}
    constructor(originalImage: String?, previewImage: String?) {
        this.originalImage = originalImage
        this.previewImage = previewImage
    }
}