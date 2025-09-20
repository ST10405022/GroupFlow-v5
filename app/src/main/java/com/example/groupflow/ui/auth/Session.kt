package com.example.groupflow.ui.auth

import android.content.Context
import com.example.groupflow.core.domain.Employee
import com.example.groupflow.core.domain.Patient
import com.example.groupflow.core.domain.Role
import com.example.groupflow.core.domain.User

object SessionCreation {
    private const val PREFERENCES_NAME = "user_session"

    private const val KEY_ID = "id"
    private const val KEY_NAME = "name"
    private const val KEY_EMAIL = "email"
    private const val KEY_ROLE = "role"

    /**
     * Saves the logged-in user's data into Shared Preferences
     * **/

    fun saveUser(
        context: Context,
        user: User,
    ) {
        val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE) // (Android Developers, 2025)
        with(prefs.edit()) {
            putString(KEY_ID, user.id)
            putString(KEY_NAME, user.name)
            putString(KEY_EMAIL, user.email)
            putString(KEY_ROLE, user.role.name)
            apply()
        }
    }

    fun getUser(context: Context): User? {
        val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val id = prefs.getString(KEY_ID, null) ?: return null
        val name = prefs.getString(KEY_NAME, null) ?: return null
        val email = prefs.getString(KEY_EMAIL, null) ?: return null
        val role = prefs.getString(KEY_ROLE, null)?.let { Role.valueOf(it) } ?: return null

        return when (role) {
            Role.PATIENT -> Patient(id = id, name = name, email = email, role = role)
            Role.EMPLOYEE -> Employee(id = id, name = name, email = email, role = role)
        }
    }

    /**
     * Checks if the current user is logged in
     * **/

    fun loggedIn(context: Context): Boolean = getUser(context) != null

    /**
     * Clears the user data in SharedPreferences
     * **/

    fun logout(context: Context) {
        val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}

/**     Reference List
 *          Android Developers. 2025. Save simple data with SharedPreferences. [Online]. Available at: https://developer.android.com/training/data-storage/shared-preferences [Accessed on 25 August 2025]
 *  **/
