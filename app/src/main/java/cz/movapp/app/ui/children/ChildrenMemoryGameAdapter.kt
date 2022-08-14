package cz.movapp.app.ui.children

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import cz.movapp.app.R

class ChildrenMemoryGameAdapter(private var data: MutableList<MemoryGameGridViewData>) : BaseAdapter() {

    private var layoutInflater: LayoutInflater? = null

    private var cardClickListener: ((Context, MemoryGameCardData) -> Unit)? = null
    private var cardChecker: ((Context, MemoryGameCardData) -> Unit)? = null

    override fun getCount(): Int {
        return data.size
    }

    // below function is use to return the item of grid view.
    override fun getItem(position: Int): Any? {
        return null
    }

    // below function is use to return item id of grid view.
    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var cardView = view

        val itemData = data[position]

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent!!.context)
        }

        if (cardView == null) {
            cardView = layoutInflater!!.inflate(R.layout.children_memory_game_card, parent, false)
        }

        val imageCard = cardView!!.findViewById<ImageView>(R.id.memory_game_card)

        val cardData = MemoryGameCardData(itemData, imageCard)

        imageCard.setOnClickListener{
            cardClickListener?.let { it1 -> it1(parent!!.context, cardData) }
        }

        /* check if card should be flipped */
        cardChecker?.let { it(parent!!.context, cardData) }

        return cardView
    }

    fun setHandlers(cardClickHandler: ((Context, MemoryGameCardData) -> Unit)?, cardCheckerHandler: ((Context, MemoryGameCardData) -> Unit)?) {
        cardClickListener = cardClickHandler
        cardChecker = cardCheckerHandler
    }
}