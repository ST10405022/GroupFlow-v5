package com.example.groupflow.data.auth

import android.content.Context
import android.se.omapi.Session
import com.example.groupflow.core.domain.*
import com.example.groupflow.core.service.AuthenticationService
import com.example.groupflow.ui.auth.SessionCreation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FirebaseAuthAdapter(
    private val context: Context) : AuthenticationService {

    private val auth = FirebaseAuth.getInstance()
    private val usersRef = FirebaseDatabase.getInstance().getReference("users")

    /**
     * Registers a new user with the provided email, password, display name, and role.
     * @param email The user's email address.
     * @param password The user's password.
     * @param displayName The user's display name.
     * @param role The user's role (PATIENT or EMPLOYEE).
     * @return A [Result] indicating the success or failure of the registration operation.
     * If successful, the [Result] contains the user's UID.
     * If unsuccessful, the [Result] contains an [Exception] describing the error.
     * @throws Exception If there is an error during the registration process.
     * @see Result
     */
    override suspend fun register(email: String, password: String, displayName: String, role: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw IllegalStateException("No UID returned from Firebase")

            val roleEnum = Role.valueOf(role.uppercase())
            val userObject: User = when (roleEnum) {
                Role.PATIENT -> Patient(id = uid, name = displayName, email = email, role = Role.PATIENT)
                Role.EMPLOYEE -> Employee(id = uid, name = displayName, email = email, role = Role.EMPLOYEE)
            }

            usersRef.child(uid).setValue(userObject).await()

            // Save user session after registration
            SessionCreation.saveUser(context, userObject)

            Result.success(uid)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    /**
     * Logs in a user with the provided email and password.
     * @param email The user's email address.
     * @param password The user's password.
     * @return A [Result] indicating the success or failure of the login operation.
     * If successful, the [Result] contains the user's UID.
     * If unsuccessful, the [Result] contains an [Exception] describing the error.
     * @throws Exception If there is an error during the login process.
     * @see Result
     */
    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw IllegalStateException("No UID")

            //Save session and fetch user profile
            val snapshot = usersRef.child(uid).get().await()
            val role = snapshot.child("role").getValue(String::class.java)?.uppercase()
            val user: User? = when (role){
                "PATIENT" -> snapshot.getValue(Patient::class.java)
                "EMPLOYEE" -> snapshot.getValue(Employee::class.java)
                else -> null
            }

            if (user != null)
            {
                SessionCreation.saveUser(context, user)     // session persistence
                Result.success(uid)
            }
            else
            {
                Result.failure(IllegalStateException("Unable to find user profile"))
            }

        }
        catch (ex: Exception)
        {
            Result.failure(ex)
        }
    }

    /**
     * Logs out the current user.
     * @throws Exception If there is an error during the logout process.
     * @see Exception
     * @see FirebaseAuth.signOut
     */
    override fun logout() {
        auth.signOut()
        SessionCreation.logout(context)     // Clear the active session
    }

    /**
     * Retrieves the current user's UID.
     * @return The UID of the current user, or null if no user is logged in.
     * @see FirebaseAuth.getUid
     */
    override fun currentUserId(): String? = auth.currentUser?.uid

    /**
     * Fetches the current logged-in user's profile from Firebase Realtime Database.
     * @return A [Result] containing the [User] object if successful, or an [Exception] if unsuccessful.
     * @throws Exception If there is an error fetching the user profile.
     * @see Result
     */
    override suspend fun getCurrentUserProfile(): Result<User> {
        val uid = currentUserId() ?: return Result.failure(IllegalStateException("No user logged in"))

        return try {
            val snapshot = usersRef.child(uid).get().await()
            val role = snapshot.child("role").getValue(String::class.java)?.uppercase()

            // Convert the snapshot to the appropriate User object based on the role
            val user: User? = when (role) {
                "PATIENT" -> snapshot.getValue(Patient::class.java)
                "EMPLOYEE" -> snapshot.getValue(Employee::class.java)
                else -> null
            }

            // Check if the user object is not null before returning it
            if (user != null) {
                SessionCreation.saveUser(context, user)     // maintain session synchronization
                Result.success(user)
            } else {
                Result.failure(IllegalStateException("User data not found or invalid"))
            }
        } catch (ex: Exception) { // Handle exceptions
            Result.failure(ex)
        }
    }
}
