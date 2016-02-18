package hu.nevermind.reakt

/* ajaxPost(AjaxRequest<AjaxResult<RESULT>>(
            url = "/ajax/ajax/",
            data = dataToSend,
            success = { result: AjaxResult<RESULT> ->
                if (result.status) {
                    resultHandler(result.data)
                } else {
                    throw Exception("Ajax Error")
                }
            }
    ))*/

data class AjaxRequest<RESULT>(val url:String,
                               val type:String = "POST",
                               val data:String,
                               val contentType:String = "application/json; charset=utf-8",
                               val dataType:String = "json",
                               val async : Boolean = true,
                               val success: ((RESULT) -> Unit))

public data class AjaxResult<T>(val status: Boolean, val data: T)

@native
interface JQAjax {
    fun <T> get(url:String, loaded:(response: T) -> Unit) : Unit = noImpl
    //fun post(url:String, data:Any?, handler:()->Unit, type:String = "json") : Unit = noImpl
    //fun ajax(url:String, type:String, contentType:String, dataType:String, data:Any, success:()->Unit) : Unit = noImpl
    fun <RESULT> ajax(request:AjaxRequest<RESULT>) : Unit = noImpl
}

@native("$") public var ajaxJQuery: JQAjax = null!!


public fun <T> ajaxGet(url:String, loaded:(response:T) -> Unit) : Unit {
    ajaxJQuery.get(url = url, loaded = loaded)
}

public fun <RESULT> ajaxPost(ajaxRequest: AjaxRequest<RESULT>) : Unit {
    ajaxJQuery.ajax(ajaxRequest)
}