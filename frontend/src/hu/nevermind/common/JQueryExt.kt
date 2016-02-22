package hu.nevermind.common

import org.w3c.dom.Element
import jquery.JQuery
import org.w3c.dom.Node
import org.w3c.dom.HTMLElement
import org.w3c.dom.Window

@Suppress("UNUSED_PARAMETER")
public @native fun Node.querySelector(selector: String): Node = noImpl

@Suppress("UNUSED_PARAMETER") public @native("$") fun jq(win: Window): JQuery = noImpl
public @native fun JQuery.hide(): Unit = noImpl
public @native("$") val jq: JQuery = noImpl
public @native fun JQuery.show(): Unit = noImpl
public @native fun JQuery.focus(): Unit = noImpl
public @native fun JQuery.blur(): Unit = noImpl
public @native fun JQuery.empty(): Unit = noImpl
@Suppress("UNUSED_PARAMETER") public @native fun JQuery.css(s1: String, s: String): JQuery = noImpl
@Suppress("UNUSED_PARAMETER") public @native fun JQuery.css(s1: String): String = noImpl
@Suppress("UNUSED_PARAMETER") public @native fun JQuery.append(element: Element): Unit = noImpl
@Suppress("UNUSED_PARAMETER") public @native fun JQuery.on(eventName: String, callback: (event: dynamic)->Unit): Unit = noImpl
@Suppress("UNUSED_PARAMETER") public @native fun JQuery.off(eventName: String, callback: (event: dynamic)->Unit): Unit = noImpl
public @native fun JQuery.remove(): Unit = noImpl
@Suppress("UNUSED_PARAMETER") public @native fun JQuery.get(index: Int): HTMLElement? = noImpl
@Suppress("UNUSED_PARAMETER") public @native fun JQuery.find(str: String): JQuery = noImpl
public @native fun JQuery.size(): Int = noImpl
@Suppress("UNUSED_PARAMETER") public @native fun JQuery.`is`(str: String): Boolean = noImpl

@Suppress("UNUSED_PARAMETER") public @native fun JQuery.prepend(str: Any): JQuery = noImpl
@Suppress("UNUSED_PARAMETER") public @native fun JQuery.append(str: Any): JQuery = noImpl
@Suppress("UNUSED_PARAMETER") public @native fun JQuery.replaceWith(str: Any): JQuery = noImpl

public @native fun JQuery.highcharts(): dynamic = noImpl
@Suppress("UNUSED_PARAMETER") public @native fun JQuery.highcharts(param: dynamic): Unit = noImpl
@Suppress("UNUSED_PARAMETER") public @native fun JQuery.notify(text: String, params: dynamic = null): Unit = noImpl

public @native fun JQuery.block(): Unit = noImpl
public @native fun JQuery.unblock(): Unit = noImpl

@Suppress("UNUSED_PARAMETER") public @native("val") fun JQuery.value(value: String?): Unit= noImpl

// jQuery Caret
public @native fun JQuery.caret(): Int = noImpl
@Suppress("UNUSED_PARAMETER") public @native fun JQuery.caret(pos: Int): Unit = noImpl