package hu.nevermind.app.keyvalue

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
        register(globalDispatcher, Actions.setKeyValues) { newConfigs ->
            configs = newConfigs.toArrayList()
            emitChange()
        }
        register(globalDispatcher, Actions.setEditingKeyValue) { keyValue ->
            editingKeyValue = keyValue
            emitChange()
        }
        register(globalDispatcher, Actions.modifyKeyValue) { modifiedConfig ->
            communicator.saveKeyValue(modifiedConfig) {
                val index = configs.indexOfFirst { it.key == modifiedConfig.key }
                if (index == -1) {
                    configs.add(modifiedConfig)
                } else {
                    configs[index] = modifiedConfig
                }
                emitChange()
            }
        }
    }

    fun keyValues(): List<KeyValue> = configs
    fun keyValue(key: String): KeyValue? = KeyValueStore.keyValues().firstOrNull {it.key == key.orEmpty()}
}
