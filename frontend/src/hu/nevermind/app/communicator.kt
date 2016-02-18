package hu.nevermind.app

import hu.nevermind.app.keyvalue.KeyValue
import hu.nevermind.reakt.ajaxGet

fun getConfigsFromServer(callback: (Array<KeyValue>) -> Unit) {
    ajaxGet<Array<KeyValue>>("/getKeyValues") { returnedConfigs ->
        callback(returnedConfigs.map {
            KeyValue(it.key, it.value)
        }.toTypedArray())
    }
}