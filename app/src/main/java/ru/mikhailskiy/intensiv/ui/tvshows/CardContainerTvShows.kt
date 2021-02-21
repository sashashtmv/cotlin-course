package ru.mikhailskiy.intensiv.ui.tvshows

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_card_tv_shows.*

import ru.mikhailskiy.intensiv.R

class CardContainerTvShows(
    private val items: List<Item>
) : Item() {

    override fun getLayout() = R.layout.item_card_tv_shows

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.item_container.adapter =
            GroupAdapter<GroupieViewHolder>().apply { addAll(items) }
    }
}