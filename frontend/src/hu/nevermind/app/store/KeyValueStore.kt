package hu.nevermind.app.store

import com.github.andrewoma.flux.Store
import hu.nevermind.app.Actions
import hu.nevermind.app.RestUrl
import hu.nevermind.app.communicator
import hu.nevermind.app.globalDispatcher

data class KeyValue(
        val key: String,
        val value: String) {
}

object KeyValueStore : Store() {

    private var keyValues: MutableList<KeyValue> = arrayListOf()
    var editingKeyValue: KeyValue? = null
        private set


    init {
        register(globalDispatcher, Actions.setLoggedInUser) { loggedInUser ->
            if (loggedInUser == null) {
                keyValues = arrayListOf()
            } else {
                communicator.getEntitiesFromServer(RestUrl.getKeyValuesFromServer) { returnedArray ->
                    val newConfigs = returnedArray.map {
                        with(it.asDynamic()) {
                            KeyValue(key, value)
                        }
                    }.toTypedArray()
                    keyValues = newConfigs.toArrayList()
                }
            }
            emitChange()
        }
        register(globalDispatcher, Actions.setEditingKeyValue) { keyValue ->
            if (keyValue != editingKeyValue) {
                editingKeyValue = keyValue
                emitChange()
            }
        }
        register(globalDispatcher, Actions.modifyKeyValue) { modifiedKeyValue ->
            communicator.saveEntity(RestUrl.saveKeyValue, modifiedKeyValue) {
                val index = keyValues.indexOfFirst { it.key == modifiedKeyValue.key }
                if (index == -1) {
                    keyValues.add(modifiedKeyValue)
                } else {
                    keyValues[index] = modifiedKeyValue
                }
                emitChange()
            }
        }
        register(globalDispatcher, Actions.deleteKeyValue) { deletingKeyValue ->
            communicator.deleteKeyValue(deletingKeyValue) {
                val index = keyValues.indexOfFirst { it.key == deletingKeyValue.key }
                if (index > -1) {
                    keyValues.removeAt(index)
                    emitChange()
                }
            }
        }
    }

    fun keyValues(): List<KeyValue> = keyValues
    fun keyValue(key: String): KeyValue? = keyValues().firstOrNull { it.key == key }
}
