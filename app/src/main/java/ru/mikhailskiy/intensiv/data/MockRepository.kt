package ru.mikhailskiy.intensiv.data

object MockRepository {

    fun getMovies(): List<Movie> {

        val moviesList = mutableListOf<Movie>()
//        for (x in 0..10) {
//            val movie = Movie(
//                title = "Spider-Man $x",
//                voteAverage = 10.0 - x
//            )
//            moviesList.add(movie)
//        }

        return moviesList
    }

    fun getActors(): List<Actor> {

        val actorsList = mutableListOf<Actor>()
        for (x in 0..7) {
            val actor = Actor(
//                urlImage = "https://blog.ru.playstation.com/tachyon/sites/11/2019/08/unnamed-file-2.jpg?resize=1088,600&crop_strategy=smart&zoom=1.5",
//                nameActor = "Nicol Kidman"
            )
            actorsList.add(actor)
        }

        return actorsList
    }

}