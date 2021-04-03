package com.wafflestudio.snutt2.adapter

import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.common.base.Strings
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTUtils
import com.wafflestudio.snutt2.manager.LectureManager.Companion.instance
import com.wafflestudio.snutt2.model.Lecture
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response

/**
 * Created by makesource on 2017. 3. 7..
 */
class LectureListAdapter(private val lectures: List<Lecture>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    enum class VIEW_TYPE(val value: Int) {
        Lecture(0), ProgressBar(1);

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE.Lecture.value) {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.cell_lecture, parent, false)
            // create ViewHolder
            LectureViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.cell_lecture_progressbar, parent, false)
            ProgressBarViewHolder(view)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val itemType = getItemViewType(position)
        if (itemType == VIEW_TYPE.ProgressBar.value) return
        val lecture = lectures[position]
        val holder = viewHolder as LectureViewHolder
        val selectedPosition = instance!!.selectedPosition
        holder.bindData(lecture)
        if (selectedPosition == position) {
            holder.layout.setBackgroundColor(Color.parseColor("#33000000"))
            if (instance!!.alreadyOwned(lecture)) {
                holder.add.visibility = View.GONE
                holder.margin.visibility = View.GONE
                holder.remove.visibility = View.VISIBLE
            } else {
                holder.remove.visibility = View.GONE
                holder.margin.visibility = View.GONE
                holder.add.visibility = View.VISIBLE
            }
        } else {
            holder.add.visibility = View.GONE
            holder.remove.visibility = View.GONE
            holder.margin.visibility = View.VISIBLE
            holder.layout.setBackgroundColor(Color.TRANSPARENT)
        }

        // 3개의 서로 다른 클릭을 구분하기 위해 onBindViewHolder 에서 view Id 를 비교함.
        setOnItemClickListener(object : ClickListener {
            override fun onClick(v: View, position: Int) {
                if (position >= itemCount) return
                if (v.id == holder.layout.id) {
                    Log.d(TAG, "View ID : " + v.id)
                    Log.d(TAG, "$position item Clicked!!")
                    if (selectedPosition == position) {
                        notifyItemChanged(position)
                        instance!!.setSelectedLecture(null)
                    } else {
                        notifyItemChanged(selectedPosition)
                        notifyItemChanged(position)
                        instance!!.setSelectedLecture(getItem(position))
                    }
                } else if (v.id == holder.add.id) {
                    Log.d(TAG, "View ID : " + v.id)
                    Log.d(TAG, "$position add Clicked!!")
                    instance!!.addLecture(getItem(position), object : Callback<Any> {
                        override fun success(o: Any?, response: Response) {
                            notifyItemChanged(position)
                        }

                        override fun failure(error: RetrofitError) {}
                    })
                } else {
                    Log.d(TAG, "View ID : " + v.id)
                    Log.d(TAG, "$position remove Clicked!!")
                    instance!!.removeLecture(getItem(position), object : Callback<Any> {
                        override fun success(o: Any?, response: Response) {
                            notifyItemChanged(position)
                        }

                        override fun failure(error: RetrofitError) {}
                    })
                }
            }
        })
    }

    override fun getItemViewType(position: Int): Int {
        val item = lectures[position]
        return if (item == null) VIEW_TYPE.ProgressBar.value else VIEW_TYPE.Lecture.value
    }

    override fun getItemCount(): Int {
        return lectures.size
    }

    fun getItem(position: Int): Lecture {
        return lectures[position]
    }

    class LectureViewHolder constructor(var layout: View) : RecyclerView.ViewHolder(layout), View.OnClickListener {
        protected var title: TextView
        protected var subTitle: TextView
        protected var tag: TextView
        protected var classTime: TextView
        protected var location: TextView
        var add: Button
        var remove: Button
        var margin: View
        fun bindData(lecture: Lecture) {
            val selectedPosition = instance!!.selectedPosition
            val titleText = lecture.course_title
            val subTitleText = "(" + lecture.instructor + " / " + java.lang.String.valueOf(lecture.credit) + "학점)"
            title.text = titleText
            title.textScaleX = 1.0f
            subTitle.text = subTitleText
            val selected = position == selectedPosition
            val margin = 20 + 20 + 10 + if (selected) 80 else 0
            val maxWidth = (SNUTTUtils.displayWidth - SNUTTUtils.dpTopx(margin.toFloat())).toInt()
            var subTitleWidth = Math.min(getTextViewWidth(subTitle), (maxWidth / 2).toFloat()).toInt()
            val titleWidth = Math.min(getTextViewWidth(title), (maxWidth - subTitleWidth).toFloat()).toInt()
            if (titleWidth + subTitleWidth < maxWidth) {
                subTitleWidth = maxWidth - titleWidth
            }
            title.layoutParams = LinearLayout.LayoutParams(titleWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            title.isSelected = selected
            title.ellipsize = if (selected) TextUtils.TruncateAt.MARQUEE else TextUtils.TruncateAt.END
            title.textScaleX = 1.0f
            subTitle.layoutParams = LinearLayout.LayoutParams(subTitleWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            var tagText: String? = ""
            if (!Strings.isNullOrEmpty(lecture.category)) {
                tagText += lecture.category + ", "
            }
            if (!Strings.isNullOrEmpty(lecture.department)) {
                tagText += lecture.department + ", "
            }
            tagText += lecture.academic_year
            if (Strings.isNullOrEmpty(tagText)) tagText = "(없음)"
            if (selected) {
                if (!Strings.isNullOrEmpty(lecture.remark)) {
                    tag.text = lecture.remark
                } else {
                    tag.text = tagText
                }
                tag.isSelected = true
                tag.ellipsize = TextUtils.TruncateAt.MARQUEE
            } else {
                tag.text = tagText
                tag.isSelected = false
                tag.ellipsize = TextUtils.TruncateAt.END
            }
            var classTimeText = lecture.simplifiedClassTime
            if (Strings.isNullOrEmpty(classTimeText)) classTimeText = "(없음)"
            classTime.text = classTimeText
            var locationText = lecture.simplifiedLocation
            if (Strings.isNullOrEmpty(locationText)) locationText = "(없음)"
            location.text = locationText
        }

        private fun getTextViewWidth(textView: TextView): Float {
            textView.measure(0, 0)
            return textView.measuredWidth.toFloat()
        }

        override fun onClick(v: View) {
            if (clickListener != null) {
                clickListener!!.onClick(v, position)
            }
        }

        init {
            title = itemView.findViewById<View>(R.id.title) as TextView
            subTitle = itemView.findViewById<View>(R.id.sub_title) as TextView
            tag = itemView.findViewById<View>(R.id.tag) as TextView
            classTime = itemView.findViewById<View>(R.id.time) as TextView
            location = itemView.findViewById<View>(R.id.location) as TextView
            add = itemView.findViewById<View>(R.id.add) as Button
            remove = itemView.findViewById<View>(R.id.remove) as Button
            margin = itemView.findViewById(R.id.margin)
            layout.setOnClickListener(this)
            add.setOnClickListener(this)
            remove.setOnClickListener(this)
        }
    }

    class ProgressBarViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        private val progressBar: ProgressBar

        init {
            progressBar = view.findViewById<View>(R.id.progressBar) as ProgressBar
        }
    }

    private interface ClickListener {
        fun onClick(v: View, position: Int)
    }

    private fun setOnItemClickListener(_clickListener: ClickListener) {
        clickListener = _clickListener
    }

    companion object {
        private const val TAG = "LECTURE_LIST_ADAPTER"
        private var clickListener: ClickListener? = null
    }
}