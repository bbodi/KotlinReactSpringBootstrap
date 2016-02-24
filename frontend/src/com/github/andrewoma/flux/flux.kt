package com.github.andrewoma.flux

public abstract class Store {

    private val changeListeners: MutableMap<Any, () -> Unit> = hashMapOf()

    fun <PAYLOAD> register(dispatcher: Dispatcher, actionDef: ActionDef<PAYLOAD>, callback: DispatchCallbackBody.(PAYLOAD) -> Unit): RegisteredActionHandler {
        return dispatcher.register(this, actionDef, callback)
    }

    fun unRegister(dispatcher: Dispatcher, token: RegisteredActionHandler) {
        dispatcher.unRegister(token)
    }

    public fun addChangeListener(self: Any, callback: () -> Unit) {
        changeListeners.put(self, callback)
    }

    protected fun emitChange() {
        changeListeners.values.forEach { it() }
    }

    fun removeListener(self: Any) {
        changeListeners.remove(self)
    }
}

public class ActionDef<PAYLOAD> {

    public operator fun invoke(dispatcher: Dispatcher, payload: PAYLOAD) {
        dispatcher.dispatch(this, payload)
    }
}


private class ActionHandlers(val handlers: MutableList<RegisteredActionHandler> = arrayListOf()) {

}

public class RegisteredActionHandler internal constructor(val store: Store, val actionDef: ActionDef<*>, val callback: DispatchCallbackBody.(Any?) -> Unit) {
    var pending = false
    var handled = false
}

public class DispatchCallbackBody(val dispatcher: Dispatcher, val store: Store) {
    fun waitFor(vararg registeredActionHandlers: Store) {
        dispatcher.waitFor(registeredActionHandlers)
    }
}

class Dispatcher {
    private var pendingPayload: Any? = null
    private var pendingActionDef: ActionDef<*>? = null
    private val actionHandlersList: MutableMap<ActionDef<*>, ActionHandlers> = hashMapOf()
    private var dispatching = false

    fun <PAYLOAD> register(store: Store, action: ActionDef<PAYLOAD>, callback: DispatchCallbackBody.(PAYLOAD) -> Unit): RegisteredActionHandler {
        val actionHandlers = actionHandlersList.getOrPut(action, { ActionHandlers() })
        val registeredActionHandler = RegisteredActionHandler(store, action, callback as (DispatchCallbackBody.(Any?) -> Unit) )
        actionHandlers.handlers.add(registeredActionHandler)
        return registeredActionHandler
    }

    fun unRegister(registeredActionHandler: RegisteredActionHandler) {
        actionHandlersList[registeredActionHandler.actionDef]?.handlers?.remove(registeredActionHandler)
    }

    fun waitFor(stores: Array<out Store>) {
        require(dispatching) { "Dispatcher.waitFor(...): Must be invoked while dispatching." }
        val handlersForCurrentAction = actionHandlersList[pendingActionDef]?.handlers.orEmpty()
        val (pendingHandlers, nonPendingHandlers) = handlersForCurrentAction.filter { stores.contains(it.store) }.partition { it.pending || it.handled }
        val unhandledHandlers = pendingHandlers.firstOrNull { !it.handled }
        require(unhandledHandlers == null) { "Dispatcher.waitFor(...): Circular dependency detected while waiting for $unhandledHandlers." }
        nonPendingHandlers.forEach {
            require(actionHandlersList[it.actionDef]?.handlers?.contains(it) ?: false) { "Dispatcher.waitFor(...): $it does not map to a registered callback." }
            invokeCallback(it)
        }
    }

    fun <PAYLOAD> dispatch(action: ActionDef<PAYLOAD>, payload: PAYLOAD) {
        require(!dispatching) { "Dispatch.dispatch(...): Cannot dispatch in the middle of a dispatch." }
        this.startDispatching(action, payload);
        try {
            actionHandlersList[action]?.handlers?.forEach {
                if (!it.pending) {
                    invokeCallback(it)
                }
            }
        } finally {
            this.stopDispatching();
        }
    }

    private fun invokeCallback(it: RegisteredActionHandler) {
        it.pending = true
        val body = DispatchCallbackBody(this, it.store)
        val callback = it.callback
        body.callback(pendingPayload)
        it.handled = true
    }

    private fun <PAYLOAD> startDispatching(action: ActionDef<PAYLOAD>, payload: PAYLOAD) {
        actionHandlersList[action]?.handlers?.forEach {
            it.pending = false
            it.handled = false
        }
        pendingPayload = payload
        pendingActionDef = action
        dispatching = true
    }

    private fun stopDispatching() {
        pendingActionDef = null
        pendingPayload = null
        dispatching = false
    }
}