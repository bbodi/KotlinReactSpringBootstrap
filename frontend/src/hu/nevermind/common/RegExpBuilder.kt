package hu.nevermind.common

import kotlin.text.js.RegExp

@Suppress("UNUSED_PARAMETER")
@native class RegExpBuilder {
    fun find(str: String):  RegExpBuilder = noImpl
    fun min(num: Int):  RegExpBuilder = noImpl
    fun max(num: Int):  RegExpBuilder = noImpl
    fun exactly(num: Int):  RegExpBuilder = noImpl
    fun then(str: String):  RegExpBuilder = noImpl
    fun like(pattern:  RegExpBuilder = noImpl):  RegExpBuilder = noImpl
    fun append(pattern:  RegExpBuilder = noImpl):  RegExpBuilder = noImpl
    fun optional(pattern:  RegExpBuilder = noImpl):  RegExpBuilder = noImpl
    fun either(pattern:  RegExpBuilder = noImpl):  RegExpBuilder = noImpl
    fun either(pattern: String):  RegExpBuilder = noImpl
    fun neither(pattern:  RegExpBuilder = noImpl):  RegExpBuilder = noImpl
    fun neither(pattern: String):  RegExpBuilder = noImpl
    fun or(pattern:  RegExpBuilder = noImpl):  RegExpBuilder = noImpl
    fun or(pattern: String):  RegExpBuilder = noImpl
    fun nor(pattern:  RegExpBuilder = noImpl):  RegExpBuilder = noImpl
    fun nor(pattern: String):  RegExpBuilder = noImpl
    fun anything():  RegExpBuilder = noImpl
    fun something():  RegExpBuilder = noImpl
    fun lineBreak():  RegExpBuilder = noImpl
    fun lineBreaks():  RegExpBuilder = noImpl
    fun whitespace():  RegExpBuilder = noImpl
    fun tab():  RegExpBuilder = noImpl
    fun tabs():  RegExpBuilder = noImpl
    fun letter():  RegExpBuilder = noImpl
    fun letters():  RegExpBuilder = noImpl
    fun startOfInput():  RegExpBuilder = noImpl
    fun startOfLine():  RegExpBuilder = noImpl
    fun endOfInput():  RegExpBuilder = noImpl
    fun endOfLine():  RegExpBuilder = noImpl
    fun lowerCaseLetter():  RegExpBuilder = noImpl
    fun lowerCaseLetters():  RegExpBuilder = noImpl
    fun upperCaseLetter():  RegExpBuilder = noImpl
    fun upperCaseLetters():  RegExpBuilder = noImpl
    fun digit():  RegExpBuilder = noImpl
    fun digits():  RegExpBuilder = noImpl
    fun asGroup():  RegExpBuilder = noImpl
    fun ofGroup():  RegExpBuilder = noImpl
    fun ignoreCase():  RegExpBuilder = noImpl
    fun anythingBut(str: String):  RegExpBuilder = noImpl
    fun of(str: String):  RegExpBuilder = noImpl
    fun maybe(str: String):  RegExpBuilder = noImpl
    fun from(str: Array<String>):  RegExpBuilder = noImpl
    fun some(str: Array<String>):  RegExpBuilder = noImpl
    fun maybeSome(str: Array<String>):  RegExpBuilder = noImpl
    fun notFrom(str: Array<String>):  RegExpBuilder = noImpl

    fun getRegExp(str: String): RegExp = noImpl
    fun test(str: String): Boolean = noImpl
    fun exec(str: String): Array<String> = noImpl
}