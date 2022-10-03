package com.moreira.netlix.model

data class MovieDetail(
    val movie: Movie,
    val similar: List<Movie>
)
