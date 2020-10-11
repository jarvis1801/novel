package com.jarvis.novel.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.jarvis.novel.R
import com.jarvis.novel.core.App
import com.jarvis.novel.data.Chapter
import com.jarvis.novel.data.Paragraph
import com.jarvis.novel.data.Volume
import com.jarvis.novel.ui.base.BaseFragment
import com.jarvis.novel.ui.recyclerview.NovelTextContentProvider
import com.jarvis.novel.ui.recyclerview.NovelTitleContentProvider
import com.jarvis.novel.util.SharedPreferenceUtil
import com.jarvis.novel.viewModel.NovelContentViewModel
import com.jarvis.novel.viewModel.NovelVolumeIndexModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_novel_content.*
import java.util.*

class NovelContentFragment : BaseFragment() {
    private lateinit var model: NovelContentViewModel
    private val novelVolumeIndexModel: NovelVolumeIndexModel by activityViewModels()

    private val novelContentAdapter = MultiTypeAdapter()
    private val novelContentItems = ArrayList<Any>()
    private var viewManager: LinearLayoutManager? = null

    private var isShowBottomBar = true

    private var volumeDB: Volume? = null
    private var chapterDB: Chapter? = null

    private var scrolledY = 0

    private var mHandler: Handler? = null
    private var mTimer: Timer? = null
    private var mTimerTask: TimerTask? = null

    private var isScrollEndOnce = false

    private var measureWidth = 0

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
        mHandler = Handler(Looper.getMainLooper())
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
            getDataBase().novelVolumeDao().findOneById(volumeId).observeOnce(viewLifecycleOwner, {
                volumeDB = it
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
            })
        } else {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initLiveData() {
        model.chapterLiveData.observeWithoutOnResume(viewLifecycleOwner, {
            if (it != null) {
                scrolledY = it.positionY
                novelContentItems.clear()
                addTitle(it.sectionName, it.title)
                novelContentItems.addAll(it.paragraph)
                novelContentAdapter.notifyDataSetChanged()
                viewManager?.scrollToPosition(scrolledY)
            }
        })
    }

    private fun addTitle(sectionName: String?, title: String?) {
        val strBuilder = StringBuilder()
        sectionName?.let { sectionNameStr ->
            strBuilder.append(sectionNameStr)

            title?.let { titleStr ->
                if (titleStr != sectionName) {
                    strBuilder.append(" - ")
                    strBuilder.append(titleStr)
                }
            }
        }
        strBuilder.toString().let {
            if (it.isNotEmpty()) {
                novelContentItems.add(it)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        viewManager = LinearLayoutManager(requireActivity())

        novelContentAdapter.register(Paragraph::class.java, NovelTextContentProvider())
        novelContentAdapter.register(String::class.java, NovelTitleContentProvider())
        novelContentAdapter.items = novelContentItems

        recyclerview_novel_content.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = novelContentAdapter

            setOnTouchListener{ v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isShowBottomBar = true
                        mTimer?.cancel()
                    }

                    MotionEvent.ACTION_UP -> {
                        if (isShowBottomBar) {
                            App.instance.setTranslation(view = bottom_bar, value = 0f, duration = 500)
                        }
                        createTimeTask()
                    }

                    MotionEvent.ACTION_MOVE -> {
                        isShowBottomBar = false
                    }
                }
                v.performClick()
            }

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE && !isScrollEndOnce) {
                        Toast.makeText(requireContext(), "章節已完結", Toast.LENGTH_SHORT).show()

                        isScrollEndOnce = true

                        chapterDB?.isRead = true
                        chapterDB?._id.let {
                            novelVolumeIndexModel.mUpdateEndChapter.postValue(it)
                        }
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
        }

        btn_increase_text_size.setOnClickListener {
            val fontScale = SharedPreferenceUtil.getFontScale() + 0.05f
            SharedPreferenceUtil.setFontScale(fontScale)
            novelContentAdapter.notifyDataSetChanged()
            createTimeTask()
        }

        btn_decrease_text_size.setOnClickListener {
            val fontScale = SharedPreferenceUtil.getFontScale() - 0.05f
            SharedPreferenceUtil.setFontScale(fontScale)
            novelContentAdapter.notifyDataSetChanged()
            createTimeTask()
        }
    }

    override fun onPause() {
        super.onPause()

        val chapterIndex: Int? = volumeDB?.chapterList?.indexOf(chapterDB)

        val chapterList = mutableListOf<Chapter>()
        chapterList.addAll(volumeDB?.chapterList!!)

        chapterDB?.positionY = viewManager?.findFirstVisibleItemPosition()!!

        chapterList[chapterIndex!!] = chapterDB!!
        volumeDB?.chapterList = chapterList

        insertOneReplace(volumeDB!!)
    }

    private fun insertOneReplace(volumeDB: Volume) {
        val observable = Observable.fromCallable {
            getDataBase().novelVolumeDao().insertOneReplace(volumeDB)
        }.subscribeOn(Schedulers.io())
            .subscribe({}, {})

        compositeDisposable.add(observable)
    }

    override fun onDestroyView() {
        mTimer!!.cancel()
        mTimer!!.purge()
        super.onDestroyView()
    }
}