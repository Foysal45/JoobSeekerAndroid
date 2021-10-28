package com.bdjobs.app.ajkerDeal.utilities

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by haideralikazal on 3/27/16.
 */
object DigitConverter {

    private val banglaMonth = arrayOf("জানুয়ারী","ফেব্রুয়ারী","মার্চ","এপ্রিল","মে","জুন","জুলাই","আগস্ট","সেপ্টেম্বর","অক্টোবর","নভেম্বর","ডিসেম্বর")
    private var decimalFormat: DecimalFormat? = null
    private val df = DecimalFormat("#.##", DecimalFormatSymbols(Locale.ENGLISH)).apply {
        roundingMode = RoundingMode.HALF_UP
    }

    init {
        decimalFormat = DecimalFormat("##,##,##,##0.00")
        //decimalFormat?.isGroupingUsed = true
        //decimalFormat?.groupingSize = 3
    }

    fun formatNumber(digits: Any, pattern: String = "##,##,##,##0.00"): String {

        decimalFormat?.applyPattern(pattern)
        return decimalFormat?.format(digits) ?: digits.toString()
    }

    @JvmStatic
    fun toBanglaDigit(englishDigits: String?): String {
        return englishDigits?.trim { it <= ' ' }?.replace('0', '০')?.replace('1', '১')?.replace('2', '২')?.replace('3', '৩')?.replace('4', '৪')?.replace('5', '৫')?.replace('6', '৬')?.replace(
            '7',
            '৭'
        )?.replace('8', '৮')?.replace('9', '৯')
            ?: ""
    }

    @JvmStatic
    fun toBanglaDigit(englishDigits: Any?, isComma: Boolean = false): String {

        return if (isComma) {
            if (englishDigits is String) {
                engCahrReplacer(englishDigits.toString())
            } else if (englishDigits is Int || englishDigits is Long) {
                engCahrReplacer(formatNumber(englishDigits ?: 0, "##,##,##,##0"))
            } else {
                engCahrReplacer(formatNumber(englishDigits ?: 0, "##,##,##,##0.00"))
            }
        } else {
            engCahrReplacer(englishDigits.toString())
        }

        /*return if (isComma) {
            toBanglaDigit(includeCommaSeparator(englishDigits))
        } else {
            englishDigits.toString().trim { it <= ' ' }
                .replace('0', '০')
                .replace('1', '১')
                .replace('2', '২')
                .replace('3', '৩')
                .replace('4', '৪')
                .replace('5', '৫')
                .replace('6', '৬')
                .replace('7', '৭')
                .replace('8', '৮')
                .replace('9', '৯')
        }*/
    }

    @JvmStatic
    fun <T> toBanglaDigitGen(englishDigits: T, isComma: Boolean = false, noOfFraction: Int = 2): String {
        return if (isComma && englishDigits != null) {
            val sDigit = englishDigits.toString()
            if (sDigit.contains(".")) {
                val dValue = sDigit.toDouble()
                if (dValue < 1000) {
                    val s = String.format("%.2f", dValue)
                    toBanglaDigit(s)
                } else {
                    toBanglaDigit(includeCommaSeparatorForDouble(sDigit.toDouble(), noOfFraction))
                }
            } else {
                toBanglaDigit(includeCommaSeparator(sDigit.toInt()))
            }
        } else {
            val sTotal = String.format("%." + noOfFraction + "f", englishDigits)
            toBanglaDigit(sTotal)
        }
    }

    @JvmStatic
    fun toEnglishDigit(banglaDigits: String): String {
        return banglaDigits.trim { it <= ' ' }
            .replace('০', '0')
            .replace('১', '1')
            .replace('২', '2')
            .replace('৩', '3')
            .replace('৪', '4')
            .replace('৫', '5')
            .replace('৬', '6')
            .replace('৭', '7')
            .replace('৮', '8')
            .replace('৯', '9')
    }

    @JvmStatic
    fun includeCommaSeparator(digits: Int): String {
        val decimalFormat = DecimalFormat()
        decimalFormat.isGroupingUsed = true
        decimalFormat.groupingSize = 3
        return decimalFormat.format(digits.toLong())
    }

    @JvmStatic
    fun includeCommaSeparatorForLong(digits: Long): String {
        val decimalFormat = DecimalFormat()
        decimalFormat.isGroupingUsed = true
        decimalFormat.groupingSize = 3
        return decimalFormat.format(digits)
    }

