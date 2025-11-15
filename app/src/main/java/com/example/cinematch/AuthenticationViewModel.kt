package com.example.cinematch

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

data class SignUp(
    val loading: Boolean = false,
    val confirm: Boolean = false,
    val errorMessage: String? = null
)

data class SignIn(
    val loading: Boolean = false,
    val confirm: Boolean = false,
    val errorMessage: String? = null
)

data class SignOutState(
    val errorMessage: String? = null
)

data class UserModel(
    val email: String? = null,
    val username: String? = null,
    val profileImage: String? = null,
    val friends: List<String>? = null,
    val favouriteMovies: List<Movie>? = null,
    val bio: String = "WELL ITS A HAPPY DAY"
)

class AuthenticationViewModel : ViewModel() {

    val instance = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    private val _signupState = mutableStateOf(SignUp())
    val signupState: State<SignUp> = _signupState

    private val _signinState = mutableStateOf(SignIn())
    val signinState: State<SignIn> = _signinState

    private val _signOutState = mutableStateOf(SignOutState())
    val signOutState: State<SignOutState> = _signOutState


    suspend fun signUpWithEmailAndPassword(email: String, password: String) {
        _signupState.value = _signupState.value.copy(loading = true, confirm = false)

        try {
            // 🔹 1️⃣ Create Firebase Auth user
            instance.createUserWithEmailAndPassword(email, password).await()
            val user = instance.currentUser ?: throw Exception("User creation failed")

            // 🔹 2️⃣ Create a Firestore document if not exists (with all required fields)
            val userRef = firestore.collection("users").document(user.uid)
            val snapshot = userRef.get().await()

            if (!snapshot.exists()) {
                val data = mapOf(
                    "email" to user.email,
                    "username" to (user.displayName ?: email.substringBefore("@")),
                    "profileImage" to "",
                    "friends" to emptyList<String>(),
                    "favouriteMovies" to emptyList<Map<String, Any>>(), // 🟢 fixed structure
                    "bio" to "Hey there! I'm a movie fan.",
                    "joined" to FieldValue.serverTimestamp()
                )

                userRef.set(data, SetOptions.merge()).await()
                Log.d("AuthVM", "✅ Firestore user document created for ${user.email}")
            } else {
                Log.d("AuthVM", "ℹ️ User document already exists for ${user.email}")
            }

            // 🔹 3️⃣ Update UI state
            _signupState.value = _signupState.value.copy(loading = false, confirm = true)

        } catch (e: Exception) {
            _signupState.value = _signupState.value.copy(
                loading = false,
                confirm = false,
                errorMessage = e.message
            )
            Log.e("AuthVM", "❌ Sign up failed", e)
        }
    }



    suspend fun signInWithEmailAndPassword(email: String, password: String) {
        _signinState.value = _signinState.value.copy(loading = true, confirm = false)
        try {
            instance.signInWithEmailAndPassword(email, password).await()
            _signinState.value = _signinState.value.copy(loading = false, confirm = true)
        } catch (e: Exception) {
            _signinState.value = _signinState.value.copy(
                loading = false,
                confirm = false,
                errorMessage = e.message
            )
            Log.e("AuthVM", "Sign in failed", e)
        }
    }

    suspend fun savename(name : String , email: String){
        try {
            val users = instance.currentUser ?: throw Exception("User not there")

            firestore.collection("users").document(users.uid)
                .update("username",name)
        }catch (e : Exception){

        }
    }

    suspend fun isEmailRegistered(email: String): Boolean {
        return try {
            val snapshot = firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()

            !snapshot.isEmpty  //  true if email exists
        } catch (e: Exception) {
            Log.e("AuthVM", "Error checking email: ${e.message}")
            false
        }
    }



    fun signOut() {
        try {
            instance.signOut()
        } catch (e: Exception) {
            _signOutState.value = _signOutState.value.copy(errorMessage = e.message)
        }
    }

