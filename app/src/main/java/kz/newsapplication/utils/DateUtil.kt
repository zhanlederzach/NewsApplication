package kz.newsapplication.utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.text.SimpleDateFormat

object DateUtil {

    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ"
    val ISO_FORMAT_2 = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"
    val ISO_FORMAT_3 = "yyyy-MM-dd'T'HH:mm:ssZ"
    val ISO_FORMAT_4 = "yyyy-MM-dd'T'HH:mm"
    val ISO_FORMAT_5 = "yyyy-MM-dd"
    val dd_MMMM_yyyy = "dd MMMM yyyy"
    val HH_mm_dd_MM_yyyy = "HH:mm, dd.MM.yyyy"
    val dd_MM_yyyy = "dd.MM.yyyy"
    val yyyy = "yyyy"

    fun convertIsoToDate(dateIso: String?, regex: String = "dd.MM.yyyy", isoFormat: String? = null): String {
        if (dateIso.isNullOrEmpty()) return ""
        return try {
            val dateTime = DateTime.parse(dateIso)
            val formatter = DateTimeFormat.forPattern(regex)
            return dateTime.toString(formatter)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            ""
        }
    }
}
