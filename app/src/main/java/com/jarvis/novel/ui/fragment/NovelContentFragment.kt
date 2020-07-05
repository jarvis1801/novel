package com.jarvis.novel.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.data.Chapter
import com.jarvis.novel.data.Volume
import com.jarvis.novel.ui.base.BaseFragment
import com.jarvis.novel.ui.recyclerview.NovelAdapter
import com.jarvis.novel.ui.recyclerview.NovelTextContentAdapter
import com.jarvis.novel.util.SharedPreferenceUtil
import com.jarvis.novel.viewModel.NovelContentViewModel
import kotlinx.android.synthetic.main.fragment_novel_content.*
import java.util.*

class NovelContentFragment : BaseFragment() {
    private lateinit var model: NovelContentViewModel

    private var novelContentAdapter: NovelTextContentAdapter? = null
    private var viewManager: LinearLayoutManager? = null

    private var isShowBottomBar = true

    private var volumeDB: Volume? = null
    private var chapterDB: Chapter? = null

    private var scrolledY = 0

    private var mHandler: Handler? = null
    private var mTimer: Timer? = null
    private var mTimerTask: TimerTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model = requireActivity().let { ViewModelProvider(this).get(NovelContentViewModel::class.java) }

        createTimeTask()
    }

    private fun createTimeTask() {
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer!!.purge()
        }
        mHandler = Handler()
        mTimer = Timer()
        mTimerTask = object : TimerTask() {
            override fun run() {
                mHandler!!.post {
                    App.instance.setTranslation(view = bottom_bar, value = App.instance.dpToPixel(35f).toFloat(), duration = 500)
                }
            }
        }
        mTimer?.schedule(mTimerTask, 3000L)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_novel_content, container, false)

        init()

        return root
    }

    private fun init() {
        initLiveData()
        getArgs()
    }

    private fun getArgs() {
        val chapterId = requireArguments().getString("chapterId", null)
        val volumeId = requireArguments().getString("volumeId", null)
        if (volumeId != null && chapterId != null) {
            volumeDB = getDataBase().volumeDao().findOneById(volumeId)

            if (volumeDB != null) {
                volumeDB!!.chapterList.forEach {
                    if (it._id == chapterId) {
                        chapterDB = it
                    }
                }

                if (chapterDB != null) {
                    model.chapterLiveData.postValue(chapterDB)
                }
            }
        } else {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initLiveData() {
        model.chapterLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                scrolledY = it.positionY
                novelContentAdapter?.updateList(it.paragraph)
                viewManager?.scrollToPosition(scrolledY)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        viewManager = LinearLayoutManager(requireActivity())
        novelContentAdapter = NovelTextContentAdapter(requireContext())
        recyclerview_novel_content.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = MergeAdapter(novelContentAdapter)

            setOnTouchListener{ v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isShowBottomBar = true
                        mTimer?.cancel()
                    }

                    MotionEvent.ACTION_UP -> {
                        if (isShowBottomBar) {
                            App.instance.setTranslation(view = bottom_bar, value = 0f, duration = 500)
                            createTimeTask()
                        }
                    }

                    MotionEvent.ACTION_MOVE -> {
                        isShowBottomBar = false
                    }
                }
                v.performClick()
            }
        }

        btn_increase_text_size.setOnClickListener {
            val fontScale = SharedPreferenceUtil.getFontScale() + 0.05f
            SharedPreferenceUtil.setFontScale(fontScale)
            novelContentAdapter?.notifyDataSetChanged()
            createTimeTask()
        }

        btn_decrease_text_size.setOnClickListener {
            val fontScale = SharedPreferenceUtil.getFontScale() - 0.05f
            SharedPreferenceUtil.setFontScale(fontScale)
            novelContentAdapter?.notifyDataSetChanged()
            createTimeTask()
        }
    }

    override fun onPause() {
        super.onPause()

        var chapterIndex: Int? = null
        chapterIndex = volumeDB?.chapterList?.indexOf(chapterDB)

        val chapterList = mutableListOf<Chapter>()
        chapterList.addAll(volumeDB?.chapterList!!)

        chapterDB?.positionY = viewManager?.findFirstVisibleItemPosition()!!

        chapterList[chapterIndex!!] = chapterDB!!
        volumeDB?.chapterList = chapterList

        getDataBase().volumeDao().insertOneReplace(volumeDB!!)
    }

    override fun onDestroy() {
        super.onDestroy()

        mTimer!!.cancel()
        mTimer!!.purge()
    }
}