    @JvmStatic
    fun includeCommaSeparatorForDouble(digits: Double, noOfFraction: Int = 2): String {
        var s = ""
        for (i in 1..noOfFraction) {
            s += 0
        }
        val formatter = DecimalFormat("#,###.$s")
        return formatter.format(digits)
    }

    @JvmStatic
    fun toBanglaDate(bangladate: String): String { // mm/dd/yyyy
        val dates: Array<String>
        val day: Int
        val month: Int
        val fullbangladete: String
        dates = bangladate.split("/").toTypedArray()
        day = Integer.parseInt(dates[1])
        month = Integer.parseInt(dates[0])
        if (day == 1) {
            dates[1] = "পহেলা"
        } else if (day == 2 || day == 3) {
            dates[1] = toBanglaDigit(dates[1].padStart(2, '0')) + "শরা"
        } else if (day == 4) {
            dates[1] = "চৌঠা"
        } else if (day >= 5 && day <= 20) {
            dates[1] = toBanglaDigit(dates[1].padStart(2, '0')) + "ই"
        } else if (day >= 21 && day <= 31) {
            dates[1] = toBanglaDigit(dates[1].padStart(2, '0')) + "শে"
        }
        if (month == 1) {
            dates[0] = " জানুয়ারী"
        } else if (month == 2) {
            dates[0] = "ফেব্রুয়ারী"
        } else if (month == 3) {
            dates[0] = "মার্চ"
        } else if (month == 4) {
            dates[0] = "এপ্রিল"
        } else if (month == 5) {
            dates[0] = "মে"
        } else if (month == 6) {
            dates[0] = "জুন"
        } else if (month == 7) {
            dates[0] = "জুলাই"
        } else if (month == 8) {
            dates[0] = "আগস্ট"
        } else if (month == 9) {
            dates[0] = "সেপ্টেম্বর"
        } else if (month == 10) {
            dates[0] = "অক্টোবর"
        } else if (month == 11) {
            dates[0] = "নভেম্বর"
        } else if (month == 12) {
            dates[0] = "ডিসেম্বর"
        }
        fullbangladete = dates[1] + " " + dates[0] + " " + toBanglaDigit(dates[2])
        return fullbangladete
    }

    @JvmStatic
    fun toBanglaMonth(month: Int): String {
        var banglaMonth = ""
        if (month == 1) {
            banglaMonth = " জানুয়ারী"
        } else if (month == 2) {
            banglaMonth = "ফেব্রুয়ারী"
        } else if (month == 3) {
            banglaMonth = "মার্চ"
        } else if (month == 4) {
            banglaMonth = "এপ্রিল"
        } else if (month == 5) {
            banglaMonth = "মে"
        } else if (month == 6) {
            banglaMonth = "জুন"
        } else if (month == 7) {
            banglaMonth = "জুলাই"
        } else if (month == 8) {
            banglaMonth = "আগস্ট"
        } else if (month == 9) {
            banglaMonth = "সেপ্টেম্বর"
        } else if (month == 10) {
            banglaMonth = "অক্টোবর"
        } else if (month == 11) {
            banglaMonth = "নভেম্বর"
        } else if (month == 12) {
            banglaMonth = "ডিসেম্বর"
        }
        return banglaMonth
    }

    @JvmStatic
    fun toFullDay(day: String): String {
        var finalday = ""
        if (day == "Sat") {
            finalday = "Saturday"
        } else if (day == "Sun") {
            finalday = "Sunday"
        } else if (day == "Mon") {
            finalday = "Monday"
        } else if (day == "Tue") {
            finalday = "Tuesday"
        } else if (day == "Wed") {
            finalday = "Wednesday"
        } else if (day == "Thu") {
            finalday = "Thursday"
        } else if (day == "Fri") {
            finalday = "Friday"
        }
        return finalday
    }

    @JvmStatic
    fun toFullEngMonth(month: String): String {
        var finalMonth = ""
        if (month == "Jan") {
            finalMonth = "January"
        } else if (month == "Feb") {
            finalMonth = "February"
        } else if (month == "Mar") {
            finalMonth = "March"
        } else if (month == "Apr") {
            finalMonth = "April"
        } else if (month == "May") {
            finalMonth = "May"
        } else if (month == "Jun") {
            finalMonth = "June"
        } else if (month == "Jul") {
            finalMonth = "July"
        } else if (month == "Aug") {
            finalMonth = "August"
        } else if (month == "Sep") {
            finalMonth = "September"
        } else if (month == "Oct") {
            finalMonth = "October"
        } else if (month == "Nov") {
            finalMonth = "November"
        } else if (month == "Dec") {
            finalMonth = "December"
        }
        return finalMonth
    }

