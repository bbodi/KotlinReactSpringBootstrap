package hu.nevermind.app.store

import com.github.andrewoma.flux.Store
import hu.nevermind.common.given
import hu.nevermind.common.qunitTest
import hu.nevermind.common.runFirstGiven
import hu.nevermind.common.size
import jquery.jq
import org.junit.Test
import kotlin.browser.window

object RouterStore : Store() {

    var path = window.location.hash.substring(1)
        private set

    init {

        window.addEventListener("hashchange", {
            path = window.location.hash.substring(1)
            emitChange()
        }, false);
    }

    fun match(vararg patterns: Pair<String, (Map<String, String>) -> Unit>, otherwise: () -> Unit) {
        val pattern = patterns.firstOrNull { pattern ->
            match(pattern.first, pattern.second)
        }
        if (pattern == null) {
            otherwise()
        }
    }

    fun match(pattern: String, body: (Map<String, String>) -> Unit): Boolean {
        val patternParts = pattern.split("/")
        val pathParts = path.split("/")
        var params = hashMapOf<String, String>()
        var ok = true
        patternParts.withIndex().forEach { entry ->
            val (index, value) = entry
            if (value.startsWith("?")) {
                if (pathParts.size > index) {
                    params.put(value.substring(1), pathParts[index])
                }
            } else if (value.startsWith(":") && pathParts.size > index) {
                params.put(value.substring(1), pathParts[index])
            } else {
                if (pathParts.size <= index || pathParts[index] != value) {
                    ok = false
                }
            }
        }
        if (ok) {
            body(params)
        }
        return ok
    }

}

class RouterStoreTest {

    @Test
    fun hack() {
        kotlin.test.assertTrue(true) // qunit hack, at least one assert must be present
        qunitTest("RouterStoreTest") { assert: dynamic ->
            tests()
            runFirstGiven(assert)
        }
    }

    fun tests() {
        var matchResult = ""
        val runMatcher = {
            RouterStore.match(
                    "${Path.login}" to { params ->
                        matchResult = "login"
                    },
                    "${Path.keyValue.root}?editedKeyValueId" to { params ->
                        matchResult = "keyValue - ${params["editedKeyValueId"].orEmpty()}"
                    },
                    otherwise = {
                        matchResult = "other"
                    }
            )
        }
        given("the URL = root") {
            window.location.hash = Path.root
            on("routing to the main screen") {
                runMatcher()
                it("should not match") {
                    assertEquals("other", matchResult)
                }
            }
        }
        given("the URL = login") {
            window.location.hash = Path.login
            on("routing to the login screen") {
                runMatcher()
                it("should match to login") {
                    assertEquals("login", matchResult)
                }
            }
        }
        given("the URL = login") {
            window.location.hash = Path.keyValue.withOpenedEditorModal("69")
            on("routing to the keyValue editor screen") {
                runMatcher()
                it("should match to login") {
                    assertEquals("keyValue - 69", matchResult)
                }
            }
        }
    }
}