package hu.nevermind.common

import kotlin.text.js.RegExp

@native class RegExpBuilder {
    fun find(str: String): RegExpBuilder
    fun min(num: Int): RegExpBuilder
    fun max(num: Int): RegExpBuilder
    fun exactly(num: Int): RegExpBuilder
    fun then(str: String): RegExpBuilder
    fun like(pattern: RegExpBuilder): RegExpBuilder
    fun append(pattern: RegExpBuilder): RegExpBuilder
    fun optional(pattern: RegExpBuilder): RegExpBuilder
    fun either(pattern: RegExpBuilder): RegExpBuilder
    fun either(pattern: String): RegExpBuilder
    fun neither(pattern: RegExpBuilder): RegExpBuilder
    fun neither(pattern: String): RegExpBuilder
    fun or(pattern: RegExpBuilder): RegExpBuilder
    fun or(pattern: String): RegExpBuilder
    fun nor(pattern: RegExpBuilder): RegExpBuilder
    fun nor(pattern: String): RegExpBuilder
    fun anything(): RegExpBuilder
    fun something(): RegExpBuilder
    fun lineBreak(): RegExpBuilder
    fun lineBreaks(): RegExpBuilder
    fun whitespace(): RegExpBuilder
    fun tab(): RegExpBuilder
    fun tabs(): RegExpBuilder
    fun letter(): RegExpBuilder
    fun letters(): RegExpBuilder
    fun startOfInput(): RegExpBuilder
    fun startOfLine(): RegExpBuilder
    fun endOfInput(): RegExpBuilder
    fun endOfLine(): RegExpBuilder
    fun lowerCaseLetter(): RegExpBuilder
    fun lowerCaseLetters(): RegExpBuilder
    fun upperCaseLetter(): RegExpBuilder
    fun upperCaseLetters(): RegExpBuilder
    fun digit(): RegExpBuilder
    fun digits(): RegExpBuilder
    fun asGroup(): RegExpBuilder
    fun ofGroup(): RegExpBuilder
    fun ignoreCase(): RegExpBuilder
    fun anythingBut(str: String): RegExpBuilder
    fun of(str: String): RegExpBuilder
    fun maybe(str: String): RegExpBuilder
    fun from(str: Array<String>): RegExpBuilder
    fun some(str: Array<String>): RegExpBuilder
    fun maybeSome(str: Array<String>): RegExpBuilder
    fun notFrom(str: Array<String>): RegExpBuilder

    fun getRegExp(str: String): RegExp
    fun test(str: String): Boolean
    fun exec(str: String): Array<String>
}