package hu.nevermind.app

import com.github.andrewoma.flux.ActionDef
import hu.nevermind.app.store.Account
import hu.nevermind.app.store.KeyValue

object Actions {
    val setEditingKeyValue = ActionDef<KeyValue?>()
    val setEditingAccount = ActionDef<Account?>()

    val modifyKeyValue = ActionDef<KeyValue>()
    val modifyAccount = ActionDef<Account>()

    val deleteKeyValue = ActionDef<KeyValue>()

    val setLoggedInUser = ActionDef<Account?>()

}
