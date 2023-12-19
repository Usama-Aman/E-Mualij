package com.elementary.mualijpro.models.sockets

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MediaFileResponse {

    @SerializedName("file_name")
    @Expose
    private var fileName: String = ""
    @SerializedName("filePath")
    @Expose
    private var filePath: String = ""
    @SerializedName("thumb_name")
    @Expose
    private var thumbName: String = ""
    @SerializedName("thumbnailPath")
    @Expose
    private var thumbnailPath: String = ""

    fun getFileName(): String {
        if (fileName == null) return ""
        return fileName
    }

    fun setFileName(fileName: String) {
        this.fileName = fileName
    }

    fun getFilePath(): String {
        if (filePath == null) return ""
        return filePath
    }

    fun setFilePath(filePath: String) {
        this.filePath = filePath
    }

    fun getThumbName(): String {
        if (thumbName == null) return ""
        return thumbName
    }

    fun setThumbName(thumbName: String) {
        this.thumbName = thumbName
    }

    fun getThumbnailPath(): String {
        if (thumbnailPath == null) return ""
        return thumbnailPath
    }

    fun setThumbnailPath(thumbnailPath: String) {
        this.thumbnailPath = thumbnailPath
    }

}