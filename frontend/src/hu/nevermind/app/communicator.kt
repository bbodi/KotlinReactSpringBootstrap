package hu.nevermind.app

import hu.nevermind.app.keyvalue.KeyValue
import hu.nevermind.common.AjaxPoster
import hu.nevermind.common.AjaxRequest
import hu.nevermind.common.ajaxGet

class Communicator(val ajaxPoster: AjaxPoster) {
    fun getKeyValuesFromServer(callback: (Array<KeyValue>) -> Unit) {
        ajaxGet<Array<KeyValue>>("/getKeyValues") { returnedConfigs ->
            callback(returnedConfigs.map {
                KeyValue(it.key, it.value)
            }.toTypedArray())
        }
    }

    fun saveKeyValue(entity: KeyValue, callback: (KeyValue) -> Unit) {
        ajaxPoster.ajaxPost(AjaxRequest<KeyValue>(
                url = "/putKeyValue",
                data = JSON.stringify(entity),
                async = false,
                success = { returnedKeyValue ->
                    callback(returnedKeyValue)
                }
        ))
    }
}