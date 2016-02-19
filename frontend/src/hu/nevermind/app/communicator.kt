package hu.nevermind.app

import hu.nevermind.app.store.KeyValue
import hu.nevermind.common.AjaxPoster
import hu.nevermind.common.Result

object RestUrl {
    val getKeyValuesFromServer = "/getKeyValues"
    val authenticate = "/user"
    val putKeyValue = "/putKeyValue"
    val deleteKeyValue = "/deleteKeyValue"
}

class Communicator(val ajaxPoster: AjaxPoster) {
    fun getKeyValuesFromServer(callback: (Array<KeyValue>) -> Unit) {
        ajaxPoster.ajaxPost(
                url = RestUrl.getKeyValuesFromServer,
                type = "GET",
                async = false) { result: Result<Array<KeyValue>, String> ->
            if (result.error != null) {
                throw Exception(result.error)
            }
            val returnedConfigs = result.ok!!
            callback(returnedConfigs.map {
                KeyValue(it.key, it.value)
            }.toTypedArray())
        }
    }

    fun saveKeyValue(entity: KeyValue, callback: (KeyValue) -> Unit) {
        ajaxPoster.ajaxPost(
                url = RestUrl.putKeyValue,
                data = JSON.stringify(entity),
                async = false) { result: Result<KeyValue, String> ->
            callback(result.ok!!)
        }
    }

    fun deleteKeyValue(entity: KeyValue, callback: (String) -> Unit) {
        ajaxPoster.ajaxPost(
                url = RestUrl.deleteKeyValue,
                data = entity.key,
                async = false) { result: Result<String, String> ->
            callback(result.ok!!)
        }
    }

    fun authenticate(after: (Result<Any, String>) -> Unit) {
        ajaxPoster.ajaxPost(
                url = RestUrl.authenticate,
                async = false,
                success = after,
                type = "GET"
        )
    }
}