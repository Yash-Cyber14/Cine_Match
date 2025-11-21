package com.example.cinematch

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

sealed class Screens(val route : String){
    object Home : Screens("Home")
    object SearchScreen : Screens("SearchScreen")
    object SignIn : Screens("SignIn")
    object SignUp : Screens("SignUp")
    object GettingStarted : Screens("GettingStarted")

    object StackCard : Screens("StackCard")

    object ProfileScreen : Screens("Profile")

    object ActionCardforfriends : Screens("Confirmfriend")

    object LoggingScreen : Screens("LoggingIn")

    object GenerativeAi : Screens("GenerativeAi")

    object VideoPlayer : Screens("Videoplayer")
}

@Composable
fun Navigation() {

    val viewModel : MainViewModel = hiltViewModel()
    val AuthviewModel : AuthenticationViewModel = viewModel()
    val aiviewModel : ChatViewModel = hiltViewModel()

    val navcontroller = rememberNavController()

    NavHost(navController = navcontroller , startDestination = Screens.LoggingScreen.route) {

        composable(route = Screens.GettingStarted.route){
            GettingStartedScreen(navcontroller)
        }
        composable(route = Screens.SignUp.route){
            SignUpScreen(navcontroller , AuthviewModel)
        }
        composable(route = Screens.SignIn.route){
            SignInScreen(navcontroller , AuthviewModel)
        }
        composable(route = Screens.Home.route){
            HomeScreen(navcontroller , viewModel , AuthviewModel)
        }

        composable(route = Screens.SearchScreen.route){
            MoviesLibraryScreen(navcontroller , viewModel)
        }

        composable(
            route = "${Screens.StackCard.route}/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val movieid = backStackEntry.arguments?.getInt("id") ?: 0

            StackCardScreen(
                id = movieid,
                moviestyperesponse = viewModel.selectedMoviesResponse,
                navController = navcontroller,
                viewModel = AuthviewModel,
                viewModel1 = viewModel
            )
        }

        composable(route = Screens.ProfileScreen.route )
        {
            ProfileScreen(navcontroller , AuthviewModel)
        }
        composable(
            "${Screens.ActionCardforfriends.route}/{friendUid}",
            arguments = listOf(navArgument("friendUid") { type = NavType.StringType })
        ) { backStackEntry ->
            val friendUid = backStackEntry.arguments?.getString("friendUid") ?: ""
            ActionCardforFriends(viewModel = AuthviewModel, navController = navcontroller, friendUid = friendUid)
        }


        composable(Screens.LoggingScreen.route){
            loggingScreen(AuthviewModel , navcontroller)
        }

        composable("${Screens.VideoPlayer.route}/{movieId}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
            VideoPlayerScreen(
                movieId = movieId,
                navController = navcontroller,
                viewModel = viewModel
            )
        }

        composable(route = Screens.GenerativeAi.route ){
            GenerativeAiScreen(aiViewModel = aiviewModel , navcontroller , viewModel)
        }




    }




}