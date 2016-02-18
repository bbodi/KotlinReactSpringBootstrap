package hu.nevermind.app

import com.github.andrewoma.flux.Store
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

    fun match(vararg patterns: Pair<String, (Map<String, String>) -> Unit>, otherwise: ()->Unit) {
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