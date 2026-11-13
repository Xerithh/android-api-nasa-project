package com.example.myapiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.myapiapp.data.local.AppDatabase
import com.example.myapiapp.data.repository.ApodRepository
import com.example.myapiapp.ui.navigation.AppNavigation
import com.example.myapiapp.ui.theme.MyAPIAppTheme
import com.example.myapiapp.ui.viewmodel.ApodViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ApodViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ApodRepository(dao = database.apodDao())
        ApodViewModel.provideFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAPIAppTheme {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}