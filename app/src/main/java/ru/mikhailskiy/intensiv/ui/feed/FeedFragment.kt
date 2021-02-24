package ru.mikhailskiy.intensiv.ui.feed

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.feed_fragment.*
import kotlinx.android.synthetic.main.feed_header.*
import kotlinx.android.synthetic.main.search_toolbar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.mikhailskiy.intensiv.MainActivity
import ru.mikhailskiy.intensiv.R
import ru.mikhailskiy.intensiv.data.Movie
import ru.mikhailskiy.intensiv.data.MoviesResponse
import ru.mikhailskiy.intensiv.network.MovieApiClient
import ru.mikhailskiy.intensiv.ui.afterTextChanged
import timber.log.Timber

class FeedFragment : Fragment() {

    private val adapter by lazy {
        GroupAdapter<GroupieViewHolder>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.feed_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Добавляем recyclerView
        movies_recycler_view.layoutManager = LinearLayoutManager(context)
        movies_recycler_view.adapter = adapter.apply { addAll(listOf()) }

        search_toolbar.search_edit_text.afterTextChanged {
            Timber.d(it.toString())
            if (it.toString().length > 3) {
                openSearch(it.toString())
            }
        }
        getNowPlayingMovies()
        getPopularMovies()
        getUpComingMovies()

    }

    private fun getNowPlayingMovies() {
        val getNowPlayingMovies =
            MovieApiClient.apiClient.getNowPlayingMovies(MainActivity.API_KEY, "ru")

        getNowPlayingMovies.enqueue(object : Callback<MoviesResponse> {

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
                    updateAdapter(0, movies)
                }
            }
        })
    }

    private fun getPopularMovies() {
        val getPopularMovies =
            MovieApiClient.apiClient.getPopularMovies(MainActivity.API_KEY, "ru")

        getPopularMovies.enqueue(object : Callback<MoviesResponse> {

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
                    updateAdapter(1, movies)
                }
            }
        })
    }

    private fun getUpComingMovies() {
        val getUpComingMovies =
            MovieApiClient.apiClient.getUpComingMovies(MainActivity.API_KEY, "ru")

        getUpComingMovies.enqueue(object : Callback<MoviesResponse> {

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
                    updateAdapter(2, movies)
                }
            }
        })
    }

    private fun updateAdapter(idAdapter: Int, movies: List<Movie>) {
        when (idAdapter) {
            0 -> {
                val moviesList = listOf(
                    MainCardContainer(
                        R.string.recommended,
                        movies.map {
                            MovieItem(it) { movie ->
                                openMovieDetails(
                                    movie
                                )
                            }
                        }.toList()
                    )
                )
                adapter.apply { addAll(moviesList) }
            }
            1 -> {
                val popularMoviesList = listOf(
                    MainCardContainer(
                        R.string.popular,
                        movies.map {
                            MovieItem(it) { movie ->
                                openMovieDetails(movie)
                            }
                        }.toList()
                    )
                )

                adapter.apply { addAll(popularMoviesList) }
            }
            2 -> {
                val newMoviesList = listOf(
                    MainCardContainer(
                        R.string.upcoming,
                        movies.map {
                            MovieItem(it) { movie ->
                                openMovieDetails(movie)
                            }
                        }.toList()
                    )
                )

                adapter.apply { addAll(newMoviesList) }
            }
        }
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
        "move".let { bundle.putString("type_detail", it) }
        findNavController().navigate(R.id.movie_details_fragment, bundle, options)
    }

    private fun openSearch(searchText: String) {
        val options = navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }

        val bundle = Bundle()
        bundle.putString("search", searchText)
        findNavController().navigate(R.id.search_dest, bundle, options)
    }

    override fun onStop() {
        super.onStop()
        search_toolbar.clear()
        adapter.clear()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }
}