    // 2019-Mar-18

    @JvmStatic
    fun toEngDate(bdate: String): String {
        val day: Int
        var month = 0
        val year: Int
        val converteDate: String
        val dates = bdate.split("-").toTypedArray()
        year = Integer.parseInt(dates[0])
        day = Integer.parseInt(dates[2])
        if (dates[1] == "Jan") {
            month = 1
        } else if (dates[1] == "Feb") {
            month = 2
        } else if (dates[1] == "Mar") {
            month = 3
        } else if (dates[1] == "Apr") {
            month = 4
        } else if (dates[1] == "May") {
            month = 5
        } else if (dates[1] == "Jun") {
            month = 6
        } else if (dates[1] == "Jul") {
            month = 7
        } else if (dates[1] == "Aug") {
            month = 8
        } else if (dates[1] == "Sep") {
            month = 9
        } else if (dates[1] == "Oct") {
            month = 10
        } else if (dates[1] == "Nov") {
            month = 11
        } else if (dates[1] == "Dec") {
            month = 12
        }
        converteDate = "$year-$month-$day"
        return converteDate
    }

    @JvmStatic
    fun commaAndBanglaDigit(amount: Int): String {
        return toBanglaDigit(includeCommaSeparator(amount))
    }

    fun toBanglaDate(inputDate: String?, inputDatePattern: String = "yyyy-MM-dd HH:mm:ss"): String {
        inputDate ?: return ""
        val dateFormatter = SimpleDateFormat(inputDatePattern, Locale.US)
        try {
            val date = dateFormatter.parse(inputDate)
            val calendar = Calendar.getInstance()
            if (date != null) {
                calendar.time = date
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                //val hour = calendar.get(Calendar.HOUR_OF_DAY)
                //val minute = calendar.get(Calendar.MINUTE)
                //val second = calendar.get(Calendar.SECOND)

                return engCahrReplacer(day.toString()) + " " + banglaMonth[month] + ", " + engCahrReplacer(year.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return inputDate
    }

    private fun engCahrReplacer(string: String): String {
        return string.replace('0', '০')
            .replace('1', '১')
            .replace('2', '২')
            .replace('3', '৩')
            .replace('4', '৪')
            .replace('5', '৫')
            .replace('6', '৬')
            .replace('7', '৭')
            .replace('8', '৮')
            .replace('9', '৯')
    }

    fun relativeWeekday(dateTime: Date): String {
        try {
            val sdf = SimpleDateFormat("hh: mm a", Locale.US)
            val calendar = Calendar.getInstance()
            val toDate = calendar.get(Calendar.DAY_OF_YEAR)
            calendar.time = dateTime
            val selectedDate = calendar.get(Calendar.DAY_OF_YEAR)
            when (selectedDate) {
                toDate -> {
                    return "আজ @ ${toBanglaDigit(sdf.format(dateTime))}"
                }
                toDate + 1 -> {
                    return "আগামীকাল @ ${toBanglaDigit(sdf.format(dateTime))}"
                }
                toDate - 1 -> {
                    return "গতকাল @ ${toBanglaDigit(sdf.format(dateTime))}"
                }
                else -> {
                    val local = Locale("bn", "BD")
                    val sdf1 = SimpleDateFormat("EEEE", local)
                    return "${sdf1.format(dateTime)} @ ${toBanglaDigit(sdf.format(dateTime))}"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    fun relativeTime(dateTime: Date): String {
        try {
            val sdf = SimpleDateFormat("hh a", Locale.US)
            return sdf.format(dateTime)
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    fun formatDecimal(value: Float): String {
        return try {
            df.format(value)
        } catch (e: Exception) {
            e.printStackTrace()
            value.toString()
        }
    }

    fun formatDate(inputDate: String?, patternInput: String, patternOutput: String): String {
        if (inputDate == null) return ""
        try {
            val sdf1 = SimpleDateFormat(patternInput, Locale.US)
            val sdf2 = SimpleDateFormat(patternOutput, Locale.US)
            val date = sdf1.parse(inputDate)
            if (date != null) {
                return sdf2.format(date)
            }
            return inputDate
        } catch (e: Exception) {
            e.printStackTrace()
            return inputDate
        }
    }
}