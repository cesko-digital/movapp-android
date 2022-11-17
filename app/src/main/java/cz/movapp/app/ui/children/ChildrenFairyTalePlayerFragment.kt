package cz.movapp.app.ui.children

import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import cz.movapp.app.MainViewModel
import cz.movapp.app.R
import cz.movapp.app.data.FairyTale
import cz.movapp.app.data.Language
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.databinding.FragmentChildrenFairyTalePlayerBinding
import kotlin.math.floor

class ChildrenFairyTalePlayerFragment : Fragment() {

    private var _binding: FragmentChildrenFairyTalePlayerBinding? = null

    private val mainSharedViewModel: MainViewModel by activityViewModels()
    private val childrenFairyTalesViewModel: ChildrenFairyTalesViewModel by activityViewModels()

    private val binding get() = _binding!!

    private fun getFairyTalePosition(fairyTale: FairyTale, mediaPosition: Float, lang: Language): Int {
        var i = 0

        for (record in fairyTale.sections!!) {
            val recordLang = when (lang.langCode) {
                "cs" -> record.cs
                "uk" -> record.uk
                else -> {null}
            } ?: continue

            if (mediaPosition >= recordLang.start && mediaPosition <= recordLang.end) {
                return i
            }
            i++
        }

        return -1
    }

    class DelayedChecker(
        private val handler: Handler,
        private val delayTime: Long,
        private val cond: ()-> Boolean,
        private val checker: () -> Unit): Runnable {

        override fun run() {
            checker()
            if (cond()) {
                handler.postDelayed(this, delayTime)
            }
        }
    }

    private var langPair = LanguagePair.getDefault()
    private var handler: Handler? = null
    private var runnableCheck : DelayedChecker? = null
    private var player: MediaPlayer? = null
    private var playerPaused = false;

    enum class BilingualState {
        TO, FROM, TO_FROM
    }

    private var  bilingualState = BilingualState.TO_FROM


    private fun getFlagDrawable(langCode: String): Int {
        return when (langCode) {
            "uk" -> R.drawable.ua
            "cs" -> R.drawable.cz
            "uk_cs" -> R.drawable.ua_cz
            "cs_uk" -> R.drawable.cz_ua
            else -> R.drawable.ua
        }
    }

    private fun getBilingualFlag(langPair: LanguagePair, state: BilingualState): Int {
        return when(state) {
            BilingualState.TO -> getFlagDrawable(langPair.to.langCode)
            BilingualState.FROM -> getFlagDrawable(langPair.from.langCode)
            BilingualState.TO_FROM -> getFlagDrawable("${langPair.to.langCode}_${langPair.from.langCode}")
        }

    }

    private fun setRecyclerViewLayoutWeight(recyclerView: RecyclerView, showMe: Boolean) {
        if (showMe) {
            recyclerView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.GONE
        }
    }

    private fun setRecyclerViewSplitScreen(state: BilingualState, recyclerViewTo: RecyclerView, recyclerViewFrom: RecyclerView) {
        when(state) {
            BilingualState.TO -> {
                setRecyclerViewLayoutWeight(recyclerViewTo, true)
                setRecyclerViewLayoutWeight(recyclerViewFrom, false)
            }
            BilingualState.FROM -> {
                setRecyclerViewLayoutWeight(recyclerViewFrom, true)
                setRecyclerViewLayoutWeight(recyclerViewTo, false)
            }
            BilingualState.TO_FROM -> {
                setRecyclerViewLayoutWeight(recyclerViewTo, true)
                setRecyclerViewLayoutWeight(recyclerViewFrom, true)
            }
        }
    }

    private fun seekFairyTaleBilingual(
        player: MediaPlayer,
        seekInPlayer: Boolean,
        recyclerViewAdapterTo: ChildrenFairyTalePlayerAdapter,
        recyclerViewAdapterFrom: ChildrenFairyTalePlayerAdapter,
        position: Int,
        updateStart: Int,
        updateCount: Int,
        seekBar: SeekBar? = null
    ) {
        if (player.isPlaying) {

            if (seekInPlayer) {
                player.seekTo(position)
            }

            recyclerViewAdapterTo.notifyItemRangeChanged(
                updateStart,
                updateCount
            )

            recyclerViewAdapterFrom.notifyItemRangeChanged(
                updateStart,
                updateCount
            )

            if (seekBar != null) {
                seekBar.progress = position
            }
        }
    }

