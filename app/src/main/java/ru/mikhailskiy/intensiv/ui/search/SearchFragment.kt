package ru.mikhailskiy.intensiv.ui.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.feed_header.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.search_toolbar.view.*

import ru.mikhailskiy.intensiv.MainActivity
import ru.mikhailskiy.intensiv.R
import ru.mikhailskiy.intensiv.data.Movie
import ru.mikhailskiy.intensiv.network.MovieApiClient
import ru.mikhailskiy.intensiv.ui.afterTextChanged
import ru.mikhailskiy.intensiv.ui.tvshows.CardContainerTvShows
import ru.mikhailskiy.intensiv.ui.tvshows.TvShowsItem
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SearchFragment : Fragment() {
    var searchTerm: String = ""
    private val adapter by lazy {
        GroupAdapter<GroupieViewHolder>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchTerm = requireArguments().getString("search").toString()
        search_toolbar.setText(searchTerm)
        movies_recycler_view.adapter = adapter.apply { addAll(listOf()) }

        Observable.create(ObservableOnSubscribe<String>() { emiter ->
            search_toolbar.search_edit_text.afterTextChanged {
                emiter.onNext(it.toString())

            }
        })
            .filter(Predicate<String>() {
                it.trim()
                it.length >= 3
            })
            .debounce(500, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                searchTerm = result
                getSearchMovies()
            }, { t: Throwable? -> Timber.w(t, "Failed to get search results") })

        getSearchMovies()
    }

    private fun getSearchMovies() {
        val getSearchMovies =
            MovieApiClient.apiClient.getSearchMovies(MainActivity.API_KEY, "ru", searchTerm)
        getSearchMovies
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { it ->
                    val movies = it.results
                    // Передаем результат в adapter и отображаем элементы
                    if (movies != null) {
                        updateAdapter(searchTerm, movies)
                    }
                },
                { error ->
                    // Логируем ошибку
                    Log.e(MainActivity.TAG, error.toString())
                }
            )
    }

    private fun updateAdapter(title: String, movies: List<Movie>) {
        adapter.clear()
        val moviesList = listOf(
            CardContainerTvShows(
                movies.map {
                    TvShowsItem(it) { movie ->
                        openMovieDetails(
                            movie
                        )
                    }
                }.toList()
            )
        )

        adapter.apply { addAll(moviesList) }
        adapter.notifyDataSetChanged()

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

    override fun onStop() {
        super.onStop()
        adapter.clear()
    }
}