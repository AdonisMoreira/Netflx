package com.moreira.netlix


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moreira.netlix.model.Category
import com.moreira.netlix.util.CategoryTask

class MainActivity : AppCompatActivity(), CategoryTask.Callback {

    private lateinit var progress: ProgressBar
    private lateinit var adapter: CategoryAdapter
    private val categories = mutableListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progress = findViewById(R.id.progress_main)

       adapter = CategoryAdapter(categories){ id ->
           val intent = Intent(this@MainActivity, MovieActivity::class.java)
           intent.putExtra("id", id)
           startActivity(intent)
       }
        val rv: RecyclerView = findViewById(R.id.rv_main)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        CategoryTask(this).execute("https://api.tiagoaguiar.co/netflixapp/home?apiKey=19b74578-26df-4d41-8c9c-c045c19ca5d0")
    }

    override fun onResult(categories: List<Category>) {
        this.categories.clear()
        this.categories.addAll(categories)
        adapter.notifyDataSetChanged()
       progress.visibility = View.GONE
    }

    override fun onFailure(message: String) {
       Toast.makeText(this,message, Toast.LENGTH_LONG).show()
        progress.visibility = View.GONE
    }

    override fun onPreExecute() {
        progress.visibility = View.VISIBLE
    }

}