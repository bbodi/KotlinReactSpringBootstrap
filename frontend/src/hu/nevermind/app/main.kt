package hu.nevermind.app


import com.github.andrewoma.flux.Dispatcher
import com.github.andrewoma.react.*
import hu.nevermind.app.keyvalue.KeyValueStore
import hu.nevermind.app.keyvalue.keyValueScreen
import hu.nevermind.app.keyvalue.loginScreen
import hu.nevermind.common.*
import hu.nevermind.reakt.bootstrap.*
import hu.nevermind.reakt.jqext.get
import hu.nevermind.reakt.jqext.hide
import hu.nevermind.reakt.jqext.size
import jquery.jq
import org.junit.Test
import kotlin.browser.window

@native private val QUnit: dynamic = noImpl


val globalDispatcher = Dispatcher()

var communicator: Communicator

public fun main(vararg arg: String) {
    QUnit.config.autostart = false
    react.render(app(), jq("#app").get(0)!!)
    if (window.location.search.contains("tests")) {
        window.location.hash = Path.root
        communicator = Communicator(TestAjaxPoster())
        QUnit.start()
    } else {
        communicator = Communicator(JqueryAjaxPoster())
        jq("#qunit").hide()
        jq("#qunit-fixture").hide()
        communicator.getKeyValuesFromServer() {
            Actions.setKeyValues(globalDispatcher, it)
        }
    }
}

enum class AppScreen {
    Login, Home, Config
}

data class AppState(val screen: AppScreen)

class App : ComponentSpec<Unit, AppState>() {
    companion object {
        val factory = react.createFactory(App())
    }

    private fun route() {
        RouterStore.match(
                "login" to { params ->
                    state = AppState(AppScreen.Login)
                },
                "config/?editedConfId" to { params ->
                    if (state.screen != AppScreen.Config) {
                        state = AppState(AppScreen.Config)
                    }
                    val keyValue = KeyValueStore.keyValue(params["editedConfId"].orEmpty())
                    Actions.setEditingKeyValue(globalDispatcher, keyValue)
                },
                "home" to { params ->
                    state = AppState(AppScreen.Home)
                },
                otherwise = {
                    window.location.hash = Path.login
                }
        )
    }

    override fun componentDidMount() {
        route()
        RouterStore.addChangeListener(this) {
            route()
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
                        a({ href = nullHref }) { text("Project name") }
                    }
                }
                if (state.screen != AppScreen.Login) {
                    bsNav {
                        bsNavItem({ id = "configScreenNavItem"; href = Path.keyValue.root; active = state.screen == AppScreen.Config }) { text("Configs") }
                        bsNavItem({ href = nullHref }) { text("Link") }
                        bsNavDropdown({ title = "Dropdown"; id = "basic-nav-dropdown" }) {
                            bsMenuItem { text("Action") }
                            bsMenuItem { text("Another Action") }
                            bsMenuItem { text("Something else here") }
                            bsMenuItemDivider()
                            bsMenuItem { text("Separated link") }
                        }
                    }
                }
                if (state.screen != AppScreen.Login) {
                    bsNav({ pullRight = true }) {
                        bsNavItem({ eventKey = 2; href = "/logout" }) { text("Logout") }
                    }
                } else {
                    bsNav({ pullRight = true }) {
                        bsNavItem({ eventKey = 2; href = "/" }) { text("Contact") }
                    }
                }
            }
            bsGrid({ fluid = true }) {
                bsRow() {
                    bsCol({ md = 12 }) {
                        if (state.screen == AppScreen.Config) {
                            keyValueScreen()
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


class RoutingTest {

    @Test
    fun hack() {
        kotlin.test.assertTrue(true) // qunit hack, at least one assert must be present
        qunitTest("RoutingTest") { assert: dynamic ->
            tests()
            runFirstGiven(assert)
        }
    }

    fun tests() {
        given("in any state") {
            on("routing to the main screen") {
                window.location.hash = Path.root
                it("should not render KeyValue screen") {assertTrue(jq("#configScreen").size() == 0)}
                it("should not make the KeyValue menupoint active") {assertTrue(jq("#configScreenNavItem").hasClass("active") == false)}
            }
            on("routing to the KeyValue screen") {
                window.location.hash = Path.keyValue.root
                it("should be rendered KeyValue screen") {assertTrue(jq("#configScreen").size() == 1)}
                it("should make the KeyValue menupoint active") {assertTrue(jq("#configScreenNavItem").hasClass("active"))}
            }
        }
    }
}