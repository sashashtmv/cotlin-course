package ru.mikhailskiy.intensiv.ui.movie_details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.movie_details_fragment.*
import ru.mikhailskiy.intensiv.MainActivity

import ru.mikhailskiy.intensiv.R
import ru.mikhailskiy.intensiv.data.*
import ru.mikhailskiy.intensiv.network.MovieApiClient
import ru.mikhailskiy.intensiv.ui.utils.showLoadingIndicator


class MovieDetailsFragment : Fragment() {

    private var idMove: Int = 0
    private var typeDetail: String = ""
    private var loadingIndicator: ImageView? = null
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
        arguments?.getString("type_detail")?.let {
            typeDetail = it
        }
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.movie_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingIndicator = view.findViewById(R.id.loading_indicator)

        // Добавляем recyclerView
        item_container.adapter = adapter.apply { addAll(listOf()) }
        showLoadingIndicator(true, loadingIndicator ?: return)
        getDetailMovie()
        getDetailActor()

    }

    private fun getDetailMovie() {
        var getDetailMovie =
            MovieApiClient.apiClient.getDetailMovie(idMove, MainActivity.API_KEY, "ru")
        if (typeDetail == "tv")
            getDetailMovie =
                MovieApiClient.apiClient.getDetailTvMovie(idMove, MainActivity.API_KEY, "ru")
        getDetailMovie
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { it ->
                    // Передаем результат в adapter и отображаем элементы
                    if (it != null) {
                        if (it.original_title == null) it.original_title = it.name
                        name_tv_show.text = it.original_title
                        movie_rating.rating = it.vote_average.div(2).toFloat()
                        tv_describe.text = it.overview
                        tv_describe_studio.text =
                            it.production_companies.map { it -> it.name.toString().toUpperCase() }
                                .toString().trim('[').trim(']')
                        tv_describe_genre.text =
                            it.genres.map { it -> it.name.toString().toUpperCase() }.toString()
                                .trim('[').trim(']')
                        tv_describe_year.text = it.release_date
                        Picasso.get()
                            .load("https://image.tmdb.org/t/p/w500" + it.poster_path)
                            .into(image_main, object : com.squareup.picasso.Callback {
                                override fun onSuccess() {
                                    showLoadingIndicator(false, loadingIndicator ?: return)

                                }

                                override fun onError(e: java.lang.Exception?) {
                                    //do smth when there is picture loading error
                                }

                            });
                    }
                },
                { error ->
                    // Логируем ошибку
                    Log.e(MainActivity.TAG, error.toString())
                }
            )
    }

    private fun getDetailActor() {
        var getDetailActor =
            MovieApiClient.apiClient.getDetailActors(idMove, MainActivity.API_KEY, "ru")
        if (typeDetail == "tv")
            getDetailActor =
                MovieApiClient.apiClient.getDetailTvActors(idMove, MainActivity.API_KEY, "ru")
        getDetailActor
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { it ->
                    val actors = it.cast
                    // Передаем результат в adapter и отображаем элементы
                    if (actors != null) {
                        updateAdapter(actors)
                    }
                },
                { error ->
                    // Логируем ошибку
                    Log.e(MainActivity.TAG, error.toString())
                }
            )
    }


    private fun updateAdapter(actors: List<Actor>) {
        val actorsList =
            actors.map {
                ActorItem(it)
            }

        adapter.apply { addAll(actorsList) }
    }

    override fun onStop() {
        super.onStop()
        adapter.clear()
    }

}