    private fun setFairyTaleTime(currentText: TextView, currentTime: Float, durationText: TextView, duration: Float) {
        currentText.text = "${"%02d".format(floor(currentTime / 60).toInt())}:${"%02d".format(floor(currentTime % 60).toInt())}"
        durationText.text = "${"%02d".format(floor(duration / 60).toInt())}:${"%02d".format(floor(duration % 60).toInt())}"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChildrenFairyTalePlayerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        handler = Looper.myLooper()?.let { Handler(it) }
        player = MediaPlayer()

        var slug = ""
        if (arguments != null) {
            slug = arguments!!.get("slug").toString()
        }

        val metaFairyTale = childrenFairyTalesViewModel.getMetaFairyTale(slug)
        val fairyTale = childrenFairyTalesViewModel.getFairyTale(slug)
        var emphasizedColumn = -1;
        val recyclerViewTo =  binding.recyclerViewChildrenFairyTaleColumnsTo
        val recyclerViewFrom =  binding.recyclerViewChildrenFairyTaleColumnsFrom

        childrenFairyTalesViewModel.fairyTales.observe(viewLifecycleOwner) {
            it.langPair = mainSharedViewModel.selectedLanguage.value!!

            val emphasizer = object: (Int) -> EmphasizerEvaluation () {
                override fun invoke(pos: Int): EmphasizerEvaluation {
                    val playerPos = player!!.currentPosition / 1000F

                    val fairyTalePosition = getFairyTalePosition(fairyTale, playerPos, langPair.to)

                    if (pos < fairyTalePosition) {
                        return EmphasizerEvaluation.LOWER
                    }

                    if (pos == fairyTalePosition) {
                        return EmphasizerEvaluation.EQUAL
                    }

                    /* this will not happen but IDE complains */
                    return EmphasizerEvaluation.GREATER
                }
            }

            recyclerViewTo.adapter = childrenFairyTalesViewModel.getFairyTaleAdapter(slug, true)
            recyclerViewTo.setHasFixedSize(true)

            (recyclerViewTo.adapter as ChildrenFairyTalePlayerAdapter).setEmphasizer(
                emphasizer
            )

            recyclerViewFrom.adapter = childrenFairyTalesViewModel.getFairyTaleAdapter(slug, false)
            recyclerViewFrom.setHasFixedSize(true)

            (recyclerViewFrom.adapter as ChildrenFairyTalePlayerAdapter).setEmphasizer(
                emphasizer
            )
        }

        mainSharedViewModel.selectedLanguage.observe(viewLifecycleOwner) {
            langPair = it

            binding.fairyTaleFlag.setImageResource(
                getBilingualFlag(it, bilingualState)
            )

            (recyclerViewTo.adapter as ChildrenFairyTalePlayerAdapter).langPair = it
            recyclerViewTo.adapter?.notifyDataSetChanged()

            (recyclerViewFrom.adapter as ChildrenFairyTalePlayerAdapter).langPair = it
            recyclerViewFrom.adapter?.notifyDataSetChanged()

            player!!.reset()

            val afd: AssetFileDescriptor = context!!.assets.openFd("stories/${slug}/${it.to.langCode}.mp3")
            player!!.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length);
            afd.close()
        }

        player!!.setOnPreparedListener {
            player!!.start()

            binding.fairyTalePlayerSeekbar.max = player!!.duration

            runnableCheck = DelayedChecker(
                handler!!,
                200,
                { player!!.isPlaying },
                {
                    val curPos = player!!.currentPosition
                    binding.fairyTalePlayerSeekbar.progress = curPos
                    val i = getFairyTalePosition(fairyTale, curPos/1000F, langPair.to)

                    if (i != emphasizedColumn) {
                        seekFairyTaleBilingual(
                            player!!,
                            false,
                            (recyclerViewTo.adapter as ChildrenFairyTalePlayerAdapter),
                            (recyclerViewFrom.adapter as ChildrenFairyTalePlayerAdapter),
                            i,
                            if (i == 0) 0 else i - 1,
                            2
                        )
                    }

                    if (emphasizedColumn < i ) {
                        recyclerViewTo.scrollToPosition(i + 1)
                        recyclerViewFrom.scrollToPosition(i + 1)
                    }

                    if (emphasizedColumn > i ) {
                        recyclerViewTo.scrollToPosition(if (i == 0) 0 else i - 1)
                        recyclerViewFrom.scrollToPosition(if (i == 0) 0 else i - 1)
                    }

                    emphasizedColumn = i

                    setFairyTaleTime(
                        binding.fairyTalePlayerCurrentTime,
                        player!!.currentPosition / 1000F,
                        binding.fairyTalePlayerDuration,
                        player!!.duration / 1000F,
                    )
                }
            )

            handler!!.postDelayed(runnableCheck!!, 200)
        }

        player!!.setOnCompletionListener {
            player!!.stop()
            binding.fairyTalePlayButton.setImageResource(R.drawable.player_play)
        }

