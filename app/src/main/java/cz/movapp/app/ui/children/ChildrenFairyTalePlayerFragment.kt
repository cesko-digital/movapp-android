package cz.movapp.app.ui.children

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import cz.movapp.app.MainViewModel
import cz.movapp.app.MediaPlayerForegroundService
import cz.movapp.app.R
import cz.movapp.app.data.FairyTale
import cz.movapp.app.data.Language
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.databinding.FragmentChildrenFairyTalePlayerBinding
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.floor

class ChildrenFairyTalePlayerFragment : Fragment() {

    private var _binding: FragmentChildrenFairyTalePlayerBinding? = null

    private val mainSharedViewModel: MainViewModel by activityViewModels()
    private val childrenFairyTalesViewModel: ChildrenFairyTalesViewModel by activityViewModels()

    private val binding get() = _binding!!

    private var broadcastMediaStateReceiver : BroadcastReceiver? = null
    private var broadcastMediaTimeReceiver : BroadcastReceiver? = null

    private var langPair = LanguagePair.getDefault()

    enum class BilingualState {
        TO, FROM, TO_FROM
    }

    var toName : String = ""
    var fromName : String = ""

    private var  bilingualState = BilingualState.TO_FROM

    private var currentTime = AtomicInteger(0)
    private var durationTime = AtomicInteger(0)

    private var playerPaused = true;

