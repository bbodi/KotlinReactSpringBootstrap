package hu.nevermind.reakt


@native("moment")
@Suppress("UNUSED_PARAMETER")
private fun moment(): MomentJs = noImpl

@native("moment")
@Suppress("UNUSED_PARAMETER")
private fun moment(other: MomentJs): MomentJs = noImpl

@native("moment")
@Suppress("UNUSED_PARAMETER")
private fun moment(millisecondsSinceUnixEpoch: Long): MomentJs = noImpl

@native("moment")
@Suppress("UNUSED_PARAMETER")
private fun moment(input: String, format: String): MomentJs = noImpl

@native("Moment")
@Suppress("UNUSED_PARAMETER")
private class MomentJs {
    fun format(formatString: String? = null): String = noImpl
    fun valueOf(): Long = noImpl
    fun millisecond(value: Int? = null): Int = noImpl
    fun second(value: Int? = null): Int = noImpl
    fun minute(value: Int? = null): Int = noImpl
    fun hour(value: Int? = null): Int = noImpl
    fun date(value: Int? = null): Int = noImpl
    fun day(value: Int? = null): Int = noImpl
    fun weekday(value: Int? = null): Int = noImpl
    fun isoWeekday(value: Int? = null): Int = noImpl
    fun dayOfYear(value: Int? = null): Int = noImpl
    fun week(value: Int? = null): Int = noImpl
    fun isoWeek(value: Int? = null): Int = noImpl
    fun month(value: Int? = null): Int = noImpl
    fun quarter(value: Int? = null): Int = noImpl
    fun year(value: Int? = null): Int = noImpl
    fun weekYear(value: Int? = null): Int = noImpl
    fun isoWeekYear(value: Int? = null): Int = noImpl
    fun weeksInYear(): Int = noImpl
    fun locale(localeName: String): Unit = noImpl
    fun toDate(): Date = noImpl

    fun startOf(unit: String): Unit = noImpl

    fun add(value: Int, unit: String): Unit = noImpl

    fun subtract(value: Int, unit: String): Unit = noImpl

    fun isSame(other: MomentJs, granularity : String): Boolean = noImpl
    fun isAfter(other: MomentJs, granularity : String): Boolean = noImpl
    fun isBefore(other: MomentJs, granularity : String): Boolean = noImpl
}

public class Moment private constructor(private val momentJs: MomentJs) {

    companion object {
        public fun now(): Moment = Moment(moment())
        public fun clone(other: Moment): Moment = Moment(moment(other.momentJs))

        public fun parse(input: String, format: String): Moment = Moment(moment(input, format))

        public fun parse(input: String, format: FormatString): Moment = Moment(moment(input, format.toString()))

        public fun parseMillisecondsSinceUnixEpoch(millisecondsSinceUnixEpoch: Long): Moment{
            requireNotNull(millisecondsSinceUnixEpoch)
            return Moment(moment(millisecondsSinceUnixEpoch))
        }

        fun setLocale(localeName: String) {
            moment().locale(localeName)
        }
    }

    public fun format(format: String): String = momentJs.format(format)

    public fun format(format: FormatString): String = this.format(format.toString())

    public fun format(init: FormatStringBuilder.() -> FormatString): String {
        val formatString = FormatStringBuilder().init()
        return this.format(formatString)
    }

    public fun clone(): Moment = Moment(moment(momentJs))

    public fun add(value: Int, unit: ManipulationUnit): Moment {
        val clone = clone()
        clone.momentJs.add(value, unit.name.toLowerCase())
        return clone
    }

    public fun plus(pair: ManipulationPair): Moment {
        return this.add(pair.value, pair.unit)
    }

    public fun subtract(value: Int, unit: ManipulationUnit): Moment {
        val clone = clone()
        clone.momentJs.subtract(value, unit.name.toLowerCase())
        return clone
    }

    public fun minus(pair: ManipulationPair): Moment {
        return this.subtract(pair.value, pair.unit)
    }

    public val millisecondsSinceUnixEpoch: Long
        get() = momentJs.valueOf()

