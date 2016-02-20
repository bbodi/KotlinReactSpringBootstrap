package hu.nevermind.app.store

import com.github.andrewoma.flux.Store
import hu.nevermind.app.Actions
import hu.nevermind.app.RestUrl
import hu.nevermind.app.communicator
import hu.nevermind.app.globalDispatcher

enum class Role {
    ROLE_ADMIN, ROLE_USER
}

data class Account(var username: String = "",
                   var disabled: Boolean = false,
                   var roles: List<Role>,
                   var plainPassword: String) {
    fun hasRole(role: Role) : Boolean {
        return roles.contains(role)
    }

    fun hasAnyRoles(vararg roles: Role) : Boolean {
        return roles.any {
            roles.contains(it)
        }
    }

    fun hasAllRoles(vararg roles: Role) : Boolean{
        return roles.all {
            roles.contains(it)
        }
    }
}

object AccountStore : Store() {

    private var accounts: MutableList<Account> = arrayListOf()
    var editingAccount: Account? = null
        private set


    init {
        register(globalDispatcher, Actions.setLoggedInUser) { loggedInUser->
            if (loggedInUser == null) {
                accounts = arrayListOf()
            } else {
                communicator.getEntitiesFromServer(RestUrl.getAccountsFromServer) { returnedArray ->
                    val newAccounts = returnedArray.map {
                        with(it.asDynamic()) {
                            Account(username, disabled, roles, "")
                        }
                    }.toTypedArray()
                    accounts = newAccounts.toArrayList()
                }
            }
            emitChange()
        }
        register(globalDispatcher, Actions.setEditingAccount) { account ->
            if (account != editingAccount) {
                editingAccount = account
                emitChange()
            }
        }
        register(globalDispatcher, Actions.modifyAccount) { modifiedAccount ->
            communicator.saveEntity(RestUrl.saveAccount, modifiedAccount) {
                val index = accounts.indexOfFirst { it.username == modifiedAccount.username }
                if (index == -1) {
                    accounts.add(modifiedAccount)
                } else {
                    accounts[index] = modifiedAccount
                }
                emitChange()
            }
        }
    }

    fun accounts(): List<Account> = accounts

    fun account(username: String): Account? {
        return accounts().firstOrNull { it.username == username.orEmpty() }
    }
}
