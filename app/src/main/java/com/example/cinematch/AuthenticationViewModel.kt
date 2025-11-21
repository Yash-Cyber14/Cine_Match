package com.example.cinematch



import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import android.util.Log

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


    // -------------------------------------------------------------------------
    // SIGNUP
    // -------------------------------------------------------------------------
    suspend fun signUpWithEmailAndPassword(email: String, password: String) {
        val cleanEmail = email.trim().lowercase()

        _signupState.value = _signupState.value.copy(loading = true, confirm = false)

        try {
            instance.createUserWithEmailAndPassword(cleanEmail, password).await()
            val user = instance.currentUser ?: throw Exception("User creation failed")

            val data = mapOf(
                "email" to cleanEmail,
                "username" to cleanEmail.substringBefore("@"),
                "profileImage" to "",
                "friends" to emptyList<String>(),
                "favouriteMovies" to emptyList<Map<String, Any>>(),
                "bio" to "Hey there! I'm a movie fan."
            )

            firestore.collection("users").document(user.uid)
                .set(data, SetOptions.merge()).await()

            _signupState.value = SignUp(loading = false, confirm = true)

        } catch (e: Exception) {
            _signupState.value = SignUp(loading = false, confirm = false, errorMessage = e.message)
        }
    }



    // -------------------------------------------------------------------------
    // SIGNIN
    // -------------------------------------------------------------------------
    suspend fun signInWithEmailAndPassword(email: String, password: String) {
        _signinState.value = SignIn(loading = true)
        try {
            instance.signInWithEmailAndPassword(email.trim().lowercase(), password).await()
            _signinState.value = SignIn(loading = false, confirm = true)
        } catch (e: Exception) {
            _signinState.value = SignIn(loading = false, confirm = false, errorMessage = e.message)
        }
    }


    // -------------------------------------------------------------------------
    // SAVE NAME
    // -------------------------------------------------------------------------
    suspend fun savename(name: String, email: String) {
        try {
            val user = instance.currentUser ?: return
            firestore.collection("users").document(user.uid)
                .update("username", name).await()
        } catch (_: Exception) { }
    }


    // -------------------------------------------------------------------------
    // CHECK IF EMAIL EXISTS
    // -------------------------------------------------------------------------
    suspend fun isEmailRegistered(email: String): Boolean {
        return try {
            val snapshot = firestore.collection("users")
                .whereEqualTo("email", email.trim().lowercase())
                .get().await()

            !snapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }


    // -------------------------------------------------------------------------
    // SIGNOUT
    // -------------------------------------------------------------------------
    fun signOut() {
        try {
            instance.signOut()
        } catch (e: Exception) {
            _signOutState.value = SignOutState(errorMessage = e.message)
        }
    }


    // -------------------------------------------------------------------------
    // FIND FRIEND BY EMAIL
    // -------------------------------------------------------------------------
    private val _foundfriend = mutableStateOf<UserModel?>(null)
    val foundfriend: State<UserModel?> = _foundfriend

    suspend fun findinguser(friendemail: String?, context: Context) {
        try {
            val snapshot = firestore.collection("users")
                .whereEqualTo("email", friendemail?.trim()?.lowercase())
                .get().await()

            _foundfriend.value =
                if (!snapshot.isEmpty) snapshot.first().toObject(UserModel::class.java)
                else null

        } catch (e: Exception) {
            Toast.makeText(context, "Friend not found", Toast.LENGTH_SHORT).show()
        }
    }



    // -------------------------------------------------------------------------
    // FIND FRIEND BY UID
    // -------------------------------------------------------------------------
    suspend fun findinguserByUid(uid: String?, context: Context) {
        if (uid.isNullOrBlank()) return

        try {
            val snapshot = firestore.collection("users").document(uid).get().await()

            _foundfriend.value =
                if (snapshot.exists()) snapshot.toObject(UserModel::class.java)
                else null

        } catch (_: Exception) {
            Toast.makeText(context, "Friend not found", Toast.LENGTH_SHORT).show()
        }
    }


    // -------------------------------------------------------------------------
    // FIND UID BY EMAIL
    // -------------------------------------------------------------------------
    suspend fun findinguserByEmail(email: String, context: Context): String? {
        return try {
            val snapshot = firestore.collection("users")
                .whereEqualTo("email", email.trim().lowercase())
                .get().await()

            if (!snapshot.isEmpty) snapshot.first().id else null

        } catch (e: Exception) {
            Toast.makeText(context, "Friend not found", Toast.LENGTH_SHORT).show()
            null
        }
    }



    // -------------------------------------------------------------------------
    // ADD FRIEND
    // -------------------------------------------------------------------------
    suspend fun Makingfriend(friendUid: String, context: Context) {
        try {
            val user = instance.currentUser ?: return
            val currentUid = user.uid

            firestore.collection("users").document(currentUid)
                .update("friends", FieldValue.arrayUnion(friendUid)).await()

            firestore.collection("users").document(friendUid)
                .update("friends", FieldValue.arrayUnion(currentUid)).await()

        } catch (_: Exception) {
            Toast.makeText(context, "Error adding friend", Toast.LENGTH_SHORT).show()
        }
    }


    // -------------------------------------------------------------------------
    // LOAD FRIENDS
    // -------------------------------------------------------------------------
    private val _friends = mutableStateOf<List<String>>(emptyList())
    val friends: State<List<String>> = _friends

    fun loadFriends() {
        val uid = instance.currentUser?.uid ?: return

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val friendIds = doc.get("friends") as? List<String> ?: emptyList()

                if (friendIds.isEmpty()) {
                    _friends.value = emptyList()
                    return@addOnSuccessListener
                }

                firestore.collection("users")
                    .whereIn(FieldPath.documentId(), friendIds)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        _friends.value = snapshot.mapNotNull { it.getString("email") }
                    }
            }
    }



    // -------------------------------------------------------------------------
    // LOAD FAVOURITE MOVIES
    // -------------------------------------------------------------------------
    private val _favouriteMovies = mutableStateOf<List<Movie>>(emptyList())
    val favouriteMovies: State<List<Movie>> = _favouriteMovies

    fun loadFavourites() {
        val uid = instance.currentUser?.uid ?: return

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val list = doc["favouriteMovies"] as? List<Map<String, Any>> ?: emptyList()

                _favouriteMovies.value = list.map { map ->
                    Movie(
                        adult = map["adult"] as? Boolean ?: false,
                        backdrop_path = map["backdrop_path"] as? String,
                        genre_ids = map["genre_ids"] as? List<Int> ?: emptyList(),
                        id = (map["id"] as? Number)?.toInt() ?: 0,
                        original_language = map["original_language"] as? String ?: "",
                        original_title = map["original_title"] as? String ?: "",
                        overview = map["overview"] as? String ?: "",
                        popularity = (map["popularity"] as? Number)?.toDouble() ?: 0.0,
                        poster_path = map["poster_path"] as? String,
                        release_date = map["release_date"] as? String ?: "",
                        title = map["title"] as? String ?: "",
                        video = map["video"] as? Boolean ?: false,
                        vote_average = (map["vote_average"] as? Number)?.toDouble() ?: 0.0,
                        vote_count = (map["vote_count"] as? Number)?.toInt() ?: 0
                    )
                }
            }
    }



    // -------------------------------------------------------------------------
    // ⭐️⭐️ TOGGLE FAVOURITE MOVIE (ADD / REMOVE) ⭐️⭐️
    // -------------------------------------------------------------------------
    suspend fun toggleFavouriteMovie(movie: Movie, context: Context) {
        val uid = instance.currentUser?.uid ?: return
        val userRef = firestore.collection("users").document(uid)

        try {
            val snapshot = userRef.get().await()
            val currentList = snapshot["favouriteMovies"] as? List<Map<String, Any>> ?: emptyList()

            val movieId = movie.id
            val exists = currentList.any { (it["id"] as? Number)?.toInt() == movieId }

            if (exists) {
                // remove
                userRef.update(
                    "favouriteMovies",
                    FieldValue.arrayRemove(movie.toMap())
                ).await()
            } else {
                // add
                userRef.update(
                    "favouriteMovies",
                    FieldValue.arrayUnion(movie.toMap())
                ).await()
            }

            loadFavourites()

        } catch (e: Exception) {
            Toast.makeText(context, "Error updating favourites", Toast.LENGTH_SHORT).show()
        }
    }


    // Convert movie object to Firestore map
    private fun Movie.toMap(): Map<String, Any?> {
        return mapOf(
            "adult" to adult,
            "backdrop_path" to backdrop_path,
            "genre_ids" to genre_ids,
            "id" to id,
            "original_language" to original_language,
            "original_title" to original_title,
            "overview" to overview,
            "popularity" to popularity,
            "poster_path" to poster_path,
            "release_date" to release_date,
            "title" to title,
            "video" to video,
            "vote_average" to vote_average,
            "vote_count" to vote_count
        )
    }



    // -------------------------------------------------------------------------
    // COMMON MOVIES
    // -------------------------------------------------------------------------
    private val _commonMovies = mutableStateOf<List<Movie>>(emptyList())
    val commonMovies: State<List<Movie>> = _commonMovies

    fun loadCommonMoviesWithFriends() {
        val uid = instance.currentUser?.uid ?: return
        val usersRef = firestore.collection("users")

        usersRef.document(uid).get().addOnSuccessListener { doc ->
            val friends = doc.get("friends") as? List<String> ?: emptyList()
            val myFavs = doc.get("favouriteMovies") as? List<Map<String, Any>> ?: emptyList()

            if (friends.isEmpty()) return@addOnSuccessListener

            usersRef.whereIn(FieldPath.documentId(), friends).get()
                .addOnSuccessListener { friendsDocs ->
                    val allFriendsFavs = friendsDocs.flatMap {
                        it.get("favouriteMovies") as? List<Map<String, Any>> ?: emptyList()
                    }

                    val myMovieIds = myFavs.mapNotNull { (it["id"] as? Number)?.toInt() }

                    val common = allFriendsFavs.filter {
                        val id = (it["id"] as? Number)?.toInt()
                        id != null && myMovieIds.contains(id)
                    }

                    _commonMovies.value = common.map { map ->
                        Movie(
                            adult = map["adult"] as? Boolean ?: false,
                            backdrop_path = map["backdrop_path"] as? String,
                            genre_ids = map["genre_ids"] as? List<Int> ?: emptyList(),
                            id = (map["id"] as? Number)?.toInt() ?: 0,
                            original_language = map["original_language"] as? String ?: "",
                            original_title = map["original_title"] as? String ?: "",
                            overview = map["overview"] as? String ?: "",
                            popularity = (map["popularity"] as? Number)?.toDouble() ?: 0.0,
                            poster_path = map["poster_path"] as? String,
                            release_date = map["release_date"] as? String ?: "",
                            title = map["title"] as? String ?: "",
                            video = map["video"] as? Boolean ?: false,
                            vote_average = (map["vote_average"] as? Number)?.toDouble() ?: 0.0,
                            vote_count = (map["vote_count"] as? Number)?.toInt() ?: 0
                        )
                    }
                }
        }
    }



    // -------------------------------------------------------------------------
    // SPLASH LOGIN CHECK
    // -------------------------------------------------------------------------
    private val _route = mutableStateOf(Screens.GettingStarted.route)
    val route: State<String> = _route

    suspend fun loggingin() {
        delay(1500)
        _route.value =
            if (instance.currentUser != null) Screens.Home.route
            else Screens.GettingStarted.route
    }
}





