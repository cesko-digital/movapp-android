package cz.movapp.app.ui.children

import android.content.Context
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.internal.ViewUtils.dpToPx
import cz.movapp.android.playSound
import cz.movapp.app.MainViewModel
import cz.movapp.app.R
import cz.movapp.app.databinding.FragmentChildrenMemoryGameBinding

class ChildrenMemoryGameFragment  : Fragment() {

    private var _binding: FragmentChildrenMemoryGameBinding? = null

    private val mainSharedViewModel: MainViewModel by activityViewModels()
    private val memoryGameViewModel: ChildrenMemoryGameViewModel by viewModels()

    private val tmpFlipped = mutableListOf<MemoryGameCardData>()
    private val flipped = mutableListOf<MemoryGameCardData>()

    private var cardClickListener: (Context, MemoryGameCardData) -> Unit =
        listener@{ context, cardData  ->
            if (tmpFlipped.size == 2) {
                playGameSound(context, "memory-game-sounds/card_flip.mp3")

                flipCardBack(tmpFlipped[0])
                flipCardBack(tmpFlipped[1])
                tmpFlipped.clear()
                return@listener
            }

            if (memoryGameViewModel.discovered[cardData.data.coordinateX][cardData.data.coordinateY]) {
                return@listener
            }

            if (tmpFlipped.size > 1) {
                return@listener
            }

            if (cardData.data in tmpFlipped.map { it.data }) {
                return@listener
            }

            tmpFlipped.add(cardData)

            flipCard(context, cardData)

            playGameSound(
                context,
                if (cardData.data.mainSound) {
                    cardData.data.item.main_sound_local
                } else {
                    cardData.data.item.source_sound_local
                }
            )

            if (tmpFlipped.size == 2) {
                if (checkSameCards(tmpFlipped[0], tmpFlipped[1])) {
                    flipped.add(tmpFlipped[0])
                    flipped.add(tmpFlipped[1])

                    tmpFlipped.clear()

                    if (checkGameFinished()) {
                        binding.imageVolume.visibility = View.INVISIBLE
                        binding.gridViewMemoryGame.setBackgroundResource(R.drawable.card_back)
                        binding.gridViewMemoryGame.setPadding(dpToPx(context, 30).toInt())

                        for (i in 0 until flipped.size) {
                            flipped[i].image.setPadding(dpToPx(context, 15).toInt())
                        }

                        playGameSound(context, "memory-game-sounds/win_music_sh.mp3")
                    } else {
                        playGameSound(context, "memory-game-sounds/reward_sfx.mp3")
                    }

                }
            }
        }

    private var cardChecker: (Context, MemoryGameCardData) -> Unit = { context, cardData ->
        if (memoryGameViewModel.discovered[cardData.data.coordinateX][cardData.data.coordinateY] ||
                cardData in tmpFlipped) {
            flipCard(context, cardData)
        } else {
            flipCardBack(cardData)
        }
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChildrenMemoryGameBinding.inflate(inflater, container, false)
        val root: View = binding.root

        memoryGameViewModel.memoryGameAdapter.observe(viewLifecycleOwner) {
            binding.imageVolume.visibility = View.VISIBLE

            binding.gridViewMemoryGame.background = null
            binding.gridViewMemoryGame.setPadding(0)

            resetCurrentAdapterHandlers()
            it.setHandlers(cardClickListener, cardChecker)

            flipped.clear()
            tmpFlipped.clear()

            binding.gridViewMemoryGame.invalidateViews()
            binding.gridViewMemoryGame.numColumns = memoryGameViewModel.edgeSize
            binding.gridViewMemoryGame.adapter = it
            (binding.gridViewMemoryGame.adapter as ChildrenMemoryGameAdapter).notifyDataSetChanged()
        }

        mainSharedViewModel.selectedLanguage.observe(viewLifecycleOwner) {
            memoryGameViewModel.createNewGame(it)
        }

        binding.buttonMemoryGameNewGame.setOnClickListener{
            memoryGameViewModel.createNewGame(mainSharedViewModel.selectedLanguage.value!!)
        }

        binding.imageVolume.setOnClickListener {
            memoryGameViewModel.toggleVolume()
            memoryGameViewModel.store()
        }

        memoryGameViewModel.mute.observe(viewLifecycleOwner) {
            if (it) {
                binding.imageVolume.setImageResource(R.drawable.ic_baseline_volume_off_24)
            } else {
                binding.imageVolume.setImageResource(R.drawable.ic_baseline_volume_up_24)
            }
        }

        return root
    }

    private fun checkSameCards(card1: MemoryGameCardData, card2: MemoryGameCardData): Boolean {
        if (card1.data.item == card2.data.item) {
            memoryGameViewModel.discovered[card1.data.coordinateX][card1.data.coordinateY] = true
            memoryGameViewModel.discovered[card2.data.coordinateX][card2.data.coordinateY] = true
            return true
        }

        return false
    }

    private fun checkGameFinished(): Boolean {
        for (x in 0 until memoryGameViewModel.edgeSize) {
            for (y in 0 until memoryGameViewModel.edgeSize) {
                if (!memoryGameViewModel.discovered[x][y]) {
                    return false
                }
            }
        }

        return true
    }

    private fun flipCard(context: Context, cardData: MemoryGameCardData) {
        val layoutParams = cardData.image.layoutParams
        layoutParams.height = cardData.image.height
        layoutParams.width = cardData.image.width

        cardData.image.setImageDrawable(cardData.data.drawable)

        cardData.image.layoutParams = layoutParams
    }

    private fun flipCardBack(cardData: MemoryGameCardData) {
        cardData.image.setImageResource(R.drawable.card_back)

        cardData.image.imageTintList = null

        memoryGameViewModel.discovered[cardData.data.coordinateX][cardData.data.coordinateY] = false
    }

    private fun resetCurrentAdapterHandlers() {
        if (binding.gridViewMemoryGame.adapter != null) {
            val adapter = (binding.gridViewMemoryGame.adapter as ChildrenMemoryGameAdapter)
            adapter.setHandlers(null, null)
        }
    }

    private fun playGameSound(
        context: Context,
        assetFileName: String
    ): MediaPlayer? {
        return if (!memoryGameViewModel.mute.value!!) {
            playSound(context, assetFileName)
        } else {
            null
        }
    }


    override fun onResume() {
        super.onResume()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onPause() {
        super.onPause()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }

    override fun onDestroyView() {
        super.onDestroyView()

        resetCurrentAdapterHandlers()
        _binding = null
    }
}