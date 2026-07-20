package com.hamidzar2002.untangle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hamidzar2002.untangle.ui.theme.UntangleTheme
import com.hamidzar2002.untangle.view.UntangleRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UntangleTheme {
                UntangleRoute()
            }
        }
    }
}

