package ru.mikhailskiy.intensiv.ui.tvshows

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.tv_shows_fragment.*
import ru.mikhailskiy.intensiv.MainActivity


import ru.mikhailskiy.intensiv.R
import ru.mikhailskiy.intensiv.data.Movie
import ru.mikhailskiy.intensiv.network.MovieApiClient
import ru.mikhailskiy.intensiv.ui.utils.showLoadingIndicator


class TvShowsFragment : Fragment() {
    var loadingIndicator: ImageView? = null
    private val adapter by lazy {
        GroupAdapter<GroupieViewHolder>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.tv_shows_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingIndicator = view.findViewById(R.id.loading_indicator)

        // Добавляем recyclerView
        movies_recycler_view.layoutManager = LinearLayoutManager(context)
        movies_recycler_view.adapter = adapter.apply { addAll(listOf()) }
        showLoadingIndicator(true,loadingIndicator ?: return)
        getTvShowMovies()

    }

    private fun getTvShowMovies() {
        val getTvShowMovies =
            MovieApiClient.apiClient.getTvShowMovies(MainActivity.API_KEY, "ru")
        getTvShowMovies
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { it ->
                    val movies = it.results
                    // Передаем результат в adapter и отображаем элементы
                    if (movies != null) {
                        updateAdapter(movies)
                    }
                    showLoadingIndicator(false, loadingIndicator ?: return@subscribe)
                },
                { error ->
                    // Логируем ошибку
                    Log.e(MainActivity.TAG, error.toString())
                }
            )
    }

    private fun updateAdapter(movies: List<Movie>) {
        val moviesList = listOf(
            CardContainerTvShows(
                movies.map {
                    it.title = it.name
                    TvShowsItem(it) { movie ->
                        openMovieDetails(
                            movie
                        )
                    }
                }.toList()
            )
        )

        adapter.apply { addAll(moviesList) }

    }

    private fun openMovieDetails(movie: Movie) {
        val options = navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }

        val bundle = Bundle()
        movie.id?.let { bundle.putInt("id", it) }
        "tv".let { bundle.putString("type_detail", it) }
        findNavController().navigate(R.id.movie_details_fragment, bundle, options)
    }

    override fun onStop() {
        super.onStop()
        adapter.clear()
    }

}