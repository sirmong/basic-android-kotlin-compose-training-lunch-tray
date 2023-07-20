/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.helpers.Helpers
import com.example.lunchtray.ui.AccompanimentMenuScreen
import com.example.lunchtray.ui.AppBar
import com.example.lunchtray.ui.CheckoutScreen
import com.example.lunchtray.ui.EntreeMenuScreen
import com.example.lunchtray.ui.OrderViewModel
import com.example.lunchtray.ui.SideDishMenuScreen
import com.example.lunchtray.ui.StartOrderScreen

// TODO: Screen enum
enum class Routes(@StringRes val title: Int) {
    Start(R.string.start_order),
    EntreeMenu(R.string.choose_entree),
    SideDishMenu(R.string.choose_side_dish),
    AccompanimentMenu(R.string.choose_accompaniment),
    Checkout(R.string.order_checkout)
}

// TODO: AppBar

@Composable
fun LunchTrayApp() {
    val navController: NavHostController = rememberNavController()
    // TODO: Create Controller and initialization
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Routes.valueOf(
        backStackEntry?.destination?.route ?: Routes.Start.name
    )
    // Create ViewModel
    val viewModel: OrderViewModel = viewModel()

    Scaffold(
        topBar = {
            AppBar(
                currentScreen = currentScreen,
                canGoBack = navController.previousBackStackEntry != null,
                navigateBack = navController::navigateUp
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = Routes.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.Start.name) {
                StartOrderScreen(
                    onStartOrderButtonClicked = { navController.navigate(Routes.EntreeMenu.name) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            composable(Routes.EntreeMenu.name) {
                EntreeMenuScreen(
                    options = DataSource.entreeMenuItems,
                    onCancelButtonClicked = {
                        Helpers.cancelOrderAndNavigateToStart(
                            viewModel,
                            navController
                        )
                    },
                    onNextButtonClicked = { navController.navigate(Routes.SideDishMenu.name) },
                    onSelectionChanged = viewModel::updateEntree
                )
            }

            composable(Routes.SideDishMenu.name) {
                SideDishMenuScreen(
                    options = DataSource.sideDishMenuItems,
                    onCancelButtonClicked = {
                        Helpers.cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    onNextButtonClicked = { navController.navigate(Routes.AccompanimentMenu.name) },
                    onSelectionChanged = viewModel::updateSideDish

                )
            }

            composable(Routes.AccompanimentMenu.name) {
                AccompanimentMenuScreen(
                    options = DataSource.accompanimentMenuItems,
                    onCancelButtonClicked = {
                        Helpers.cancelOrderAndNavigateToStart(
                            viewModel,
                            navController
                        )
                    },
                    onNextButtonClicked = { navController.navigate(Routes.Checkout.name) },
                    onSelectionChanged = viewModel::updateAccompaniment
                )
            }

            composable(Routes.Checkout.name) {
                CheckoutScreen(
                    orderUiState = uiState,
                    onNextButtonClicked = {
                        Helpers.cancelOrderAndNavigateToStart(
                            viewModel,
                            navController
                        )
                        viewModel.resetOrder()
                    },
                    onCancelButtonClicked = {
                        Helpers.cancelOrderAndNavigateToStart(
                            viewModel,
                            navController
                        )
                    })
            }
        }
    }
}