    public var millisecond: Int
        get() = momentJs.millisecond()
        set(value) {
            momentJs.millisecond(value)
        }
    public var second: Int
        get() = momentJs.second()
        set(value) {
            momentJs.second(value)
        }
    public var minute: Int
        get() = momentJs.minute()
        set(value) {
            momentJs.minute(value)
        }
    public var hour: Int
        get() = momentJs.hour()
        set(value) {
            momentJs.hour(value)
        }
    public var dayOfMonth: Int
        get() = momentJs.date()
        set(value) {
            momentJs.date(value)
        }
    public var dayOfYear: Int
        get() = momentJs.dayOfYear()
        set(value) {
            momentJs.dayOfYear(value)
        }
    public var month: Int
        get() = momentJs.month()
        set(value) {
            momentJs.month(value)
        }

    public fun startOf(unit: DateUnit): Moment {
        val clone = clone()
        clone.momentJs.startOf(unit.name.toLowerCase())
        return clone
    }

    public fun isSame(other: Moment, granularity : DateUnit): Boolean {
        return momentJs.isSame(other.momentJs, granularity.name.toLowerCase())
    }

    public fun isAfter(other: Moment, granularity : DateUnit): Boolean {
        return momentJs.isAfter(other.momentJs, granularity.name.toLowerCase())
    }

    public fun isBefore(other: Moment, granularity : DateUnit): Boolean {
        return momentJs.isBefore(other.momentJs, granularity.name.toLowerCase())
    }

    override fun toString(): String {
        return this.format(format{ year.fourDigits + "." + month.twoDigits + "." + dayOfMonth.twoDigits + " " + hour24.twoDigits + ":" + minutes.twoDigits })
    }

    fun toDate(): Date {
        return momentJs.toDate()
    }
}

public class FormatElement (val str: String) {

    operator public fun plus(b: FormatElement): FormatString {
        return FormatString(arrayListOf(this, b))
    }

    operator fun plus(b: String): FormatString {
        return FormatString(arrayListOf(this, FormatElement(b)))
    }
}

public class FormatString(private val elements: MutableList<FormatElement> = arrayListOf()) {

    public operator fun plus(b: FormatElement): FormatString {
        elements.add(b)
        return FormatString(elements)
    }

    public operator fun plus(b: String): FormatString {
        elements.add(FormatElement(b))
        return FormatString(elements)
    }

    override fun toString(): String = elements.map { it.str }.joinToString(separator = "")
}

class Digit(private val oneDigitFactory: ()->FormatElement, private val twoDigitsFactory: ()->FormatElement, private val fourDigitsFactory: ()->FormatElement) {
    val oneDigit: FormatElement
        get() = oneDigitFactory()
    val twoDigits: FormatElement
        get() = twoDigitsFactory()
    val fourDigits: FormatElement
        get() = fourDigitsFactory()
}

public class FormatStringBuilder() {

    public val year: Digit = Digit({throw UnsupportedOperationException()}, {FormatElement("YY")}, {FormatElement("YYYY")})
    public val month: Digit = Digit({FormatElement("M")}, {FormatElement("MM")}, {throw UnsupportedOperationException()})
    public val dayOfMonth: Digit = Digit({FormatElement("D")}, {FormatElement("DD")}, {throw UnsupportedOperationException()})
    public val hour24: Digit = Digit({FormatElement("H")}, {FormatElement("HH")}, {throw UnsupportedOperationException()})
    public val hour12: Digit = Digit({FormatElement("h")}, {FormatElement("hh")}, {throw UnsupportedOperationException()})
    public val minutes: Digit = Digit({FormatElement("m")}, {FormatElement("mm")}, {throw UnsupportedOperationException()})
    public val seconds: Digit = Digit({FormatElement("s")}, {FormatElement("ss")}, {throw UnsupportedOperationException()})
}

public fun format(init: FormatStringBuilder.() -> FormatString): FormatString {
    return FormatStringBuilder().init()
}

public enum class ManipulationUnit {
    YEARS,
    QUARTERS,
    MONTHS,
    WEEKS,
    DAYS,
    HOURS,
    MINUTES,
    SECONDS,
    MILLISECONDS
}

public enum class DateUnit {
    YEAR,
    QUARTER,
    MONTH,
    WEEK,
    DAY,
    HOUR,
    MINUTE,
    SECOND,
}

public data class ManipulationPair(val value: Int, val unit: ManipulationUnit)

public val Int.days: ManipulationPair
    get() = ManipulationPair(this, ManipulationUnit.DAYS)
public val Int.months: ManipulationPair
    get() = ManipulationPair(this, ManipulationUnit.MONTHS)
public val Int.years: ManipulationPair
    get() = ManipulationPair(this, ManipulationUnit.YEARS)