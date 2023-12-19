package com.elementary.mualijpro.adapters

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elementary.mualijpro.models.sockets.AllMessages
import com.elementary.mualijpro.R
import com.elementary.mualijpro.dialogs.DialogChat
import com.elementary.mualijpro.models.posts.dashboard.patient.PatientDataResponse
import com.elementary.mualijpro.models.posts.user.User
import com.elementary.mualijpro.utils.AppUtils
import com.elementary.mualijpro.utils.Constants
import com.elementary.mualijpro.utils.UserDataPref
import com.makeramen.roundedimageview.RoundedImageView
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


@Suppress("DEPRECATION")
class ChatAdapter(
    private var activity: Activity,
    private var dialogChat: DialogChat,
    allMessages: ArrayList<AllMessages>,
    private var patientObj: PatientDataResponse
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var allMessages: ArrayList<AllMessages>? = null
    var userData: User = UserDataPref.getInstance(activity!!).getUserData()!!

    init {
        this.allMessages = allMessages
    }

    override fun onBindViewHolder(vHolder: RecyclerView.ViewHolder, position: Int) {


        // vHolder.setIsRecyclable(false)

        val startTime = System.currentTimeMillis()

        when (getItemViewType(position)) {
            yourVideo -> {
                val holder = vHolder as YourVideoHolder
                Glide.with(activity)
                    .load(userData.getFullImage())
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .into(holder.ivProfile)


                var messageObj = allMessages!![position]

                Glide.with(activity)
                    .load(messageObj.getMediaThumbUrl())
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .into(holder.ivVideoThumb)


                holder.llChild.setOnClickListener {

                    val intent = Intent(Intent.ACTION_VIEW)
                    val data = Uri.parse(messageObj.getMediaUrl())
                    intent.setDataAndType(data, "video/*")
                    activity!!.startActivity(intent)

                }

                holder.llChild.setOnLongClickListener {
                    if (Constants.isExpired == 0)
                        dialogChat.removeMessage(position)
                    return@setOnLongClickListener true
                }
                holder.llParent.setOnLongClickListener {
                    if (Constants.isExpired == 0)
                        dialogChat.removeMessage(position)
                    return@setOnLongClickListener true
                }


            }
            yourMessage -> {
                val holder = vHolder as YourMessageHolder
                Glide.with(activity)
                    .load(userData.getFullImage())
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .into(holder.ivProfile)


                var messageObj = allMessages!![position]
                holder.tvMsg.text = messageObj.getMessage()
                holder.tvSentTime.text = messageObj.getDate()

                holder.llParent.setOnLongClickListener {
                    if (Constants.isExpired == 0)
                        dialogChat.removeMessage(position)
                    return@setOnLongClickListener true
                }


            }
            yourImage -> {
                val holder = vHolder as YourImageHolder
                Glide.with(activity)
                    .load(userData.getFullImage())
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .into(holder.ivProfile)


                var messageObj = allMessages!![position]
                Glide.with(activity)
                    .load(messageObj?.getMediaUrl())
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .into(holder.imageView)


                holder.llParent.setOnLongClickListener {
                    if (Constants.isExpired == 0)
                        dialogChat.removeMessage(position)
                    return@setOnLongClickListener true
                }
                holder.imageView.setOnLongClickListener {
                    if (Constants.isExpired == 0)
                        dialogChat.removeMessage(position)
                    return@setOnLongClickListener true
                }


                holder.imageView.setOnClickListener {
                    dialogChat.chatGallery(messageObj.getMediaUrl())
                }

            }
            yourAudio -> {
                val holder = vHolder as YourAudioHolder
                var messageObj = allMessages!![position]

                holder.skAudio.id = position
                holder.skAudio.tag = position

                if (messageObj.getAudioTotalLength() > 0)
                    holder.skAudio.max = messageObj.getAudioTotalLength()



                if(!messageObj.getAudioPlaying()!! && !messageObj.getAudioStarted()!!)
                    holder.tvAudioTime.text = messageObj.getAudioTotalTime()
                else holder.tvAudioTime.text = messageObj.getAudioTotalTimeCurrent()

                holder.skAudio.progress = messageObj.getAudioProgressBar()


                if (messageObj.getAudioPlaying()!!) {

                    holder.rlLoading.visibility = View.GONE
                    holder.rlPlay.visibility = View.GONE
                    holder.rlPause.visibility = View.VISIBLE
                } else {

                    if (messageObj.getAudioBuffer()!!) {
                        holder.rlLoading.visibility = View.VISIBLE
                        holder.rlPlay.visibility = View.GONE
                        holder.rlPause.visibility = View.INVISIBLE
                    } else {
                        holder.rlLoading.visibility = View.GONE
                        holder.rlPlay.visibility = View.VISIBLE
                        holder.rlPause.visibility = View.GONE
                    }

                }

                holder.skAudio.setOnSeekBarChangeListener(object :
                    SeekBar.OnSeekBarChangeListener {
                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                    }

                    override fun onProgressChanged(
                        seekBar: SeekBar,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if (fromUser && (!messageObj.getAudioBuffer()!!) && (messageObj.getAudioStarted()!!)) {

                            if (messageObj.getMediaPlayerSeek() != null) {

                                if (messageObj.getMediaPlayerSeek()!!.isPlaying) {
                                    holder.rlPause.visibility = View.GONE
                                    holder.rlPlay.visibility = View.VISIBLE
                                    dialogChat.updateAudioPause(position)
                                }
                                if (messageObj.getAudioStarted()!!) {

                                    holder.skAudio.max =
                                        messageObj.getMediaPlayerSeek()!!.duration
                                    seekBar.max = messageObj.getMediaPlayerSeek()!!.duration
                                    messageObj.getMediaPlayerSeek()!!.seekTo(seekBar.progress)
                                    messageObj.setAudioTotalLength(messageObj.getMediaPlayerSeek()!!.duration)

                                    AppUtils.getTimeString(messageObj.getMediaPlayerSeek()!!.currentPosition.toLong())?.let {
                                        messageObj.setAudioTotalTimeCurrent(
                                            it
                                        )
                                    }
                                    holder.tvAudioTime.text = messageObj.getAudioTotalTimeCurrent()

                                }
                            }
                            messageObj.setAudioProgressBar(seekBar.progress)


                        } else if (fromUser) {
                            holder.skAudio.progress = 0
                        }
                    }
                })


                holder.rlPlay.setOnClickListener {
                    try {

                        holder.rlPause.visibility = View.VISIBLE
                        holder.rlPlay.visibility = View.GONE
                        holder.rlLoading.visibility = View.VISIBLE
                        if (messageObj.getMediaPlayerSeek() == null) {
                            messageObj.initMediaPlayer(
                                messageObj.getMediaUrl()
                            )
                        }
                        dialogChat.updateAudioPlay(
                            position,
                            messageObj.getMediaPlayerSeek(),
                            holder.skAudio
                        )
                    } catch (e: Exception) {

                    }
                }

                holder.rlPause.setOnClickListener {
                    try {
                        holder.rlPause.visibility = View.GONE
                        holder.rlPlay.visibility = View.VISIBLE
                        dialogChat.updateAudioPause(position)
                    } catch (e: Exception) {

                    }
                }

                Glide.with(activity)
                    .load(userData.getFullImage())
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .into(holder.ivProfile)


                holder.rlPlay.setOnLongClickListener {
                    if (Constants.isExpired == 0)
                        dialogChat.removeMessage(position)
                    return@setOnLongClickListener true
                }
                holder.rlPause.setOnLongClickListener {
                    if (Constants.isExpired == 0)
                        dialogChat.removeMessage(position)
                    return@setOnLongClickListener true
                }
                holder.skAudio.setOnLongClickListener {
                    if (Constants.isExpired == 0)
                        dialogChat.removeMessage(position)
                    return@setOnLongClickListener true
                }
                holder.llChild.setOnLongClickListener {
                    if (Constants.isExpired == 0)
                        dialogChat.removeMessage(position)
                    return@setOnLongClickListener true
                }
                holder.llParent.setOnLongClickListener {
                    if (Constants.isExpired == 0)
                        dialogChat.removeMessage(position)
                    return@setOnLongClickListener true
                }

            }
            chatReport -> {
                val holder = vHolder as ChatReportHolder
                holder.rlReport.setOnClickListener {
                    dialogChat.reportPatient()
                }
            }
            chatJoined -> {
                val holder = vHolder as ChatJoinedHolder
                var messageObj = allMessages!![position]
                if (messageObj.getType().equals(
                        Constants.patientJoin,
                        ignoreCase = true
                    )
                ) {
                    holder.tvJoined.text = messageObj.getMessage()
                    holder.tvJoined.setTextColor(activity.resources.getColor(R.color.colorPrimary))
                } else {
                    holder.tvJoined.text =
                        activity?.resources?.getString(R.string.txt_you_have_joined)
                    holder.tvJoined.setTextColor(activity.resources.getColor(R.color.gray_dark))
                }

            }
            fromVideo -> {
                val holder = vHolder as FromVideoHolder
                Glide.with(activity)
                    .load(patientObj.getPhoto())
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .into(holder.ivProfile)

                var messageObj = allMessages!![position]
                holder.tvSentTime.text = messageObj.getDate()
                Glide.with(activity)
                    .load(messageObj.getMediaThumbUrl())
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .into(holder.ivVideoThumb)

                holder.llChild.setOnClickListener {

                    val intent = Intent(Intent.ACTION_VIEW)
                    val data = Uri.parse(messageObj.getMediaUrl())
                    intent.setDataAndType(data, "video/*")
                    activity!!.startActivity(intent)

                }
            }
            fromMessage -> {
                val holder = vHolder as FromMessageHolder
                Glide.with(activity)
                    .load(patientObj.getPhoto())
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .into(holder.ivProfile)

                var messageObj = allMessages!![position]
                holder.tvMsg.text = messageObj.getMessage()
                holder.tvSentTime.text = messageObj.getDate()
            }
            fromImage -> {
                val holder = vHolder as FromImageHolder
                Glide.with(activity)
                    .load(patientObj.getPhoto())
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .into(holder.ivProfile)

                var messageObj = allMessages!![position]
                Glide.with(activity)
                    .load(messageObj?.getMediaUrl())
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .into(holder.imageView)

                holder.imageView.setOnClickListener {
                    dialogChat.chatGallery(messageObj.getMediaUrl())
                }
                holder.tvSentTime.text = messageObj.getDate()
            }
            fromAudio -> {
                val holder = vHolder as FromAudioHolder
                Glide.with(activity)
                    .load(patientObj.getPhoto())
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .into(holder.ivProfile)


                var messageObj = allMessages!![position]
                holder.tvSentTime.text = messageObj.getDate()
                holder.skAudio.id = position
                holder.skAudio.tag = position
                if (messageObj.getAudioTotalLength() > 0)
                    holder.skAudio.max = messageObj.getAudioTotalLength()

                if(!messageObj.getAudioPlaying()!! && !messageObj.getAudioStarted()!!)
                    holder.tvAudioTime.text = messageObj.getAudioTotalTime()
                else holder.tvAudioTime.text = messageObj.getAudioTotalTimeCurrent()

                holder.skAudio.progress = messageObj.getAudioProgressBar()


                holder.skAudio.setOnSeekBarChangeListener(object :
                    SeekBar.OnSeekBarChangeListener {
                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                    }

                    override fun onProgressChanged(
                        seekBar: SeekBar,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if (fromUser && (!messageObj.getAudioBuffer()!!) && (messageObj.getAudioStarted()!!)) {
                            if (messageObj.getMediaPlayerSeek() != null) {
                                if (messageObj.getMediaPlayerSeek()!!.isPlaying) {
                                    holder.rlPause.visibility = View.GONE
                                    holder.rlPlay.visibility = View.VISIBLE
                                    dialogChat.updateAudioPause(position)
                                }
                                if (messageObj.getAudioStarted()!!) {
                                    holder.skAudio.max = messageObj.getMediaPlayerSeek()!!.duration
                                    seekBar.max = messageObj.getMediaPlayerSeek()!!.duration
                                    messageObj.getMediaPlayerSeek()!!.seekTo(seekBar.progress)
                                    messageObj.setAudioTotalLength(messageObj.getMediaPlayerSeek()!!.duration)


                                    AppUtils.getTimeString(messageObj.getMediaPlayerSeek()!!.currentPosition.toLong())?.let {
                                        messageObj.setAudioTotalTimeCurrent(
                                            it
                                        )
                                    }
                                    holder.tvAudioTime.text = messageObj.getAudioTotalTimeCurrent()

                                }
                            }
                            messageObj.setAudioProgressBar(seekBar.progress)
                        }else if (fromUser) {
                            holder.skAudio.progress = 0
                        }
                    }
                })

                if (messageObj.getAudioPlaying()!!) {
                    holder.rlLoading.visibility = View.GONE
                    holder.rlPause.visibility = View.VISIBLE
                    holder.rlPlay.visibility = View.GONE
                } else {
                    if (messageObj.getAudioBuffer()!!) {
                        holder.rlLoading.visibility = View.VISIBLE
                        holder.rlPlay.visibility = View.GONE
                        holder.rlPause.visibility = View.INVISIBLE
                    } else {
                        holder.rlLoading.visibility = View.GONE
                        holder.rlPlay.visibility = View.VISIBLE
                        holder.rlPause.visibility = View.GONE
                    }
                }

                holder.rlPlay.setOnClickListener {
                    try {
                        holder.rlPause.visibility = View.VISIBLE
                        holder.rlPlay.visibility = View.GONE
                        holder.rlLoading.visibility = View.VISIBLE
                        if (messageObj.getMediaPlayerSeek() == null)
                            messageObj.initMediaPlayer(
                                messageObj.getMediaUrl()
                            )
                        dialogChat.updateAudioPlay(
                            position,
                            messageObj.getMediaPlayerSeek(),
                            holder.skAudio
                        )
                    } catch (e: Exception) {

                    }
                }

                holder.rlPause.setOnClickListener {
                    try {
                        holder.rlPause.visibility = View.GONE
                        holder.rlPlay.visibility = View.VISIBLE
                        dialogChat.updateAudioPause(position)

                    } catch (e: Exception) {

                    }
                }
            }
        }

        //..bind view stuff
        /*  Log.e(
              "Time: ",
              "bindView time: " + (System.currentTimeMillis() - startTime)
          )*/
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(viewGroup.context)
        val v: View
        when (viewType) {
            yourVideo -> {
                v = inflater.inflate(R.layout.item_chat_your_video, viewGroup, false)
                viewHolder = YourVideoHolder(v)
            }
            yourMessage -> {
                v = inflater.inflate(R.layout.item_chat_your_message, viewGroup, false)
                viewHolder = YourMessageHolder(v)
            }
            yourImage -> {
                v = inflater.inflate(R.layout.item_chat_your_image, viewGroup, false)
                viewHolder = YourImageHolder(v)
            }
            yourAudio -> {
                v = inflater.inflate(R.layout.item_chat_your_audio, viewGroup, false)
                viewHolder = YourAudioHolder(v)
            }
            chatReport -> {
                v = inflater.inflate(R.layout.item_chat_report, viewGroup, false)
                viewHolder = ChatReportHolder(v)
            }
            chatJoined -> {
                v = inflater.inflate(R.layout.item_chat_joined, viewGroup, false)
                viewHolder = ChatJoinedHolder(v)
            }
            fromVideo -> {
                v = inflater.inflate(R.layout.item_chat_from_video, viewGroup, false)
                viewHolder = FromVideoHolder(v)
            }

            fromMessage -> {
                v = inflater.inflate(R.layout.item_chat_from_message, viewGroup, false)
                viewHolder = FromMessageHolder(v)
            }
            fromImage -> {
                v = inflater.inflate(R.layout.item_chat_from_image, viewGroup, false)
                viewHolder = FromImageHolder(v)
            }
            fromAudio -> {
                v = inflater.inflate(R.layout.item_chat_from_audio, viewGroup, false)
                viewHolder = FromAudioHolder(v)
            }
            chatEnded -> {
                v = inflater.inflate(R.layout.item_chat_ended, viewGroup, false)
                viewHolder = ChatEndedHolder(v)
            }

        }
        return viewHolder!!
    }

    inner class YourVideoHolder(v: View) : RecyclerView.ViewHolder(v) {

        var llParent: LinearLayout = v.findViewById(R.id.llParent)
        var llChild: LinearLayout = v.findViewById(R.id.llChild)
        var ivVideoThumb: RoundedImageView = v.findViewById(R.id.ivVideoThumb)
        var tvSentTime: TextView = v.findViewById(R.id.tvSentTime)
        var ivSeen: ImageView = v.findViewById(R.id.ivSeen)
        var ivProfile: CircleImageView = v.findViewById(R.id.ivProfile)

    }

    inner class YourMessageHolder(v: View) : RecyclerView.ViewHolder(v) {

        var llParent: LinearLayout = v.findViewById(R.id.llParent)
        var llChild: LinearLayout = v.findViewById(R.id.llChild)
        var tvMsg: TextView = v.findViewById(R.id.tvMsg)
        var tvSentTime: TextView = v.findViewById(R.id.tvSentTime)
        var ivSeen: ImageView = v.findViewById(R.id.ivSeen)
        var ivProfile: CircleImageView = v.findViewById(R.id.ivProfile)

    }

    inner class YourImageHolder(v: View) : RecyclerView.ViewHolder(v) {

        var llParent: LinearLayout = v.findViewById(R.id.llParent)
        var llChild: LinearLayout = v.findViewById(R.id.llChild)
        var imageView: RoundedImageView = v.findViewById(R.id.imageView)
        var tvSentTime: TextView = v.findViewById(R.id.tvSentTime)
        var ivSeen: ImageView = v.findViewById(R.id.ivSeen)
        var ivProfile: CircleImageView = v.findViewById(R.id.ivProfile)

    }

    inner class YourAudioHolder(v: View) : RecyclerView.ViewHolder(v) {
        var llParent: LinearLayout = v.findViewById(R.id.llParent)
        var rlLoading: RelativeLayout = v.findViewById(R.id.rlLoading)
        var llChild: LinearLayout = v.findViewById(R.id.llChild)
        var rlPlay: RelativeLayout = v.findViewById(R.id.rlPlay)
        var rlPause: RelativeLayout = v.findViewById(R.id.rlPause)
        var tvAudioTime: TextView = v.findViewById(R.id.tvAudioTime)
        var tvSentTime: TextView = v.findViewById(R.id.tvSentTime)
        var skAudio: SeekBar = v.findViewById(R.id.skAudio)
        var ivSeen: ImageView = v.findViewById(R.id.ivSeen)
        var ivProfile: CircleImageView = v.findViewById(R.id.ivProfile)

    }

    inner class ChatReportHolder(v: View) : RecyclerView.ViewHolder(v) {

        var rlReport: RelativeLayout = v.findViewById(R.id.rlReport)

    }

    inner class ChatJoinedHolder(v: View) : RecyclerView.ViewHolder(v) {

        var llJoined: LinearLayout = v.findViewById(R.id.llJoined)
        var tvJoined: TextView = v.findViewById(R.id.tvJoined)

    }

    inner class FromVideoHolder(v: View) : RecyclerView.ViewHolder(v) {

        var llParent: LinearLayout = v.findViewById(R.id.llParent)
        var llChild: LinearLayout = v.findViewById(R.id.llChild)
        var ivVideoThumb: RoundedImageView = v.findViewById(R.id.ivVideoThumb)
        var tvSentTime: TextView = v.findViewById(R.id.tvSentTime)
        var ivSeen: ImageView = v.findViewById(R.id.ivSeen)
        var ivProfile: CircleImageView = v.findViewById(R.id.ivProfile)

    }

    inner class FromMessageHolder(v: View) : RecyclerView.ViewHolder(v) {

        var llParent: LinearLayout = v.findViewById(R.id.llParent)
        var llChild: LinearLayout = v.findViewById(R.id.llChild)
        var tvMsg: TextView = v.findViewById(R.id.tvMsg)
        var tvSentTime: TextView = v.findViewById(R.id.tvSentTime)
        var ivSeen: ImageView = v.findViewById(R.id.ivSeen)
        var ivProfile: CircleImageView = v.findViewById(R.id.ivProfile)
    }

    inner class FromImageHolder(v: View) : RecyclerView.ViewHolder(v) {

        var llParent: LinearLayout = v.findViewById(R.id.llParent)
        var llChild: LinearLayout = v.findViewById(R.id.llChild)
        var imageView: RoundedImageView = v.findViewById(R.id.imageView)
        var tvSentTime: TextView = v.findViewById(R.id.tvSentTime)
        var ivSeen: ImageView = v.findViewById(R.id.ivSeen)
        var ivProfile: CircleImageView = v.findViewById(R.id.ivProfile)

    }

    inner class FromAudioHolder(v: View) : RecyclerView.ViewHolder(v) {
        var rlLoading: RelativeLayout = v.findViewById(R.id.rlLoading)
        var llParent: LinearLayout = v.findViewById(R.id.llParent)
        var llChild: LinearLayout = v.findViewById(R.id.llChild)
        var rlPlay: RelativeLayout = v.findViewById(R.id.rlPlay)
        var rlPause: RelativeLayout = v.findViewById(R.id.rlPause)
        var tvAudioTime: TextView = v.findViewById(R.id.tvAudioTime)
        var tvSentTime: TextView = v.findViewById(R.id.tvSentTime)
        var skAudio: SeekBar = v.findViewById(R.id.skAudio)
        var ivSeen: ImageView = v.findViewById(R.id.ivSeen)
        var ivProfile: CircleImageView = v.findViewById(R.id.ivProfile)

    }


    inner class ChatEndedHolder(v: View) : RecyclerView.ViewHolder(v)

    override fun getItemViewType(position: Int): Int {
        var viewType = 0
        if (allMessages!![position].getType().equals(
                "patient_join",
                ignoreCase = true
            ) || allMessages!![position].getType().equals("doctor_join", ignoreCase = true)
        )
            viewType = chatJoined
        else if (allMessages!![position].getType().equals(
                Constants.doctorOfflineType,
                ignoreCase = true
            ) || allMessages!![position].getType().equals(
                Constants.patientOfflineType,
                ignoreCase = true
            )
        )
            viewType = chatEnded
        else if (allMessages!![position].getType().equals(
                "report",
                ignoreCase = true
            )
        )
            viewType = chatReport
        else if (UserDataPref.getInstance(activity).getUserData()?.getId() == Integer.parseInt(
                allMessages!![position].getFromId()
            )
        ) {
            when {
                allMessages!![position].getType().equals("message", ignoreCase = true) -> viewType =
                    yourMessage
                allMessages!![position].getType().equals("audio", ignoreCase = true) -> viewType =
                    yourAudio
                allMessages!![position].getType().equals("image", ignoreCase = true) -> viewType =
                    yourImage
                allMessages!![position].getType().equals("video", ignoreCase = true) -> viewType =
                    yourVideo
            }
        } else {
            when {
                allMessages!![position].getType().equals("message", ignoreCase = true) -> viewType =
                    fromMessage
                allMessages!![position].getType().equals("audio", ignoreCase = true) -> viewType =
                    fromAudio
                allMessages!![position].getType().equals("image", ignoreCase = true) -> viewType =
                    fromImage
                allMessages!![position].getType().equals("video", ignoreCase = true) -> viewType =
                    fromVideo
            }
        }
        return viewType
    }

    override fun getItemCount(): Int {
        return allMessages!!.size
    }

    companion object {
        private const val yourVideo = 0
        private const val yourMessage = 1
        private const val yourImage = 2
        private const val yourAudio = 3
        private const val chatReport = 4
        private const val chatJoined = 5
        private const val fromVideo = 6
        private const val fromMessage = 7
        private const val fromImage = 8
        private const val fromAudio = 9
        private const val chatEnded = 10
    }
}
