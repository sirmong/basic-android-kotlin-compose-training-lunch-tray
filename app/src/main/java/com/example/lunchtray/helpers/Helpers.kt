package com.example.lunchtray.helpers

import androidx.navigation.NavHostController
import com.example.lunchtray.Routes
import com.example.lunchtray.ui.OrderViewModel

object Helpers {
    fun cancelOrderAndNavigateToStart(
        viewModel: OrderViewModel,
        navController: NavHostController
    ) {
        viewModel.resetOrder()
        navController.popBackStack(Routes.Start.name, false)
    }
}