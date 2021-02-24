package ru.mikhailskiy.intensiv.ui.movie_details

import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_with_actor.*

import ru.mikhailskiy.intensiv.R
import ru.mikhailskiy.intensiv.data.Actor

class ActorItem(
    private val content: Actor
) : Item() {

    override fun getLayout() = R.layout.item_with_actor

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.description.text = content.name

        Picasso.get()
            .load(content.imageUrl)
            .into(viewHolder.image_preview)
    }
}