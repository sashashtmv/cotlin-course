package ru.mikhailskiy.intensiv.ui.movie_details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_card_actors.*
import kotlinx.android.synthetic.main.movie_details_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.mikhailskiy.intensiv.MainActivity

import ru.mikhailskiy.intensiv.R
import ru.mikhailskiy.intensiv.data.*
import ru.mikhailskiy.intensiv.network.MovieApiClient


class MovieDetailsFragment : Fragment() {

    private var idMove: Int = 0
    private val adapter by lazy {
        GroupAdapter<GroupieViewHolder>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.getInt("id")?.let {
            idMove = it
        }
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.movie_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Добавляем recyclerView
        item_container.layoutManager = LinearLayoutManager(context)
        item_container.adapter = adapter.apply { addAll(listOf()) }
        getDetailMovie()
        getDetailActor()

    }

    private fun getDetailMovie() {
        val getDetailMovie =
            MovieApiClient.apiClient.getDetailMovie(idMove, MainActivity.API_KEY, "ru")

        getDetailMovie.enqueue(object : Callback<DetailMovieResponse> {

            override fun onFailure(call: Call<DetailMovieResponse>, error: Throwable) {
                // Логируем ошибку
                Log.e(MainActivity.TAG, error.toString())
            }

            override fun onResponse(
                call: Call<DetailMovieResponse>, response: Response<DetailMovieResponse>
            ) {
                val date = response.body()
                // Передаем результат в adapter и отображаем элементы
                if (date != null) {
                    name_tv_show.text = date.original_title
                    movie_rating.rating = date.vote_average.div(2).toFloat()
                    tv_describe.text = date.overview
                    tv_describe_studio.text = date.production_companies.map { it -> it.name.toString().toUpperCase() }.toString().trim('[').trim(']')
                    tv_describe_genre.text = date.genres.map { it -> it.name.toString().toUpperCase() }.toString().trim('[').trim(']')
                    tv_describe_year.text = date.release_date
                    Picasso.get()
                        .load("https://image.tmdb.org/t/p/w500" + date.poster_path)
                        .into(image_main)
                }
            }
        })
    }

    private fun getDetailActor() {
        val getDetailActor =
            MovieApiClient.apiClient.getDetailActors(idMove, MainActivity.API_KEY, "ru")

        getDetailActor.enqueue(object : Callback<DetailActorResponse> {

            override fun onFailure(call: Call<DetailActorResponse>, error: Throwable) {
                // Логируем ошибку
                Log.e(MainActivity.TAG, error.toString())
            }

            override fun onResponse(
                call: Call<DetailActorResponse>,
                response: Response<DetailActorResponse>
            ) {

                val actors = response.body()?.cast
                // Передаем результат в adapter и отображаем элементы
                if (actors != null) {
                    updateAdapter(actors)
                }
            }
        })
    }


    private fun updateAdapter(actors: List<Actor>) {
        val actorsList = listOf(
            CardContainerDetailMove(
                actors.map {
                    ActorItem(it)
                }.toList()
            )
        )

        adapter.apply { addAll(actorsList) }
    }

    override fun onStop() {
        super.onStop()
        adapter.clear()
    }

}