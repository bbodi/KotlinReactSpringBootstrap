package hu.nevermind.app.store

import com.github.andrewoma.flux.Store
import hu.nevermind.app.Actions
import hu.nevermind.app.communicator
import hu.nevermind.app.globalDispatcher

data class KeyValue(
        val key: String,
        val value: String) {
}

object KeyValueStore : Store() {

    private var configs: MutableList<KeyValue> = arrayListOf()
    var editingKeyValue: KeyValue? = null
        private set


    init {
        register(globalDispatcher, Actions.setLoggedInuser) { loggedInuser ->
            if (loggedInuser == null) {
                configs = arrayListOf()
            } else {
                communicator.getKeyValuesFromServer() { newConfigs ->
                    configs = newConfigs.toArrayList()
                }
            }
            emitChange()
        }
        register(globalDispatcher, Actions.setEditingKeyValue) { keyValue ->
            editingKeyValue = keyValue
            emitChange()
        }
        register(globalDispatcher, Actions.modifyKeyValue) { modifiedKeyValue ->
            communicator.saveKeyValue(modifiedKeyValue) {
                val index = configs.indexOfFirst { it.key == modifiedKeyValue.key }
                if (index == -1) {
                    configs.add(modifiedKeyValue)
                } else {
                    configs[index] = modifiedKeyValue
                }
                emitChange()
            }
        }
        register(globalDispatcher, Actions.deleteKeyValue) { deletingKeyValue ->
            communicator.deleteKeyValue(deletingKeyValue) {
                val index = configs.indexOfFirst { it.key == deletingKeyValue.key }
                if (index > -1) {
                    configs.removeAt(index)
                    emitChange()
                }
            }
        }
    }

    fun keyValues(): List<KeyValue> = configs
    fun keyValue(key: String): KeyValue? = keyValues().firstOrNull { it.key == key.orEmpty() }
}