    private var _foundfriend = mutableStateOf<UserModel?>(null)
    val foundfriend : State<UserModel?> = _foundfriend

//    private var _errorfindingfriend = mutableStateOf<String?>(null)
//    val errorfindingfriend : State<String?> = _errorfindingfriend
//
//    private var _registeringfriend = mutableStateOf<String?>(null)
//    val regesteringfriend : State<String?> = _registeringfriend




    suspend fun findinguser(friendemail: String?, context: Context) {

        try {
            val user = instance.currentUser ?: throw Exception("User not there")

            val snapshot = firestore.collection("users")
                .whereEqualTo("email", friendemail)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                val friend = snapshot.first().toObject(UserModel::class.java)
                _foundfriend.value = friend
            }else{
                _foundfriend.value = null
            }
        }catch (e : Exception){
            Toast.makeText(context , "FriendEmail Not Found" , Toast.LENGTH_SHORT).show()


        }

    }


    suspend fun Makingfriend(friendemail: String, context: Context) {
        try {
            val user = instance.currentUser ?: throw Exception("User not there")

            val snapshot = firestore.collection("users")
                .whereEqualTo("email", friendemail)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                val friend = snapshot.first().toObject(UserModel::class.java)
                val friendId = snapshot.first().id

                // Add friend to current user's list
                firestore.collection("users").document(user.uid)
                    .update("friends", FieldValue.arrayUnion(friendId))
                    .await()

                // Optionally, also add the current user to friend's list (bidirectional friendship)
                firestore.collection("users").document(friendId)
                    .update("friends", FieldValue.arrayUnion(user.uid))
                    .await()

                Log.d("AuthVM", "Friend added successfully: ${friend.email}")
            } else {
                Log.w("AuthVM", "No friend found with email: $friendemail")
            }
        } catch (e: Exception) {
            Log.e("AuthVM", "Error finding friend", e)
            Toast.makeText(context , "${e.message}" , Toast.LENGTH_SHORT).show()
        }
    }

    private val _friends = mutableStateOf<List<String>>(emptyList())
    val friends: State<List<String>> = _friends

    fun loadFriends() {
        val uid = instance.currentUser?.uid ?: return
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val friendIds = document.get("friends") as? List<String> ?: emptyList()
                if (friendIds.isEmpty()) {
                    _friends.value = emptyList()
                    return@addOnSuccessListener
                }

                firestore.collection("users")
                    .whereIn(FieldPath.documentId(), friendIds)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val emails = snapshot.mapNotNull { it.getString("email") }
                        _friends.value = emails
                    }
            }
    }


    private val _favouriteMovies = mutableStateOf<List<Movie>>(emptyList())
    val favouriteMovies: State<List<Movie>> = _favouriteMovies

    private val _commonMovies = mutableStateOf<List<Movie>>(emptyList())
    val commonMovies: State<List<Movie>> = _commonMovies


    // 🟢 Toggle Favourite Movie
    fun toggleFavorite(movie: Movie) {
        val user = instance.currentUser ?: return
        val userRef = firestore.collection("users").document(user.uid)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val favs = (snapshot.get("favouriteMovies") as? List<Map<String, Any>>)?.toMutableList() ?: mutableListOf()


            // check if this movie already exists
            val existingIndex = favs.indexOfFirst { it["id"] == movie.id }

            if (existingIndex != -1) {
                // 🗑️ Movie already exists → remove it
                favs.removeAt(existingIndex)
                transaction.update(userRef, "favouriteMovies", favs)
            } else {
                // ✅ Add full movie object
                val movieMap = mapOf(
                    "adult" to movie.adult,
                    "backdrop_path" to movie.backdrop_path,
                    "genre_ids" to movie.genre_ids,
                    "id" to movie.id,
                    "original_language" to movie.original_language,
                    "original_title" to movie.original_title,
                    "overview" to movie.overview,
                    "popularity" to movie.popularity,
                    "poster_path" to movie.poster_path,
                    "release_date" to movie.release_date,
                    "title" to movie.title,
                    "video" to movie.video,
                    "vote_average" to movie.vote_average,
                    "vote_count" to movie.vote_count
                )
                favs.add(movieMap as Map<String, Any>)
                transaction.update(userRef, "favouriteMovies", favs)
            }
        }.addOnSuccessListener {
            Log.d("Firestore", "Toggled favorite for ${movie.title}")
        }.addOnFailureListener {
            Log.e("Firestore", "Failed to toggle favorite", it)
        }
    }



    // 🟢 Load User’s Favourite Movies
    fun loadFavourites() {
        val uid = instance.currentUser?.uid ?: return
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val list = doc["favouriteMovies"] as? List<Map<String, Any>> ?: emptyList()
                val movies = list.map { map ->
                    Movie(
                        adult = map["adult"] as? Boolean ?: false,
                        backdrop_path = map["backdrop_path"] as? String,
                        genre_ids = (map["genre_ids"] as? List<Int>) ?: emptyList(),
                        id = (map["id"] as? Long)?.toInt() ?: 0,
                        original_language = map["original_language"] as? String ?: "",
                        original_title = map["original_title"] as? String ?: "",
                        overview = map["overview"] as? String ?: "",
                        popularity = (map["popularity"] as? Number)?.toDouble() ?: 0.0,
                        poster_path = map["poster_path"] as? String,
                        release_date = map["release_date"] as? String ?: "",
                        title = map["title"] as? String ?: "",
                        video = map["video"] as? Boolean ?: false,
                        vote_average = (map["vote_average"] as? Number)?.toDouble() ?: 0.0,
                        vote_count = (map["vote_count"] as? Long)?.toInt() ?: 0
                    )
                }
                _favouriteMovies.value = movies
            }
            .addOnFailureListener {
                _favouriteMovies.value = emptyList()
            }
    }


    // 🟢 Load Common Movies with Friends
    fun loadCommonMoviesWithFriends() {
        val uid = instance.currentUser?.uid ?: return
        val usersRef = firestore.collection("users")

        usersRef.document(uid).get().addOnSuccessListener { doc ->
            val friends = doc.get("friends") as? List<String> ?: emptyList()
            val myFavs = doc.get("favouriteMovies") as? List<Map<String, Any>> ?: emptyList()

            if (friends.isEmpty()) {
                _commonMovies.value = emptyList()
                return@addOnSuccessListener
            }

            usersRef.whereIn(FieldPath.documentId(), friends)
                .get()
                .addOnSuccessListener { friendsDocs ->
                    val allFriendsFavs = friendsDocs.flatMap {
                        it.get("favouriteMovies") as? List<Map<String, Any>> ?: emptyList()
                    }

                    val myMovieIds = myFavs.mapNotNull { (it["id"] as? Long)?.toInt() }
                    val common = allFriendsFavs.filter {
                        val id = (it["id"] as? Long)?.toInt()
                        id != null && myMovieIds.contains(id)
                    }

                    val movies = common.map { map ->
                        Movie(
                            adult = map["adult"] as? Boolean ?: false,
                            backdrop_path = map["backdrop_path"] as? String,
                            genre_ids = (map["genre_ids"] as? List<Int>) ?: emptyList(),
                            id = (map["id"] as? Long)?.toInt() ?: 0,
                            original_language = map["original_language"] as? String ?: "",
                            original_title = map["original_title"] as? String ?: "",
                            overview = map["overview"] as? String ?: "",
                            popularity = (map["popularity"] as? Number)?.toDouble() ?: 0.0,
                            poster_path = map["poster_path"] as? String,
                            release_date = map["release_date"] as? String ?: "",
                            title = map["title"] as? String ?: "",
                            video = map["video"] as? Boolean ?: false,
                            vote_average = (map["vote_average"] as? Number)?.toDouble() ?: 0.0,
                            vote_count = (map["vote_count"] as? Long)?.toInt() ?: 0
                        )
                    }
                    _commonMovies.value = movies
                    Log.d("Firestore", "Common movies: ${movies.size}")
                }
        }
    }

    private val _route = mutableStateOf<String>(Screens.GettingStarted.route)
    val route: State<String> = _route

    suspend fun loggingin() {
        delay(1900) // give Firebase 1 second to initialize
        if (instance.currentUser != null) {
            _route.value = Screens.Home.route
        } else {
            _route.value = Screens.GettingStarted.route
        }
    }


}



