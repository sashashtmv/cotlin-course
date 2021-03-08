package ru.mikhailskiy.intensiv.ui.feed

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
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function3
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.feed_fragment.*
import kotlinx.android.synthetic.main.feed_header.*
import kotlinx.android.synthetic.main.search_toolbar.view.*
import ru.mikhailskiy.intensiv.MainActivity
import ru.mikhailskiy.intensiv.MainActivity.Companion.TAG
import ru.mikhailskiy.intensiv.R
import ru.mikhailskiy.intensiv.data.Movie
import ru.mikhailskiy.intensiv.data.MoviesResponse
import ru.mikhailskiy.intensiv.network.MovieApiClient
import ru.mikhailskiy.intensiv.ui.afterTextChanged
import ru.mikhailskiy.intensiv.ui.utils.showLoadingIndicator
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit


class FeedFragment : Fragment() {
    private var loadingIndicator: ImageView? = null
    private var searchTerm: String = ""

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
        loadingIndicator = view.findViewById(R.id.loading_indicator)

        Observable.create(ObservableOnSubscribe<String>() {emiter ->
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
                openSearch()
            }, { t: Throwable? -> Timber.w(t, "Failed to get search results") })

        showLoadingIndicator(true,loadingIndicator ?: return)
        val getNowPlayingMovies =
            MovieApiClient.apiClient.getNowPlayingMovies(MainActivity.API_KEY, "ru")
        val getPopularMovies =
            MovieApiClient.apiClient.getPopularMovies(MainActivity.API_KEY, "ru")
        val getUpComingMovies =
            MovieApiClient.apiClient.getUpComingMovies(MainActivity.API_KEY, "ru")

        Single.zip(
            getNowPlayingMovies,
            getPopularMovies,
            getUpComingMovies,
            Function3<MoviesResponse, MoviesResponse, MoviesResponse, HashMap<Int, MoviesResponse>> { nowPlaying, popular, upComing ->
                return@Function3 hashMapOf(0 to nowPlaying, 1 to popular, 2 to upComing)
            }
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { it ->
                    it.map { element ->
                        updateAdapter(element.key, element.value.results)

                    }
                    showLoadingIndicator(false, loadingIndicator ?: return@subscribe)
                }, { error ->
                    Log.e(TAG, error.toString())
                    showLoadingIndicator(false, loadingIndicator ?: return@subscribe)
                }
            )
    }

    private fun updateAdapter(idAdapter: Int, movies: List<Movie>) {
        var titleId: Int = 0
        if (idAdapter == 0) titleId = R.string.recommended
        if (idAdapter == 1) titleId = R.string.popular
        if (idAdapter == 2) titleId = R.string.upcoming
        val moviesList = listOf(
            MainCardContainer(
                titleId,
                movies.map {
                    MovieItem(it) { movie ->
                        openMovieDetails(
                            movie
                        )
                    }
                }.toList()
            )
        )
        adapter.addAll(moviesList)
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
        bundle.putString("type_detail", "move")
        findNavController().navigate(R.id.movie_details_fragment, bundle, options)
    }

    private fun openSearch() {
        val options = navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }

        val bundle = Bundle()
        bundle.putString("search", searchTerm)
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