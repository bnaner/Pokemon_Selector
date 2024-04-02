package com.example.pokemon_selector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import com.bumptech.glide.Glide
import kotlinx.coroutines.delay
import kotlin.concurrent.thread
import kotlin.random.Random

class Pokemon{
    var imageURL = ""
    var species = ""
    var type = ""
}

var selectedPoke = Pokemon()


class MainActivity : AppCompatActivity() {
    private lateinit var  pokeList : MutableList<Pokemon>
    private lateinit var  rvPoke : RecyclerView
    private lateinit var adapter: PokeAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pokeList = mutableListOf<Pokemon>()
        rvPoke = findViewById(R.id.rvPokemon)
        val btnRandom = findViewById<Button>(R.id.btnRandom)
        val btnSearch = findViewById<Button>(R.id.btnSearch)
        val imgPokemon = findViewById<ImageView>(R.id.viewPokemon)
        val txtSpeciesOut = findViewById<TextView>(R.id.txtSpeciesOutput)
        val txtTypesOut = findViewById<TextView>(R.id.txtTypeOutput)
        val txtSearch = findViewById<EditText>(R.id.inputPokemon)

        btnRandom.setOnClickListener{
            var randID = Random.nextInt(1, 1025)
            getPokemon(randID.toString())

            txtSpeciesOut.text = selectedPoke.species
            txtTypesOut.text = selectedPoke.type

            Glide.with(this).load(selectedPoke.imageURL).fitCenter().into(imgPokemon)
        }
        btnSearch.setOnClickListener{
            var name : String = txtSearch.text.toString()
            getPokemon(name.lowercase())
            txtSpeciesOut.text = selectedPoke.species
            txtTypesOut.text = selectedPoke.type

            Glide.with(this).load(selectedPoke.imageURL).fitCenter().into(imgPokemon)
        }

        adapter = PokeAdapter(pokeList)
        rvPoke.adapter = adapter
        rvPoke.layoutManager = LinearLayoutManager(this@MainActivity)


        for (i in 1..11){
            getPokemon(i.toString())
        }

    }

    private fun getPokemon(ID : String){
        val client = AsyncHttpClient();

        client["https://pokeapi.co/api/v2/pokemon/" + ID, object : JsonHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                Log.d("Pokemon", "response successful$json")
                var selectedPoke = Pokemon()
                selectedPoke.imageURL = json.jsonObject.getJSONObject("sprites").getString("front_default")
                selectedPoke.species = json.jsonObject.getString("name")
                selectedPoke.type = json.jsonObject.getJSONArray("types").getJSONObject(0).getJSONObject("type").getString("name")
                if (json.jsonObject.getJSONArray("types").length() == 2)
                    selectedPoke.type += "/" + json.jsonObject.getJSONArray("types").getJSONObject(1).getJSONObject("type").getString("name")
                pokeList.add(selectedPoke)
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("Pokemon Error", errorResponse)

                val toast = Toast.makeText(this@MainActivity, "Invalid Pokemon", Toast.LENGTH_SHORT) // in Activity
                toast.show()
            }
        }]
    }


}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val pokeImage: ImageView
    val speciesText : TextView
    val typeText : TextView

    init {
        // Find our RecyclerView item's ImageView for future use
        pokeImage = view.findViewById(R.id.viewListPokemon)
        speciesText = view.findViewById(R.id.txtListSpeciesOutput)
        typeText = view.findViewById((R.id.txtListTypeOutput))
    }
}
class PokeAdapter(private val pokeList : List<Pokemon>) : RecyclerView.Adapter<ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pokemon_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView).load(pokeList[position].imageURL).centerCrop().into(holder.pokeImage)
        holder.speciesText.text = pokeList[position].species
        holder.typeText.text = pokeList[position].type
    }

    override fun getItemCount() = pokeList.size

}


