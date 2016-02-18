package hu.nevermind.app


import com.github.andrewoma.flux.Dispatcher
import com.github.andrewoma.react.*
import hu.nevermind.app.keyvalue.KeyValueScreenTest
import hu.nevermind.app.keyvalue.KeyValueStore
import hu.nevermind.app.keyvalue.keyValueScreen
import hu.nevermind.common.given
import hu.nevermind.common.runFirstGiven
import hu.nevermind.reakt.bootstrap.*
import hu.nevermind.reakt.jqext.get
import hu.nevermind.reakt.jqext.hide
import hu.nevermind.reakt.jqext.size
import jquery.jq
import org.junit.Test
import kotlin.browser.window

@native private val QUnit: dynamic = noImpl


val globalDispatcher = Dispatcher()

public fun main(vararg arg: String) {
    QUnit.config.autostart = false
    react.render(app(), jq("#app").get(0)!!)
    if (window.location.search.contains("tests")) {
        window.location.hash = Path.root
        QUnit.start()
    } else {
        jq("#qunit").hide()
        jq("#qunit-fixture").hide()
        getConfigsFromServer() {
            Actions.setKeyValues(globalDispatcher, it)
        }
    }
}

enum class AppScreen {
    Main, Config
}

data class AppState(val screen: AppScreen)

class App : ComponentSpec<Unit, AppState>() {
    companion object {
        val factory = react.createFactory(App())
    }

    override fun componentDidMount() {
        RouterStore.addChangeListener(this) {
            RouterStore.match(
                    "config/?editedConfId" to { params ->
                        if (state.screen != AppScreen.Config) {
                            state = AppState(AppScreen.Config)
                        }
                        val keyValue = KeyValueStore.keyValue(params["editedConfId"].orEmpty())
                        Actions.setEditingKeyValue(globalDispatcher, keyValue)
                    },
                    "" to { params ->
                        state = AppState(AppScreen.Main)
                    }
            )
        }
    }

    override fun initialState(): AppState? {
        return AppState(AppScreen.Main)
    }

    override fun Component.render() {
        div({ className = "" }) {
            bsNavbar() {
                bsNavbarHeader() {
                    bsNavbarBrand() {
                        a({ href = nullHref }) { text("Link") }
                    }
                }
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
                bsNav({ pullRight = true }) {
                    bsNavItem({
                        eventKey = 1; href = Path.root
                    }) { text("Link Right") }
                    bsNavItem({ eventKey = 2; href = nullHref }) { text("Link Right") }
                }
            }
            bsGrid({ fluid = true }) {
                bsRow() {
                    bsCol({ md = 12 }) {
                        if (state.screen == AppScreen.Config) {
                            keyValueScreen()
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



class RoutingTest {

    init {
        val test = js("QUnit.test")
        test("tests") { assert: dynamic ->
            configScreenShouldAppear()
            KeyValueScreenTest().tests()

            runFirstGiven(assert)
        }
    }

    @Test fun hack() {
        kotlin.test.assertTrue(true) // qunit hack, at least one assert must be present
    }

    fun configScreenShouldAppear() {
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