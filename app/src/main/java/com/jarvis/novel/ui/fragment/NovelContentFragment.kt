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
import com.jarvis.novel.ui.base.BaseFragment
import com.jarvis.novel.ui.recyclerview.NovelAdapter
import com.jarvis.novel.ui.recyclerview.NovelTextContentAdapter
import com.jarvis.novel.util.SharedPreferenceUtil
import com.jarvis.novel.viewModel.NovelContentViewModel
import kotlinx.android.synthetic.main.fragment_novel_content.*

class NovelContentFragment : BaseFragment() {
    private lateinit var model: NovelContentViewModel

    private var novelContentAdapter: NovelTextContentAdapter? = null
    private var viewManager: RecyclerView.LayoutManager? = null

    private var isShowBottomBar = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model = requireActivity().let { ViewModelProvider(this).get(NovelContentViewModel::class.java) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_novel_content, container, false)

        init()

        return root
    }

    private fun init() {
        initLiveData()
        getArgs()
//        mHandler = Handler()
//        mRunnable = Runnable {
//            Log.d("chris", bottomBarVisibilityTimer.toString())
//            if (enableTimer) {
//                bottomBarVisibilityTimer++
//            }
//            if (bottomBarVisibilityTimer > 2) {
//                App.instance.setTranslation(view = bottom_bar, value = App.instance.dpToPixel(35f).toFloat(), duration = 500)
//            }
//            mHandler.postDelayed(mRunnable, 1000)
//        }
//        mHandler.postDelayed(mRunnable, 1000)
    }

    private fun getArgs() {
        val chapterId = requireArguments().getString("chapterId", null)
        val volumeId = requireArguments().getString("volumeId", null)
        if (volumeId != null && chapterId != null) {
            val volumeDB = getDataBase().volumeDao().findById(volumeId)

            if (volumeDB != null) {
                var chapter: Chapter? = null
                volumeDB.forEach {it ->
                    it.chapterList.forEach {
                        if (it._id == chapterId) {
                            chapter = it
                        }
                    }
                }

                if (chapter != null) {
                    model.chapterLiveData.postValue(chapter)
                }
            }
        } else {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initLiveData() {
        model.chapterLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                novelContentAdapter?.updateList(it.paragraph)
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
        }

        recyclerview_novel_content.setOnTouchListener{ v, event ->
//            enableTimer = false
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isShowBottomBar = true
//                    enableTimer = true
//                    bottomBarVisibilityTimer = 0
                }

                MotionEvent.ACTION_UP -> {
                    if (isShowBottomBar) {
                        App.instance.setTranslation(view = bottom_bar, value = 0f, duration = 500)
//                        mHandler.removeCallbacks(mRunnable)
//                        mHandler.postDelayed(mRunnable, 2000)
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    isShowBottomBar = false
                }
            }
            v.performClick()
        }

        btn_increase_text_size.setOnClickListener {
            val fontScale = SharedPreferenceUtil.getFontScale() + 0.05f
            SharedPreferenceUtil.setFontScale(fontScale)
            novelContentAdapter?.notifyDataSetChanged()

//            mHandler.removeCallbacksAndMessages(null)
//            mHandler.postDelayed({
//                App.instance.setTranslation(view = bottom_bar, value = App.instance.dpToPixel(35f).toFloat(), duration = 500)
//            }, 2000)
        }

        btn_decrease_text_size.setOnClickListener {
            val fontScale = SharedPreferenceUtil.getFontScale() - 0.05f
            SharedPreferenceUtil.setFontScale(fontScale)
            novelContentAdapter?.notifyDataSetChanged()

//            mHandler.removeCallbacks(mRunnable)
//            mHandler.postDelayed(mRunnable, 2000)
        }

        bottom_bar.setOnTouchListener { v, event ->
//            enableTimer = false
            v.performClick()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

//        mHandler.removeCallbacksAndMessages(mRunnable)
    }
}