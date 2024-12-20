package com.example.examencasa

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Paso2 : AppCompatActivity() {

    private lateinit var BroadcastReceiverTextView : TextView
    private lateinit var nombreTextView : TextView
    private lateinit var imageView: ImageView
    private lateinit var openDocumentLauncher: ActivityResultLauncher<Array<String>>
    private val STORAGE_PERMISSION_CODE = 100

    // Agregar el launcher para solicitar permisos de almacenamiento
    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Si el permiso es concedido, abrimos el selector de documentos
            openDocumentLauncher.launch(arrayOf("*/*"))
        } else {
            Toast.makeText(
                this,
                "El permiso de almacenamiento es necesario para seleccionar archivos",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private val modoAvion = object : BroadcastReceiver() {
        /**
         * Necesita el contexto y el intent para pasar la información
         */
        override fun onReceive(context: Context, intent: Intent) {

            val isAirplaneModeOn = intent.getBooleanExtra("state", false)
            BroadcastReceiverTextView.text = "airplane: $isAirplaneModeOn"
        }
    }

    private val Conexion = object : BroadcastReceiver() {
        /**
         * Necesita el contexto y el intent para pasar la información
         */
        override fun onReceive(context: Context, intent: Intent) {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo

            val isConnected = networkInfo?.isConnectedOrConnecting == true
            val connectionType = when (networkInfo?.type) {
                ConnectivityManager.TYPE_WIFI -> "Wi-Fi"
                ConnectivityManager.TYPE_MOBILE -> "Datos móviles"
                else -> "Sin conexión"
            }

            BroadcastReceiverTextView.text = if (isConnected) {
                "Conectado a $connectionType"
            } else {
                "Sin conexión a Internet"
            }
        }
    }

    private val Cargar = object : BroadcastReceiver() {
        /**
         * Maneja la recepción de eventos de energía conectada o desconectada.
         */
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_POWER_CONNECTED -> {
                    BroadcastReceiverTextView.text = "Dispositivo conectado a la energía"
                }
                Intent.ACTION_POWER_DISCONNECTED -> {
                    BroadcastReceiverTextView.text = "Dispositivo desconectado de la energía"
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_paso2)

        BroadcastReceiverTextView = findViewById(R.id.BroadcastReceiverTextView)
        nombreTextView = findViewById(R.id.nombreTextView)

        val bienvenida = findViewById<TextView>(R.id.bienvenida)
        val textoRecibido = intent.getStringExtra("nombre")
        val TipotextoRecibido = intent.getStringExtra("tipo")
        val ButtonPermisos = findViewById<Button>(R.id.permisos)
        val imageView2 = findViewById<ImageView>(R.id.imageView2)
        bienvenida.text = ("Bienvenido $textoRecibido has seleccionado $TipotextoRecibido")

        ButtonPermisos.setOnClickListener {
            checkAndRequestStoragePermission()
        }

        BroadcastReceiverTextView = findViewById(R.id.BroadcastReceiverTextView)

        registerReceiver(modoAvion, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED))
        registerReceiver(Conexion, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        registerReceiver(Cargar, IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        })

        openDocumentLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                // Mostrar la imagen seleccionada en el ImageView
                imageView2.setImageURI(uri)
                nombreTextView.text = uri.lastPathSegment
            } else {
                Toast.makeText(this, "No se seleccionó ningún archivo", Toast.LENGTH_SHORT).show()
            }
        }


        ButtonPermisos.setOnClickListener {
            // Lanzamos el selector de documentos para acceder a imágenes
            openDocumentLauncher.launch(arrayOf("*/*"))
        }


    }
    private fun checkAndRequestStoragePermission() {
        when {
            // Verificar si ya tenemos el permiso
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Si ya tenemos el permiso, abrimos el selector de documentos
                openDocumentLauncher.launch(arrayOf("*/*"))
            }
            // Verificar si debemos mostrar la explicación del permiso
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                // Mostrar diálogo explicando por qué necesitamos el permiso
                Toast.makeText(
                    this,
                    "Necesitamos acceso al almacenamiento para seleccionar archivos",
                    Toast.LENGTH_LONG
                ).show()
                // Solicitar el permiso después de mostrar la explicación
                storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            else -> {
                // Solicitar el permiso directamente
                storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("nombreTextView", nombreTextView.text.toString());
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        nombreTextView.text = savedInstanceState.getString("nombreTextView", "ningún seleccionado")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(Cargar)
    }

}
