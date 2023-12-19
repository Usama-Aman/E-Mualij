package com.elementary.mualijpro.models.sockets

import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.RelativeLayout
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Suppress("DEPRECATION")
class AllMessages {

    @SerializedName("timestamp")
    @Expose
    private var timeStamp: String = ""

    @SerializedName("chat_id")
    @Expose
    private var chatId: String = ""

    @SerializedName("from_id")
    @Expose
    private var fromId: String = ""

    @SerializedName("from_type")
    @Expose
    private var fromType: String = ""

    @SerializedName("to_id")
    @Expose
    private var toId: String = ""

    @SerializedName("to_type")
    @Expose
    private var toType: String = ""

    @SerializedName("is_seen")
    @Expose
    private var isSeen: String = ""

    @SerializedName("is_deleted")
    @Expose
    private var isDeleted: String = ""

    @SerializedName("type")
    @Expose
    private var type: String = ""

    @SerializedName("date")
    @Expose
    private var date: String = ""

    @SerializedName("message")
    @Expose
    private var message: String = ""

    @SerializedName("media_url")
    @Expose
    private var mediaUrl: String = ""

    @SerializedName("media_thumb_url")
    @Expose
    private var mediaThumbUrl: String = ""

    @SerializedName("media_time")
    @Expose
    private var audioTotalTime: String = ""

    private var audioPlaying: Boolean? = false
    private var audioPaused: Boolean? = false
    private var audioProgressBar = 0
    private var audioTotalLength = 0
    private var mediaPlayer: MediaPlayer? = null
    private var audioBuffer: Boolean? = false

    private var audioSetForPlay: Boolean? = false
    fun getAudioSetForPlay(): Boolean? {
        return audioSetForPlay
    }

    fun setAudioSetForPlay(audioSetForPlay: Boolean?) {
        this.audioSetForPlay = audioSetForPlay
    }

    private var audioStarted: Boolean? = false
    private var audioTotalTimeCurrent: String = ""


    fun getAudioTotalTimeCurrent(): String {
        if (audioTotalTimeCurrent == null) return ""
        return audioTotalTimeCurrent
    }

    fun setAudioTotalTimeCurrent(audioTotalTimeCurrent: String) {
        this.audioTotalTimeCurrent = audioTotalTimeCurrent
    }

    fun getAudioStarted(): Boolean? {
        return audioStarted
    }

    fun setAudioStarted(audioStarted: Boolean?) {
        this.audioStarted = audioStarted
    }

    fun getTimeStamp(): String {
        if (timeStamp == null) return ""
        return timeStamp
    }

    fun setTimeStamp(timeStamp: String) {
        this.timeStamp = timeStamp
    }

    fun getChatId(): String {
        if (chatId == null) return ""
        return chatId
    }

    fun setChatId(chatId: String) {
        this.chatId = chatId
    }

    fun getFromId(): String {
        if (fromId == null) return ""
        return fromId
    }

    fun setFromId(fromId: String) {
        this.fromId = fromId
    }

    fun getFromType(): String {
        if (fromType == null) return ""
        return fromType
    }

    fun setFromType(fromType: String) {
        this.fromType = fromType
    }

    fun getToId(): String {
        if (toId == null) return ""
        return toId
    }

    fun setToId(toId: String) {
        this.toId = toId
    }

    fun getToType(): String {
        if (toType == null) return ""
        return toType
    }

    fun setToType(toType: String) {
        this.toType = toType
    }

    fun getIsSeen(): String {
        if (isSeen == null) return ""
        return isSeen
    }

    fun setIsSeen(isSeen: String) {
        this.isSeen = isSeen
    }

    fun getIsDeleted(): String {
        if (isDeleted == null) return ""
        return isDeleted
    }

    fun setIsDeleted(isDeleted: String) {
        this.isDeleted = isDeleted
    }

    fun getType(): String {
        if (type == null) return ""
        return type
    }

    fun setType(type: String) {
        this.type = type
    }

    fun getDate(): String {
        if (date == null) return ""
        return date
    }

    fun setDate(date: String) {
        this.date = date
    }

    fun getMessage(): String {
        if (message == null) return ""
        return message
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun getMediaUrl(): String {
        if (mediaUrl == null) return ""
        return mediaUrl
    }

    fun setMediaUrl(mediaUrl: String) {
        this.mediaUrl = mediaUrl
    }

    fun getMediaThumbUrl(): String {
        if (mediaThumbUrl == null) return ""
        return mediaThumbUrl
    }

    fun setMediaThumbUrl(mediaThumbUrl: String) {
        this.mediaThumbUrl = mediaThumbUrl
    }

    fun getAudioTotalTime(): String {
        if (audioTotalTime == null) return ""
        return audioTotalTime
    }

    fun setAudioTotalTime(audioTotalTime: String) {
        this.audioTotalTime = audioTotalTime
    }




    fun initMediaPlayer(url: String) {

        releaseMedia()

        mediaPlayer = MediaPlayer()
        try {
            //"http://203.170.68.162:8080/mualij/public/upload/chat_media/15874162658_MualijJWz5wogM1CcNTx9AOZySPemka1xTySXw.m4a"
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.prepareAsync()
        } catch (e: Exception) {

        }
    }

    private fun releaseMedia() {
        if (mediaPlayer != null && mediaPlayer?.isPlaying!!) {
            mediaPlayer?.seekTo(0)
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    private fun getMimeType(fileUrl: String): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(fileUrl)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }



    fun getMediaPlayerSeek(): MediaPlayer? {
        if (mediaPlayer == null) return null
        return mediaPlayer
    }

    fun playAudio() {
        if (mediaPlayer != null)
            mediaPlayer?.start()
    }

    fun pauseAudio() {

        if (mediaPlayer != null && mediaPlayer?.isPlaying!!)
            mediaPlayer?.pause()
    }

    fun stopAudio() {
        if (this.mediaPlayer != null) {
            mediaPlayer?.pause()
            mediaPlayer?.seekTo(0)
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    fun getAudioBuffer(): Boolean? {
        return audioBuffer
    }

    fun setAudioBuffer(audioBuffer: Boolean?) {
        this.audioBuffer = audioBuffer
    }

    fun getAudioPlaying(): Boolean? {
        return audioPlaying
    }

    fun setAudioPlaying(audioPlaying: Boolean?) {
        this.audioPlaying = audioPlaying
    }

    fun getAudioPaused(): Boolean? {
        return audioPaused
    }

    fun setAudioPaused(audioPaused: Boolean?) {
        this.audioPaused = audioPaused
    }

    fun getAudioProgressBar(): Int {
        return audioProgressBar
    }

    fun setAudioProgressBar(audioProgressBar: Int) {
        this.audioProgressBar = audioProgressBar
    }

    fun getAudioTotalLength(): Int {
        return audioTotalLength
    }

    fun setAudioTotalLength(audioTotalLength: Int) {
        this.audioTotalLength = audioTotalLength
    }

}