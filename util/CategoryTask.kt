package com.moreira.netlix.util

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.moreira.netlix.model.Category
import com.moreira.netlix.model.Movie
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection
import javax.security.auth.callback.Callback

class CategoryTask(private val callback: Callback) {

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    interface Callback {
        fun onPreExecute()
        fun onResult(categories: List<Category>)
        fun onFailure(message: String)
    }

    fun execute(url: String) {
            callback.onPreExecute()

        executor.execute {

            var urlConnection: HttpsURLConnection? = null
            var stream: InputStream? = null


            try {
                val requestURL = URL(url)
                urlConnection = requestURL.openConnection() as HttpsURLConnection
                urlConnection.readTimeout = 2000
                urlConnection.connectTimeout = 2000

                val statusCode: Int = urlConnection.responseCode
                if (statusCode > 400) {
                        throw IOException("Erro ao se comunicar com o servidor!")
                }

                stream = urlConnection.inputStream
                val jsonAsString = stream.bufferedReader().use { it.readText() }
                val categories =  toCategories(jsonAsString)
                Log.i("Teste", categories.toString())

                handler.post{
                    callback.onResult(categories)
                }

            }catch (e: IOException){
                val message = e.message ?: "Erro desconhecido"
                Log.e("teste", message, e)
                handler.post {
                    callback.onFailure(message)
                }
            }finally {
                urlConnection?.disconnect()
                stream?.close()

            }
        }
    }

    private fun toCategories(jasonAsString: String) : List<Category>{
            val categories = mutableListOf<Category>()

        val jsonRoot = JSONObject(jasonAsString)
        val jsonCategories = jsonRoot.getJSONArray("category")
        for(i in 0 until jsonCategories.length()){
            val jsonCategory = jsonCategories.getJSONObject(i)

            val title = jsonCategory.getString("title")
            val jsonMovies = jsonCategory.getJSONArray("movie")

            val movies = mutableListOf<Movie>()
            for (j in 0 until jsonMovies.length()){
                val jsonMovie = jsonMovies.getJSONObject(j)
                val id = jsonMovie.getInt("id")
                val coverUrl = jsonMovie.getString("cover_url")

                movies.add(Movie(id,coverUrl))
            }

            categories.add(Category(title, movies))
        }


        return categories
    }
}