package hu.nevermind.app


import com.github.andrewoma.flux.Dispatcher
import com.github.andrewoma.react.*
import hu.nevermind.app.screen.accountScreen
import hu.nevermind.app.screen.keyValueScreen
import hu.nevermind.app.screen.loginScreen
import hu.nevermind.app.store.*
import hu.nevermind.common.*
import hu.nevermind.reakt.bootstrap.*
import hu.nevermind.common.get
import hu.nevermind.common.hide
import jquery.jq
import kotlin.browser.window

@native private val QUnit: dynamic = noImpl


val globalDispatcher = Dispatcher()

var communicator: Communicator
var testAjaxPoster: TestAjaxPoster

public fun main(vararg arg: String) {
    QUnit.config.autostart = false

    if (window.location.search.contains("tests")) {
        testAjaxPoster = TestAjaxPoster()
        communicator = Communicator(testAjaxPoster)
        testAjaxPoster.pushResult<Nothing>(RestUrl.authenticate, {ok(object{val name="testUser";val roles=arrayOf(Role.ROLE_ADMIN)})})
        testAjaxPoster.pushResult<Nothing>(RestUrl.getKeyValuesFromServer, {ok(emptyArray<Any>())})
        testAjaxPoster.pushResult<Nothing>(RestUrl.getAccountsFromServer, {ok(emptyArray<Any>())})
        testAjaxPoster.pushResult(RestUrl.saveKeyValue, {result: KeyValue -> ok(result)})
        testAjaxPoster.pushResult<Nothing>(RestUrl.deleteKeyValue, {ok("")})
        react.render(app(), jq("#app").get(0)!!)
        QUnit.start()
    } else {
        communicator = Communicator(JqueryAjaxPoster())
        jq("#qunit").hide()
        jq("#qunit-fixture").hide()
        react.render(app(), jq("#app").get(0)!!)
    }
}

object NavMenuIds {
    const val root = "navItem"
    const val account = "${root}_account"
    const val keyValue = "${root}_keyValue"
}

enum class AppScreen {
    Login,
    Home,

    Config, Account
}

data class AppState(val screen: AppScreen)

class App : ComponentSpec<Unit, AppState>() {
    companion object {
        val factory = react.createFactory(App())
    }

    private fun route() {
        if (!LoggedInUserStore.isLoggedIn) {
            window.location.hash = Path.login
        } else {
            RouterStore.match(
                    "${Path.login}" to { params ->
                        state = AppState(AppScreen.Login)
                    },
                    "${Path.keyValue.root}?editedKeyValueId" to { params ->
                        if (state.screen != AppScreen.Config) {
                            state = AppState(AppScreen.Config)
                        }
                        val keyValue = KeyValueStore.keyValue(params["editedKeyValueId"].orEmpty())
                        Actions.setEditingKeyValue(globalDispatcher, keyValue)
                    },
                    "${Path.account.root}?editedAccountId" to { params ->
                        if (state.screen != AppScreen.Account) {
                            state = AppState(AppScreen.Account)
                        }
                        val account = AccountStore.account(params["editedAccountId"].orEmpty())
                        if (account != null) {
                            Actions.setEditingAccount(globalDispatcher, EditingAccount(account, false))
                        } else {
                            Actions.setEditingAccount(globalDispatcher, null)
                        }
                    },
                    otherwise = {
                        state = AppState(AppScreen.Home)
                    }
            )
        }
    }

    override fun componentDidMount() {
        authenticate()
        route()
        RouterStore.addChangeListener(this) {
            route()
        }
    }

    private fun authenticate() {
        communicator.authenticate() { result ->
            if (result.ok != null) {
                val principal = result.ok.asDynamic()
                Actions.setLoggedInUser(globalDispatcher, Account(principal.name, false, Role.valueOf(principal.role), ""))
            } else {
                Actions.setLoggedInUser(globalDispatcher, null)
            }
        }
    }

    override fun initialState(): AppState? {
        return AppState(AppScreen.Login)
    }

    override fun Component.render() {
        div({ className = "" }) {
            bsNavbar() {
                bsNavbarHeader() {
                    bsNavbarBrand() {
                        a({ href = nullHref }) { text(homeScreenMsg.title) }
                    }
                }
                if (state.screen != AppScreen.Login) {
                    bsNav {
                        if (LoggedInUserStore.loggedInUser.role == Role.ROLE_ADMIN) {
                            bsNavDropdown({ title = homeScreenMsg.menuAdmin }) {
                                bsMenuItem({
                                    id = NavMenuIds.account
                                    href = "#${Path.account.root}"
                                    active = state.screen == AppScreen.Account
                                }) { text(homeScreenMsg.menuAccounts) }
                            }
                        }
                        bsNavDropdown({ title = homeScreenMsg.menuOptions }) {
                            bsMenuItem({
                                id = NavMenuIds.keyValue
                                href = "#${Path.keyValue.root}"
                                active = state.screen == AppScreen.Config
                            }) { text(homeScreenMsg.menuKeyValue) }
                        }
                    }
                }
                if (state.screen != AppScreen.Login) {
                    bsNav({ pullRight = true }) {
                        bsNavItem {
                            text("${LoggedInUserStore.loggedInUser.username}")
                            text("(${LoggedInUserStore.loggedInUser.role})")
                        }
                        bsNavItem({
                            eventKey = 2
                            href = "/logout"
                            onClick = { Actions.setLoggedInUser(globalDispatcher, null) }
                        }) {
                            text(homeScreenMsg.logout)
                        }
                    }
                } else {
                    bsNav({ pullRight = true }) {
                        bsNavItem({ eventKey = 2; href = "/" }) { text(homeScreenMsg.contact) }
                    }
                }
            }
            bsGrid({ fluid = true }) {
                bsRow() {
                    bsCol({ md = 12 }) {
                        if (state.screen == AppScreen.Config) {
                            keyValueScreen()
                        } else if (state.screen == AppScreen.Account) {
                                accountScreen()
                        } else if (state.screen == AppScreen.Login) {
                            loginScreen()
                        }
                    }
                }
            }
        }
    }
}

fun app() = App.factory(Ref(null))

interface ValidationRule {
    val errorMsg: String

    fun hasValidationError(value: String): Boolean
}

data class EmptyOr(val orRule: ValidationRule) : ValidationRule {

    override fun hasValidationError(value: String): Boolean {
        return value.isNotEmpty() && orRule.hasValidationError(value)
    }

    override val errorMsg: String = "Must be empty or ${orRule.errorMsg}"

}

data class Min(val length: Int) : ValidationRule {

    override val errorMsg: String = "Must longer than $length"

    override fun hasValidationError(value: String): Boolean {
        return value.size < length
    }
}

data class Max(val length: Int) : ValidationRule {
    override val errorMsg: String = "Must shorter than $length"

    override fun hasValidationError(value: String): Boolean {
        return value.size > length
    }
}

fun validate(value: String, vararg rules: ValidationRule): List<String> {
    val errorMessages = arrayListOf<String>()
    rules.forEach {
        if (it.hasValidationError(value)) {
            errorMessages.add(it.errorMsg)
        }
    }
    return errorMessages
}