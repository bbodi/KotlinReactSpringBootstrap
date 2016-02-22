package hu.nevermind.common

data class AjaxRequest(val url: String,
                       val type: String = "POST",
                       val data: Any,
                       val contentType: String = "application/json; charset=utf-8",
                       val dataType: String = "json",
                       val async: Boolean = true,
                       val success: ((Any) -> Unit))

public data class AjaxResult<T>(val status: Boolean, val data: T)

@native("$")
@Suppress("UNUSED_PARAMETER")
object Jq {
    fun ajax(req: Any): Unit = noImpl
    fun <T> get(url: String,
                data: Any? = null,
                success: ((response: T) -> Unit)?,
                dataType: String? = null
    ): dynamic = noImpl
}

class JqueryAjaxPoster() : AjaxPoster {

    override fun <RESULT> ajaxPost(url: String,
                                   type: String,
                                   data: Any?,
                                   contentType: String,
                                   dataType: String,
                                   async: Boolean,
                                   after: (Result<RESULT, String>) -> Unit) {
        val error = {jqXHR: dynamic, textStatus: String, errorThrown: String ->
            after(err("$textStatus $errorThrown"))
        }
        val success = { data: RESULT->
            after(ok(data))
        }
        val ajaxRequest: dynamic = object {
                val url = url
                val type = type
                val data = data
                val contentType = contentType
                val dataType = dataType
                val async = async
                val error = error
                val success = success
        }
        Jq.ajax(ajaxRequest)
    }


}

class TestAjaxPoster() : AjaxPoster {

    override fun <RESULT> ajaxPost(url: String,
                                   type: String,
                                   data: Any?,
                                   contentType: String,
                                   dataType: String,
                                   async: Boolean,
                                   after: (Result<RESULT, String>) -> Unit) {
        require(url in results) {"'$url' is not found in TestAjaxPoster"}
        val result = results[url]!!(data) as Result<RESULT, String>
        after(result)
    }

    private val results: MutableMap<String, (Any?) -> Result<*, String>> = hashMapOf()

    fun <T> pushResult(url: String, resultBuilder: (T) -> Result<*, String>) {
        results.put(url, resultBuilder.asDynamic())
    }
}

open class Result<OK, ERR>(val ok: OK?, val error: ERR?)
fun <OK, ERR> ok(data: OK): Result<OK, ERR> = Result(data, null)
fun <OK, ERR> err(err: ERR): Result<OK, ERR> = Result(null, err)

interface AjaxPoster {
    public fun <RESULT> ajaxPost(
            url: String,
            type: String = "POST",
            data: Any? = null,
            contentType: String = "application/json; charset=utf-8",
            dataType: String = "json",
            async: Boolean = true,
            after: ((Result<RESULT, String>) -> Unit)
    ): Unit
}