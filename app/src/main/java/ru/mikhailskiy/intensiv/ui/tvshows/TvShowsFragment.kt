package ru.mikhailskiy.intensiv.ui.tvshows

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.tv_shows_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.mikhailskiy.intensiv.MainActivity


import ru.mikhailskiy.intensiv.R
import ru.mikhailskiy.intensiv.data.Movie
import ru.mikhailskiy.intensiv.data.MoviesResponse
import ru.mikhailskiy.intensiv.network.MovieApiClient


class TvShowsFragment : Fragment() {
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

        // Добавляем recyclerView
        movies_recycler_view.layoutManager = LinearLayoutManager(context)
        movies_recycler_view.adapter = adapter.apply { addAll(listOf()) }
        getTvShowMovies()

    }

    private fun getTvShowMovies() {
        val getTvShowMovies =
            MovieApiClient.apiClient.getTvShowMovies(MainActivity.API_KEY, "ru")

        getTvShowMovies.enqueue(object : Callback<MoviesResponse> {

            override fun onFailure(call: Call<MoviesResponse>, error: Throwable) {
                // Логируем ошибку
                Log.e(MainActivity.TAG, error.toString())
            }

            override fun onResponse(
                call: Call<MoviesResponse>,
                response: Response<MoviesResponse>
            ) {

                val movies = response.body()?.results
                // Передаем результат в adapter и отображаем элементы
                if (movies != null) {
                    updateAdapter(movies)
                }
            }
        })
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