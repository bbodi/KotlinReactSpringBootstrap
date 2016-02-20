package hu.nevermind.app

import hu.nevermind.app.store.KeyValue
import hu.nevermind.common.AjaxPoster
import hu.nevermind.common.Result

object RestUrl {
    const val authenticate = "/user"

    const val getKeyValuesFromServer = "/getKeyValues"
    const val getAccountsFromServer = "/getAllAccounts"

    const val saveKeyValue = "/saveKeyValue"
    const val saveAccount = "/saveAccount"

    const val deleteKeyValue = "/deleteKeyValue"
}

class Communicator(val ajaxPoster: AjaxPoster) {

    fun getEntitiesFromServer(url: String, callback: (Array<Any>) -> Unit) {
        ajaxPoster.ajaxPost(
                url = url,
                type = "GET",
                async = false) { result: Result<Array<Any>, String> ->
            requireNotNull(result.ok)
            val returnedEntities = result.ok!!
            callback(returnedEntities)
        }
    }
    fun <T : Any> saveEntity(url: String, entity: T, callback: () -> Unit) {
        ajaxPoster.ajaxPost(
                url = url,
                data = JSON.stringify(entity),
                async = false) { result: Result<T, String> ->
            requireNotNull(result.ok)
            callback()
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
                after = after,
                type = "GET"
        )
    }
}