        binding.apply {
            topPlayerBar.navigationIcon = AppCompatResources
                .getDrawable(requireContext(), R.drawable.ic_baseline_arrow_back_24)

            topPlayerBar.setNavigationOnClickListener {
                activity?.onBackPressed()
            }
            topPlayerBar.title = context!!.resources.getString(R.string.fairy_tales)

            fairyTaleImage.setImageDrawable(
                childrenFairyTalesViewModel.getFairyTaleDrawable(metaFairyTale.slug)
            )

            fairyTalePlayerNameFrom.text = when(mainSharedViewModel.selectedLanguage.value!!.to.langCode) {
                "cs" -> metaFairyTale.title.cs
                "uk" -> metaFairyTale.title.uk
                else -> {""}
            }

            fairyTalePlayerNameTo.text = when(mainSharedViewModel.selectedLanguage.value!!.from.langCode) {
                "cs" -> metaFairyTale.title.cs
                "uk" -> metaFairyTale.title.uk
                else -> {""}
            }

            setFairyTaleTime(
                fairyTalePlayerCurrentTime,
                0F,
                fairyTalePlayerDuration,
                0F,
            )

            fairyTalePlayerSeekbar.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    if (b) {
                        seekFairyTaleBilingual(
                            player!!,
                            true,
                            (recyclerViewTo.adapter as ChildrenFairyTalePlayerAdapter),
                            (recyclerViewFrom.adapter as ChildrenFairyTalePlayerAdapter),
                            i,
                            0,
                            fairyTale.sections!!.size
                        )
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }
            })

            fairyTaleBackButton.setOnClickListener {
                if (player!!.isPlaying) {
                    var newPosition = player!!.currentPosition - 10 * 1000
                    newPosition = if (newPosition < 0) 0 else newPosition

                    seekFairyTaleBilingual(
                        player!!,
                        true,
                        (recyclerViewTo.adapter as ChildrenFairyTalePlayerAdapter),
                        (recyclerViewFrom.adapter as ChildrenFairyTalePlayerAdapter),
                        newPosition,
                        0,
                        fairyTale.sections!!.size,
                        binding.fairyTalePlayerSeekbar
                    )
                }
            }

            fairyTaleForwardButton.setOnClickListener {
                if (player!!.isPlaying) {
                    var newPosition = player!!.currentPosition + 10 * 1000
                    newPosition = if (newPosition > player!!.duration) player!!.duration else newPosition

                    seekFairyTaleBilingual(
                        player!!,
                        true,
                        (recyclerViewTo.adapter as ChildrenFairyTalePlayerAdapter),
                        (recyclerViewFrom.adapter as ChildrenFairyTalePlayerAdapter),
                        newPosition,
                        0,
                        fairyTale.sections!!.size,
                        binding.fairyTalePlayerSeekbar
                    )
                }
            }

            fairyTalePlayButton.setOnClickListener{
                if (player!!.isPlaying) {
                    binding.fairyTalePlayButton.setImageResource(R.drawable.player_play)

                    player!!.pause()
                    playerPaused = true
                } else {
                    binding.fairyTalePlayButton.setImageResource(R.drawable.player_pause)

                    if (playerPaused) {
                        playerPaused = false
                        player!!.start()

                        seekFairyTaleBilingual(
                            player!!,
                            true,
                            (recyclerViewTo.adapter as ChildrenFairyTalePlayerAdapter),
                            (recyclerViewFrom.adapter as ChildrenFairyTalePlayerAdapter),
                            binding.fairyTalePlayerSeekbar.progress,
                            0,
                            fairyTale.sections!!.size
                        )

                        handler!!.postDelayed(runnableCheck!!, 200)
                    } else {
                        player!!.prepareAsync()
                    }
                }
            }

            fairyTaleFlag.setOnClickListener {
                bilingualState = when (bilingualState) {
                    BilingualState.TO -> BilingualState.FROM
                    BilingualState.FROM -> BilingualState.TO_FROM
                    BilingualState.TO_FROM -> BilingualState.TO
                }

                fairyTaleFlag.setImageResource(
                    getBilingualFlag(langPair, bilingualState)
                )

                setRecyclerViewSplitScreen(
                    bilingualState,
                    recyclerViewTo,
                    recyclerViewFrom
                )
            }
        }

        return root
    }

    override fun onPause() {
        super.onPause()
        if (player != null) {
            binding.fairyTalePlayButton.setImageResource(R.drawable.player_play)
            player!!.stop()
        }

        playerPaused = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (handler != null && runnableCheck != null) {
            handler!!.removeCallbacks(runnableCheck!!)
        }
        if (player != null) {
            if (player!!.isPlaying) {
                player!!.stop()
            }

            player!!.reset()
        }

        playerPaused = false
        player = null
        handler = null
        runnableCheck = null
        _binding = null
    }
}