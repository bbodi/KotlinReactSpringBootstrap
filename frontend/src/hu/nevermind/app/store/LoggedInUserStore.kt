package hu.nevermind.app.store

import com.github.andrewoma.flux.Store
import hu.nevermind.app.Actions
import hu.nevermind.app.globalDispatcher

enum class Role {
    Admin, User
}

data class LoggedInUser(
        val name: String,
        val roles: Array<Role>)
object LoggedInUserStore : Store() {

    private var loggedInUser: LoggedInUser? = null

    init {
        register(globalDispatcher, Actions.setLoggedInuser) { newLoggedInUser ->
            loggedInUser = newLoggedInUser
            emitChange()
        }

    }

    fun hasRole(role: Role) : Boolean {
        return loggedInUser!!.roles.contains(role)
    }

    fun hasAnyRoles(vararg roles: Role) : Boolean {
        return roles.any {
            loggedInUser!!.roles.contains(it)
        }
    }

    fun hasAllRoles(vararg roles: Role) : Boolean{
        return roles.all {
            loggedInUser!!.roles.contains(it)
        }
    }

    val isLoggedIn: Boolean
        get() = loggedInUser != null
    val username: String
        get() = loggedInUser!!.name
    val roles: Array<Role>
        get() = loggedInUser!!.roles
}
