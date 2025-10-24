package com.example.gamezone30.navegation

import com.example.gamezone30.navigation.AppScreens

sealed class NavegationEvent {
    data class NavegateTo(
        val route: AppScreens,
        val popUpToRoute : AppScreens? = null,
        val inclusive : Boolean = false,
        val singleTop : Boolean = false
    ): NavegationEvent()
    object PopBackStack : NavegationEvent()

    object NavegateUp : NavegationEvent()

}