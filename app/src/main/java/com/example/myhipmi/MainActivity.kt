package com.example.myhipmi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.myhipmi.ui.navigation.NavGraph
import com.example.myhipmi.ui.theme.MyHIPMITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyHIPMITheme {
                MyHipmiApp()
            }
        }
    }
}

@Composable
fun MyHipmiApp() {
    val navController = rememberNavController()
    NavGraph(navController = navController)
}
