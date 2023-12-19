package com.elementary.mualijpro.utils

import androidx.annotation.Nullable

import androidx.recyclerview.widget.DiffUtil
import com.elementary.mualijpro.models.sockets.AllMessages


class MyDiffUtilCallback(newList: ArrayList<AllMessages>?, oldList: ArrayList<AllMessages>?) :
    DiffUtil.Callback() {
    var newList: ArrayList<AllMessages>? = newList
    var oldList: ArrayList<AllMessages>? = oldList
    override fun getOldListSize(): Int {
        return if (oldList != null) oldList!!.size else 0
    }

    override fun getNewListSize(): Int {
        return if (newList != null) newList!!.size else 0
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList?.get(oldItemPosition)?.getChatId() .equals(newList?.get(newItemPosition)?.getChatId())
    }


    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return newList!![newItemPosition]==(oldList!![oldItemPosition])
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }

}