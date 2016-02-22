package hu.nevermind.app

import com.github.andrewoma.flux.ActionDef
import hu.nevermind.app.store.Account
import hu.nevermind.app.store.KeyValue

data class EditingAccount(val account: Account, val new: Boolean)

object Actions {
    val setEditingKeyValue = ActionDef<KeyValue?>()
    val setEditingAccount = ActionDef<EditingAccount?>()

    val modifyKeyValue = ActionDef<KeyValue>()
    val modifyAccount = ActionDef<Account>()

    val deleteKeyValue = ActionDef<KeyValue>()

    val setLoggedInUser = ActionDef<Account?>()

}
