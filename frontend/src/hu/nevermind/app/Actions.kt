package hu.nevermind.app

import com.github.andrewoma.flux.ActionDef
import hu.nevermind.app.store.KeyValue
import hu.nevermind.app.store.LoggedInUser

object Actions {
    val setLoggedInuser = ActionDef<LoggedInUser?>()
    val modifyKeyValue = ActionDef<KeyValue>()
    val setEditingKeyValue = ActionDef<KeyValue?>()
}
