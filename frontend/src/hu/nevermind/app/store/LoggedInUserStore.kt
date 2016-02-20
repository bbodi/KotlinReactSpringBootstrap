package hu.nevermind.app.store

import com.github.andrewoma.flux.Store
import hu.nevermind.app.Actions
import hu.nevermind.app.globalDispatcher

object LoggedInUserStore : Store() {

    private var maybeLoggedInUser: Account? = null

    init {
        register(globalDispatcher, Actions.setLoggedInUser) { newLoggedInUser ->
            maybeLoggedInUser = newLoggedInUser
            emitChange()
        }

    }

    val isLoggedIn: Boolean
        get() = maybeLoggedInUser != null

    val loggedInUser: Account
        get() = maybeLoggedInUser!!
}