    private fun startMediaPlayerService(fileName: String){
        Intent(context, MediaPlayerForegroundService::class.java).also {
            it.putExtra("fileName", fileName)
            it.putExtra("toName", toName)
            it.putExtra("fromName", fromName)
        }.also {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context!!.startForegroundService(it)
            } else {
                context!!.startService(it)
            }
        }
    }

    private fun stopMediaPlayerService() {
        sendMediaPlayerState("stopService")
    }

    private fun sendMediaPlayerSeekTo(seekTo : Int) {
        val intent = Intent("MediaPlayerSeekTo")
        intent.putExtra("seekTo", seekTo)
        LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
    }

    private fun sendMediaPlayerState(state: String) {
        val intent = Intent("MediaPlayerState")
        intent.putExtra("state", state)
        LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
    }

    private fun seekFairyTaleBilingual(
        seekInPlayer: Boolean,
        recyclerViewAdapterTo: ChildrenFairyTalePlayerAdapter,
        recyclerViewAdapterFrom: ChildrenFairyTalePlayerAdapter,
        position: Int,
        updateStart: Int,
        updateCount: Int,
        seekBar: SeekBar? = null
    ) {
        if (!playerPaused) {

            if (seekInPlayer) {
                sendMediaPlayerSeekTo(position)
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
        } else {
            if (seekInPlayer) {
                sendMediaPlayerSeekTo(position)
            }
        }
    }

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

        var slug = ""
        if (arguments != null) {
            slug = arguments!!.get("slug").toString()
        }

        val metaFairyTale = childrenFairyTalesViewModel.getMetaFairyTale(slug)
        val fairyTale = childrenFairyTalesViewModel.getFairyTale(slug)
        var emphasizedColumn = -1;
        val recyclerViewTo =  binding.recyclerViewChildrenFairyTaleColumnsTo
        val recyclerViewFrom =  binding.recyclerViewChildrenFairyTaleColumnsFrom

        broadcastMediaStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intend: Intent?) {
                if (_binding != null) {
                    when (intend?.getStringExtra("state")) {
                        "start" -> binding.fairyTalePlayButton.setImageResource(R.drawable.player_pause)
                        "pause" -> binding.fairyTalePlayButton.setImageResource(R.drawable.player_play)
                        "stop" -> {
                            binding.fairyTalePlayButton.setImageResource(R.drawable.player_play)
                            playerPaused = true
                        }
                    }
                }
            }
        }

        broadcastMediaTimeReceiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intend: Intent?) {
                if (intend !== null && _binding != null) {
                    currentTime.set(intend.getIntExtra("current", 0))
                    durationTime.set(intend.getIntExtra("duration", 0))

                    val ct = currentTime.get()
                    val dur = durationTime.get()

                    binding.fairyTalePlayerSeekbar.max = dur
                    binding.fairyTalePlayerSeekbar.progress = ct

                    val i = getFairyTalePosition(fairyTale, ct/1000F, langPair.to)

                    if (i != emphasizedColumn) {
                        seekFairyTaleBilingual(
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
                        ct / 1000F,
                        binding.fairyTalePlayerDuration,
                        dur / 1000F,
                    )
                }
            }
        }

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            broadcastMediaStateReceiver!!,
            IntentFilter("MediaPlayerState")
        )

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            broadcastMediaTimeReceiver!!,
            IntentFilter("MediaPlayerTime")
        )

        childrenFairyTalesViewModel.fairyTales.observe(viewLifecycleOwner) {
            it.langPair = mainSharedViewModel.selectedLanguage.value!!

            val emphasizer = object: (Int) -> EmphasizerEvaluation () {
                override fun invoke(pos: Int): EmphasizerEvaluation {
                    //val playerPos = player!!.currentPosition / 1000F
                    val playerPos = currentTime.get() / 1000F

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

            when(langPair.to.langCode) {
                "cs" -> { toName = metaFairyTale.title.cs!!; fromName = metaFairyTale.title.uk!! }
                "uk" -> { toName = metaFairyTale.title.uk!!; fromName = metaFairyTale.title.cs!! }
            }

            stopMediaPlayerService()
            startMediaPlayerService("stories/${slug}/${langPair.to.langCode}.mp3")
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
                var newPosition = currentTime.get() - 10 * 1000
                newPosition = if (newPosition < 0) 0 else newPosition

                seekFairyTaleBilingual(
                    true,
                    (recyclerViewTo.adapter as ChildrenFairyTalePlayerAdapter),
                    (recyclerViewFrom.adapter as ChildrenFairyTalePlayerAdapter),
                    newPosition,
                    0,
                    fairyTale.sections!!.size,
                    binding.fairyTalePlayerSeekbar
                )
            }

            fairyTaleForwardButton.setOnClickListener {
                var newPosition = currentTime.get() + 10 * 1000
                val duration = durationTime.get()
                newPosition = if (newPosition > duration) duration else newPosition

                seekFairyTaleBilingual(
                    true,
                    (recyclerViewTo.adapter as ChildrenFairyTalePlayerAdapter),
                    (recyclerViewFrom.adapter as ChildrenFairyTalePlayerAdapter),
                    newPosition,
                    0,
                    fairyTale.sections!!.size,
                    binding.fairyTalePlayerSeekbar
                )
            }

            fairyTalePlayButton.setOnClickListener{

                playerPaused = if (playerPaused) {
                    sendMediaPlayerState("start")

                    seekFairyTaleBilingual(
                        true,
                        (recyclerViewTo.adapter as ChildrenFairyTalePlayerAdapter),
                        (recyclerViewFrom.adapter as ChildrenFairyTalePlayerAdapter),
                        binding.fairyTalePlayerSeekbar.progress,
                        0,
                        fairyTale.sections!!.size
                    )

                    false
                } else {
                    sendMediaPlayerState("pause")
                    true
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

        sendMediaPlayerState("stop")
        binding.fairyTalePlayButton.setImageResource(R.drawable.player_play)
        playerPaused = true
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (broadcastMediaStateReceiver != null) {
            LocalBroadcastManager.getInstance(context!!)
                .unregisterReceiver(broadcastMediaStateReceiver!!)
            broadcastMediaStateReceiver = null
        }

        if (broadcastMediaTimeReceiver != null) {
            LocalBroadcastManager.getInstance(context!!)
                .unregisterReceiver(broadcastMediaTimeReceiver!!)
            broadcastMediaTimeReceiver = null
        }

        stopMediaPlayerService()

        playerPaused = false
        _binding = null
    }
}