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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import cz.movapp.app.MainViewModel
import cz.movapp.app.MediaPlayerForegroundService
import cz.movapp.app.R
import cz.movapp.app.data.FairyTale
import cz.movapp.app.data.Language
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.data.MetaFairyTale
import cz.movapp.app.databinding.FragmentChildrenFairyTalePlayerBinding
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
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

    enum class PlayerState {
        PAUSED, PLAYING, STOPPED
    }

    var toName : String = ""
    var fromName : String = ""

    private var  bilingualState = AtomicReference(BilingualState.TO_FROM)

    private var currentTime = AtomicInteger(0)
    private var durationTime = AtomicInteger(0)

    private var playerState = PlayerState.PAUSED

    private fun startMediaPlayerService(slug: String, fileName: String){
        Intent(context, MediaPlayerForegroundService::class.java).also {
            it.putExtra("slug", slug)
            it.putExtra("fileName", fileName)
            it.putExtra("toName", toName)
            it.putExtra("fromName", fromName)
        }.also {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireContext().startForegroundService(it)
            } else {
                requireContext().startService(it)
            }
        }
    }

    private fun stopMediaPlayerService() {
        sendMediaPlayerState("stopService")
    }

    private fun sendMediaPlayerSeekTo(seekTo : Int) {
        val intent = Intent("MediaPlayerSeekTo")
        intent.putExtra("seekTo", seekTo)
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    private fun sendMediaPlayerState(state: String) {
        val intent = Intent("MediaPlayerState")
        intent.putExtra("state", state)
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    private fun sendMediaPlayerOnlyChangeLang(fileName: String, toNameChange: String, fromNameChange: String, seekTo: Int) {
        val intent = Intent("MediaPlayerOnlyLang")
        intent.putExtra("fileName", fileName)
        intent.putExtra("toName", toNameChange)
        intent.putExtra("fromName", fromNameChange)
        intent.putExtra("seekTo", seekTo)
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
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
        when (playerState) {
            PlayerState.PLAYING -> {
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
            }

            PlayerState.PAUSED -> {
                if (seekInPlayer) {
                    sendMediaPlayerSeekTo(position)
                }
            }

            PlayerState.STOPPED -> {}
        }
    }

    private fun getFairyTalePosition(fairyTale: FairyTale, mediaPosition: Float, lang: Language): Int {
        var i = 0

        for (record in fairyTale.sections!!) {

            val recordLang = record.getValue(lang.langCode)  ?: continue

            if (mediaPosition >= recordLang.start && mediaPosition <= recordLang.end) {
                return i
            }
            i++
        }

        return -1
    }

    private fun getFairyTaleTimestamp(fairyTale: FairyTale, column: Int, lang: Language): Float {
        if (column < 0) {
            return 0F
        }

        return fairyTale.sections!![column].getValue(lang.langCode)?.start ?: 0F
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

    private fun setFairyTaleLanguage(metaFairyTale: MetaFairyTale) {
        langPair = mainSharedViewModel.selectedLanguage.value!!

        binding.fairyTaleFlag.setImageResource(
            getBilingualFlag(langPair, bilingualState.get())
        )

        when (bilingualState.get()) {
            BilingualState.TO_FROM, BilingualState.TO, null -> {
                toName = metaFairyTale.title.getValue(langPair.to.langCode) ?: ""
                fromName = metaFairyTale.title.getValue(langPair.from.langCode) ?: ""
            }

            BilingualState.FROM -> {
                toName = metaFairyTale.title.getValue(langPair.from.langCode) ?: ""
                fromName = metaFairyTale.title.getValue(langPair.to.langCode) ?: ""
            }
        }
    }

    private fun getMediaLang() : Language {
        return when (bilingualState.get()) {
            BilingualState.TO -> langPair.to
            BilingualState.FROM -> langPair.from
            BilingualState.TO_FROM -> langPair.to
            null -> langPair.to
        }
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
            slug = requireArguments().get("slug").toString()
        }

        val metaFairyTale = childrenFairyTalesViewModel.getMetaFairyTale(slug)
        val fairyTale = childrenFairyTalesViewModel.getFairyTale(slug)
        var emphasizedColumn = -1;
        val recyclerViewTo =  binding.recyclerViewChildrenFairyTaleColumnsTo
        val recyclerViewFrom =  binding.recyclerViewChildrenFairyTaleColumnsFrom

        setFairyTaleLanguage(metaFairyTale)

        broadcastMediaStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                if (_binding != null) {
                    when (intent?.getStringExtra("state")) {
                        "start" -> binding.fairyTalePlayButton.setImageResource(R.drawable.player_pause)
                        "pause" -> binding.fairyTalePlayButton.setImageResource(R.drawable.player_play)
                        "stop" -> {
                            playerState = PlayerState.STOPPED
                            binding.fairyTalePlayButton.setImageResource(R.drawable.player_play)
                        }
                    }
                }
            }
        }

        broadcastMediaTimeReceiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                if (playerState == PlayerState.STOPPED) {
                    return
                }

                if (intent !== null && _binding != null) {

                    playerState = if (intent.getBooleanExtra("isPlaying", false)) {
                        PlayerState.PLAYING
                    } else {
                        PlayerState.PAUSED
                    }

                    currentTime.set(intent.getIntExtra("current", 0))
                    durationTime.set(intent.getIntExtra("duration", 0))

                    val ct = currentTime.get()
                    val dur = durationTime.get()

                    binding.fairyTalePlayerSeekbar.max = dur
                    binding.fairyTalePlayerSeekbar.progress = ct

                    val i = getFairyTalePosition(fairyTale, ct/1000F, getMediaLang())

                    if (emphasizedColumn < i ) {
                        seekFairyTaleBilingual(
                            false,
                            (recyclerViewTo.adapter as ChildrenFairyTalePlayerAdapter),
                            (recyclerViewFrom.adapter as ChildrenFairyTalePlayerAdapter),
                            i,
                            0,
                            i + 1
                        )

                        recyclerViewTo.scrollToPosition(i + 1)
                        recyclerViewFrom.scrollToPosition(i + 1)
                    }

                    if (emphasizedColumn > i ) {
                        seekFairyTaleBilingual(
                            false,
                            (recyclerViewTo.adapter as ChildrenFairyTalePlayerAdapter),
                            (recyclerViewFrom.adapter as ChildrenFairyTalePlayerAdapter),
                            i,
                            i,
                            fairyTale.sections!!.size - i
                        )

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

            val columnCallback = object: (Int) -> Unit {
                override fun invoke(column: Int) {
                    val seekTo = (getFairyTaleTimestamp(fairyTale, column, getMediaLang()) * 1000).toInt()
                    sendMediaPlayerOnlyChangeLang(
                        "stories/${slug}/${getMediaLang().langCode}.mp3",
                        toName,
                        fromName,
                        seekTo
                    )
                    binding.fairyTalePlayerSeekbar.progress = seekTo
                }
            }

            val emphasizer = object: (Int) -> EmphasizerEvaluation () {
                override fun invoke(pos: Int): EmphasizerEvaluation {
                    val playerPos = currentTime.get() / 1000F

                    val fairyTalePosition = getFairyTalePosition(fairyTale, playerPos, getMediaLang())

                    if (pos < fairyTalePosition) {
                        return EmphasizerEvaluation.LOWER
                    }

                    if (pos == fairyTalePosition) {
                        return EmphasizerEvaluation.EQUAL
                    }

                    return EmphasizerEvaluation.GREATER
                }
            }

            recyclerViewTo.adapter = childrenFairyTalesViewModel.getFairyTaleAdapter(slug, true)
            recyclerViewTo.setHasFixedSize(true)

            (recyclerViewTo.adapter as ChildrenFairyTalePlayerAdapter).apply {
                setEmphasizer(emphasizer)
                setColumnCallback(columnCallback)
            }

            recyclerViewFrom.adapter = childrenFairyTalesViewModel.getFairyTaleAdapter(slug, false)
            recyclerViewFrom.setHasFixedSize(true)

            (recyclerViewFrom.adapter as ChildrenFairyTalePlayerAdapter).apply {
                setEmphasizer(emphasizer)
                setColumnCallback(columnCallback)
            }
        }

        mainSharedViewModel.selectedLanguage.observe(viewLifecycleOwner) {

            setFairyTaleLanguage(metaFairyTale)

            (recyclerViewTo.adapter as ChildrenFairyTalePlayerAdapter).langPair = it
            recyclerViewTo.adapter?.notifyDataSetChanged()

            (recyclerViewFrom.adapter as ChildrenFairyTalePlayerAdapter).langPair = it
            recyclerViewFrom.adapter?.notifyDataSetChanged()

            startMediaPlayerService(
                slug,
                "stories/${slug}/${getMediaLang().langCode}.mp3"
            )
        }

        binding.apply {
            topPlayerBar.navigationIcon = AppCompatResources
                .getDrawable(requireContext(), R.drawable.ic_baseline_arrow_back_24)

            topPlayerBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            topPlayerBar.title = requireContext().resources.getString(R.string.fairy_tales)

            fairyTaleImage.setImageDrawable(
                childrenFairyTalesViewModel.getFairyTaleDrawable(metaFairyTale.slug)
            )

            fairyTalePlayerNameFrom.text = metaFairyTale.title.getValue(
                mainSharedViewModel.selectedLanguage.value!!.to.langCode
            )

            fairyTalePlayerNameTo.text = metaFairyTale.title.getValue(
                mainSharedViewModel.selectedLanguage.value!!.from.langCode
            )

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

                playerState = when(playerState) {
                    PlayerState.PLAYING -> {
                        sendMediaPlayerState("pause")
                        PlayerState.PAUSED
                    }

                    PlayerState.PAUSED -> {
                        sendMediaPlayerState("start")

                        seekFairyTaleBilingual(
                            true,
                            (recyclerViewTo.adapter as ChildrenFairyTalePlayerAdapter),
                            (recyclerViewFrom.adapter as ChildrenFairyTalePlayerAdapter),
                            binding.fairyTalePlayerSeekbar.progress,
                            0,
                            fairyTale.sections!!.size
                        )

                        PlayerState.PLAYING
                    }

                    PlayerState.STOPPED -> {
                        sendMediaPlayerState("start")
                        PlayerState.PLAYING
                    }
                }

            }

            fairyTaleFlag.setOnClickListener {
                bilingualState.set(when (bilingualState.get()) {
                    BilingualState.TO -> BilingualState.FROM
                    BilingualState.FROM -> BilingualState.TO_FROM
                    BilingualState.TO_FROM -> BilingualState.TO
                    null -> BilingualState.TO_FROM
                })

                fairyTaleFlag.setImageResource(
                    getBilingualFlag(langPair, bilingualState.get())
                )

                setRecyclerViewSplitScreen(
                    bilingualState.get(),
                    recyclerViewTo,
                    recyclerViewFrom
                )

                val toNameChange = when (bilingualState.get()) {
                    BilingualState.TO -> toName
                    BilingualState.FROM -> fromName
                    BilingualState.TO_FROM -> toName
                    null -> toName
                }

                val fromNameChange = when (bilingualState.get()) {
                    BilingualState.TO -> fromName
                    BilingualState.FROM -> toName
                    BilingualState.TO_FROM -> fromName
                    null -> fromName
                }

                sendMediaPlayerOnlyChangeLang(
                    "stories/${slug}/${getMediaLang().langCode}.mp3",
                    toNameChange,
                    fromNameChange,
                    (getFairyTaleTimestamp(fairyTale, emphasizedColumn, getMediaLang()) * 1000).toInt()
                )

                recyclerViewTo.scrollToPosition(emphasizedColumn)
                recyclerViewFrom.scrollToPosition(emphasizedColumn)
            }
        }

        startMediaPlayerService(
            slug,
            "stories/${slug}/${getMediaLang().langCode}.mp3"
        )

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (broadcastMediaStateReceiver != null) {
            LocalBroadcastManager.getInstance(requireContext())
                .unregisterReceiver(broadcastMediaStateReceiver!!)
            broadcastMediaStateReceiver = null
        }

        if (broadcastMediaTimeReceiver != null) {
            LocalBroadcastManager.getInstance(requireContext())
                .unregisterReceiver(broadcastMediaTimeReceiver!!)
            broadcastMediaTimeReceiver = null
        }

        stopMediaPlayerService()

        _binding = null
    }
}