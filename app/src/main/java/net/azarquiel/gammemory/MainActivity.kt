package net.azarquiel.gammemory

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity(), OnClickListener {
    private var isTapando: Boolean = false
    private var inicioTime: Long = 0
    private lateinit var ivprimera: ImageView
    private lateinit var linearv: LinearLayout
    private lateinit var random: Random
    private val vpokemons = Array(809) { i -> i+1}
    private val vjuego = Array(30) {0}
    private var isFirst = true
    private var aciertos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        inicioTime = System.currentTimeMillis()
        
        linearv = findViewById<LinearLayout>(R.id.linearv)

        random = Random(System.currentTimeMillis())
        newGame()
    }

    private fun newGame() {
        isFirst = true
        aciertos = 0
        inicioTime = System.currentTimeMillis()
        vpokemons.shuffle(random)
        var x = 0
        for (v in 0 until 2) {
            for (p in 0 until 15) {
                vjuego[x] = vpokemons[p]
                x++
            }
        }
        vjuego.shuffle()
        var c = 0
        for (i in 0 until linearv.childCount) {
            val linearh = linearv.getChildAt(i) as LinearLayout
            for (j in 0 until linearh.childCount) {
                val ivpokemon = linearh.getChildAt(j) as ImageView
                ivpokemon.isEnabled = true
                ivpokemon.setOnClickListener(this)
                val foto = "pokemon${vjuego[c]}"
                ivpokemon.tag = vjuego[c]
                c++
                val id = resources.getIdentifier(foto, "drawable",packageName)
                ivpokemon.setBackgroundResource(id)
                ivpokemon.setImageResource(R.drawable.tapa)
            }
        }
    }

    override fun onClick(v: View?) {

        val ivpulsada = v as ImageView
        val pokemopulsado = ivpulsada.tag as Int

        if(isTapando) return

        ivpulsada.setImageResource(android.R.color.transparent)
        if (isFirst) {
            ivprimera = ivpulsada
        }
        else {
            if (ivpulsada==ivprimera) {
                tostada("No me engañes gorrión......")
                return
            }
            if (pokemopulsado == ivprimera.tag as Int) {
                aciertos++
                ivpulsada.isEnabled = false
                ivprimera.isEnabled = false
                checkGameOver()
            }
            else {
                GlobalScope.launch() {
                    isTapando = true
                    SystemClock.sleep(1000)
                    launch(Main) {
                        ivprimera.setImageResource(R.drawable.tapa)
                        ivpulsada.setImageResource(R.drawable.tapa)
                        isTapando = false
                    }
                }
            }
        }
        isFirst = !isFirst
    }

    private fun checkGameOver() {
        if ( aciertos == 15) {
            val time = calcTime()
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Has completado el juego!!")
            builder.setMessage("Tardaste $time segundos")
            builder.setPositiveButton("Volver a jugar") { _ , _ ->
                newGame()
            }
            builder.setNegativeButton("Salir") { _ , _ ->
                finish()
            }
            builder.setCancelable(false)
            builder.show()
        }
    }

    private fun calcTime(): Long {
        return ((System.currentTimeMillis() - inicioTime)/1000)
    }

    fun tostada (msg:String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}