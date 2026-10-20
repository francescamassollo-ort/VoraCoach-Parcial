package com.example.voracoach.navigation

//Ruteo
sealed class AppDestination(val route: String) {
    data object SignIn : AppDestination("sign_in")
    data object SignUp : AppDestination("sign_up")
    data object Home : AppDestination("home")
}
