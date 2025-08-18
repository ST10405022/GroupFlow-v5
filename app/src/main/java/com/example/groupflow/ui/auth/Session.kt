package com.example.groupflow.ui.auth

import com.example.groupflow.core.domain.User

object SessionCreation {
    var loggedInUser: User? = null
    fun loggedIn(): Boolean = loggedInUser != null

    fun loggedOut(){
        loggedInUser = null
    }

}