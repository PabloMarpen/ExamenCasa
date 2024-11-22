package com.example.examencasa

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {



    private val tipo = arrayOf("Aventura", "Cultural", "Relax")
    private val descripciones = arrayOf(
        "Modo aventura",
        "Modo cultura",
        "Modo relax"
    )

    private val imagenes = intArrayOf(
        R.drawable.aventura,
        R.drawable.cultura,
        R.drawable.relax
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val seleccion = findViewById<TextView>(R.id.ciudadSeleccionada)
        val boton = findViewById<Button>(R.id.button)
        val selectorCiudades = findViewById<Spinner>(R.id.spinner)
        val nombre = findViewById<EditText>(R.id.editTextText)
        val adaptadorPersonalizado = AdaptadorPersonalizado(this, R.layout.lineasspiner, tipo)
        selectorCiudades.adapter = adaptadorPersonalizado
        selectorCiudades.onItemSelectedListener = this

        boton.setOnClickListener{
            val intent = Intent(this, Paso2::class.java)
            var nombreString = nombre.text.toString()
            var tipo = seleccion.text.toString()
            intent.putExtra("nombre", nombreString)
            intent.putExtra("tipo", tipo)
            if (nombreString == ""){
                Toast.makeText(this, "Introduce todo", Toast.LENGTH_SHORT).show()
            }else {
                startActivity(intent)
            }
        }



    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val c = view?.findViewById<TextView>(R.id.nombre)
        val seleccion = findViewById<TextView>(R.id.ciudadSeleccionada)
        seleccion.text = c?.text
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        val seleccion = findViewById<TextView>(R.id.ciudadSeleccionada)
        seleccion.text = "nada seleccionado!"
    }

    /**
     * Creo una subclase de array adapter para personalizar el adaptador
     *
     * Las clases internas son útiles cuando deseas agrupar lógica relacionada en un solo lugar,
     * proporcionar un nivel adicional de encapsulación o cuando la clase interna necesita acceder
     * a miembros privados de la clase contenedora.
     *
     * Es importante notar que, en Kotlin, una clase interna anidada es interna solo si se declara
     * con la palabra clave inner. De lo contrario, se considera una clase interna estática y no
     * puede acceder a los miembros de la clase contenedora sin una instancia de la misma.
     *
     *
     */

    private inner class AdaptadorPersonalizado(
        context: Context,
        resource: Int,
        objects: Array<String>
    ) : ArrayAdapter<String>(context, resource, objects) {
        //Constructor de mi adaptador paso el contexto (this)
        // el layout, y los elementos

        /**
         * Reescribo el método getDropDownView para que me devuelva una fila personalizada en la
         * lista desplegable en vez del elemento que se encuentra en esa posición
         * @param posicion
         * @param ViewConvertida
         * @param padre
         * @return
         */
        override fun getDropDownView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {


            // Llama a la función para crear la fila personalizada y la devuelve
            return crearFilaPersonalizada(position, convertView, parent)
        }

        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            // Este método se llama para mostrar una vista personalizada en el elemento seleccionado

            // Llama a la función para crear la fila personalizada y la devuelve
            return crearFilaPersonalizada(position, convertView, parent)
        }

        /**
         * Método que me crea mis filas personalizadas pasando como parámetro la posición
         * la vista y la vista padre
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        private fun crearFilaPersonalizada(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {

            // Crea un objeto LayoutInflater para inflar la vista personalizada desde un diseño XML
            val layoutInflater = LayoutInflater.from(context)

            //Declaro una vista de mi fila, y la preparo para inflarla con datos
            // Los parametros son: XML descriptivo
            // Vista padre
            // Booleano que indica si se debe ceñir a las características del padre
            val rowView = convertView ?: layoutInflater.inflate(R.layout.lineasspiner, parent, false)

            //Fijamos el nombre de la ciudad
            rowView.findViewById<TextView>(R.id.nombre).text = tipo[position]

            //Fijamos la descripción de la ciudad
            rowView.findViewById<TextView>(R.id.descripcion).text = descripciones[position]

            //Fijamos la imagen de la ciudad
            rowView.findViewById<ImageView>(R.id.imagenCiudad).setImageResource(imagenes[position])

            // Devuelve la vista de fila personalizada
            return rowView
        }
    }
    }

