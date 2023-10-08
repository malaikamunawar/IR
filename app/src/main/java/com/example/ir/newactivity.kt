package com.example.ir

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.view.SurfaceView
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView


class newactivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var navigationView: NavigationView
    lateinit var surfaceView: SurfaceView
    private lateinit var webview : WebView
    private lateinit var currentPage: PdfRenderer.Page
    private lateinit var pdfRenderer: PdfRenderer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newactivity)
        drawerLayout = findViewById(R.id.my_drawer_layout)
        navigationView = findViewById(R.id.nav)
      //   surfaceView = findViewById(R.id.pdf_surface_view)
        webview = findViewById(R.id.webview)

        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        navigationView.setNavigationItemSelectedListener(this@newactivity);
        actionBarDrawerToggle.syncState()
        // to make the Navigation drawer icon always appear on the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }



private fun NavigationView.setNavigationItemSelectedListener(context: Context) {
    this.setNavigationItemSelectedListener { menuItem ->
        val assetManager = context.assets
        val webSettings: WebSettings = webview.getSettings()
        webSettings.javaScriptEnabled = true
        when (menuItem.itemId) {
            R.id.ch1 -> {



                webview.loadUrl("https://opg.optica.org/josaa/abstract.cfm?uri=josaa-19-12-2329")
         //       webview.loadUrl("https://www.google.com");

//                val parcelFileDescriptor = assetFileDescriptor.parcelFileDescriptor
//
//// Create a PdfRenderer object
//                val pdfRenderer = PdfRenderer(parcelFileDescriptor)
//
//// Display the first page
//                displayPage(pdfRenderer, 0)
                true
            }
            R.id.ch2 -> {
                webview.loadUrl("https://opg.optica.org/josaa/home.cfm")

                true
            }
            R.id.ch3 -> {
                webview.loadUrl("https://opg.optica.org/conferences.cfm")

                    true
            }
            R.id.ch4 -> {
                webview.loadUrl("https://opg.optica.org/conferences.cfm#global-nav")

                true
            }
            R.id.ch5 -> {
                webview.loadUrl("https://preprints.opticaopen.org/")
                true
            }

            R.id.ch6 -> {
                webview.loadUrl("https://knowledge.figshare.com/")

                true
            }
            // Add more cases as needed
            else -> {
                false
            }
        }
    }
}

    private fun displayPage(pdfRenderer: PdfRenderer, pageIndex: Int) {
        currentPage = pdfRenderer.openPage(pageIndex)

        val bitmap = Bitmap.createBitmap(
            currentPage.width,
            currentPage.height,
            Bitmap.Config.ARGB_8888
        )
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        val canvas = surfaceView.holder.lockCanvas()
        canvas?.let {
            it.drawBitmap(bitmap, 0f, 0f, null)
            surfaceView.holder.unlockCanvasAndPost(it)
        }

        currentPage.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        pdfRenderer.close()
    }
}
