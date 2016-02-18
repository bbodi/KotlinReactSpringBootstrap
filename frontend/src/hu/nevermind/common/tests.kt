package hu.nevermind.common

import hu.nevermind.reakt.jqext.get
import hu.nevermind.reakt.jqext.size
import jquery.jq
import org.w3c.dom.HTMLInputElement
import kotlin.browser.window

open class TestBuilder(val assert: dynamic, val description: TestDescription) {
    fun assertTrue(b: Boolean) {
        kotlin.test.assertTrue(b, description.toString())
    }

    fun assertFalse(b: Boolean) {
        kotlin.test.assertFalse(b, description.toString())
    }

    fun <T> assertEquals(expected: T, actual: T) {
        kotlin.test.assertEquals(expected, actual, description.toString())
    }
}

class GivenBuilder(val assert: dynamic, val body: GivenBuilder.()->Unit, val description: TestDescription) {

    private var ons = arrayListOf<Pair<String, OnBuilder.() -> Unit>>()

    fun on(onDescr: String, body: OnBuilder.() -> Unit) {
        ons.add(onDescr to body)
    }

    fun runFirstOn(afterNoMoreOnToRun: ()->Unit) {
        val pair = ons.firstOrNull()
        if (pair == null) {
            afterNoMoreOnToRun()
            return
        }
        ons.removeAt(0)
        val on = OnBuilder(assert, description.copy(on = pair.first))
        val runBodyAndCollectIts = pair.second
        runGivenBodyToInitializeEnvironmentForOns()
        later(assert) {
            on.runBodyAndCollectIts()
            later(assert) {
                on.runIts()
                this.runFirstOn(afterNoMoreOnToRun)
            }
        }
    }

    fun runGivenBodyToInitializeEnvironmentForOns() {
        val tmpGiven = GivenBuilder(assert, body, description)
        tmpGiven.body()
    }
}

class OnBuilder(val assert: dynamic, val description: TestDescription) {

    private val its = arrayListOf<Pair<String, TestBuilder.() -> Unit>>()

    fun it(itDescr: String, body: TestBuilder.() -> Unit) {
        its.add(itDescr to body)
    }

    fun runIts() {
        its.forEach { pair ->
            val it = TestBuilder(assert, description.copy(it = pair.first))
            val body = pair.second
            console.info("TEST: ${it.description}")
            try {
                it.body()
            } catch(e: dynamic) {
                console.error("Error during test: ${it.description}", e)
            }
        }
    }
}

data class TestDescription(val given: String, val on: String?, val it: String?)

fun runFirstGiven(assert: dynamic) {
    val pair = givens.firstOrNull() ?: return
    givens.removeAt(0)
    val body = pair.second
    val given = GivenBuilder(assert, body, TestDescription(pair.first, null, null))
    given.body()
    given.runFirstOn(afterNoMoreOnToRun = {runFirstGiven(assert)})
}

private val givens = arrayListOf<Pair<String, GivenBuilder.() -> Unit>>()

fun given(description: String, body: GivenBuilder.() -> Unit) {
    givens.add(description to body)
}

private fun later(assert: dynamic, body: () -> Unit) {
    val done = assert.async()
    window.setTimeout({
        body()
        done()
    }, 500)
}

fun simulateChangeInput(id: String, body: (HTMLInputElement)->Unit) {
    val input = jq("#$id").get(0) as HTMLInputElement
    body(input)
    ReactTestUtils.Simulate.change(input);
}

fun String.appearOnScreen(): Boolean = jq("#${this}").size() == 1

fun String.simulateClick() {
    ReactTestUtils.Simulate.click(jq("#${this}").get(0)!!)
}