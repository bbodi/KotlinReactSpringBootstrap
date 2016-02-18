package hu.nevermind.common

data class AjaxRequest<RESULT>(val url:String,
                               val type:String = "POST",
                               val data: Any,
                               val contentType:String = "application/json; charset=utf-8",
                               val dataType:String = "json",
                               val async : Boolean = true,
                               val success: ((RESULT) -> Unit))

public data class AjaxResult<T>(val status: Boolean, val data: T)

@native("$")
object Jq {
    fun ajax(req: AjaxRequest<*>): Unit = noImpl
    fun <T> get(url:String, loaded:(response: T) -> Unit) : Unit = noImpl
}

class JqueryAjaxPoster() : AjaxPoster {

    override public fun <RESULT> ajaxPost(ajaxRequest: AjaxRequest<RESULT>) : Unit {
        Jq.ajax(ajaxRequest)
    }
}

class TestAjaxPoster() : AjaxPoster {
    var ajaxRequest: AjaxRequest<*>? = null
    override public fun <RESULT> ajaxPost(ajaxRequest: AjaxRequest<RESULT>) : Unit {
        this.ajaxRequest = ajaxRequest
    }
}

public fun <T> ajaxGet(url:String, loaded:(response:T) -> Unit) : Unit {
    Jq.get(url = url, loaded = loaded)
}

interface AjaxPoster {
    public fun <RESULT> ajaxPost(ajaxRequest: AjaxRequest<RESULT>) : Unit
}