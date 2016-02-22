package hu.nevermind.app

abstract class LocalizedTextsScreenAccount {
    abstract val username: String
    abstract val password: String
    abstract val state: String
    abstract val disabled: String
    abstract val enabled: String
    abstract val columnDisabled: String
}

abstract class LocalizedTextsScreenKeyValue {
    abstract val key: String
    abstract val value: String
}

abstract class LocalizedTextsScreen {
    abstract val account: LocalizedTextsScreenAccount
    abstract val keyValue: LocalizedTextsScreenKeyValue
    abstract val login: LocalizedTextsLogin
    abstract val home: LocalizedTextsHome
}

abstract class LocalizedTextsCommon {
    abstract val edit: String
    abstract val add: String
    abstract val delete: String
    abstract val cancel: String
    abstract val save: String
}


abstract class LocalizedTextsHome {
    abstract val contact: String
    abstract val logout: String
    abstract val title: String
    abstract val menuAdmin: String
    abstract val menuAccounts: String
    abstract val menuOptions: String
    abstract val menuKeyValue: String
}

abstract class LocalizedTextsLogin {
    abstract val login: String
    abstract val username: String
    abstract val password: String
}

abstract class LocalizedTexts {
    abstract val screen: LocalizedTextsScreen
    abstract val common: LocalizedTextsCommon
}

val msg = object : LocalizedTexts() {
    override val common = object : LocalizedTextsCommon() {
        override val edit = "Edit"
        override val add = "Add"
        override val delete = "Delete"
        override val cancel = "Cancel"
        override val save = "Save"

    }
    override val screen = object : LocalizedTextsScreen() {
        override val account = object : LocalizedTextsScreenAccount() {
            override val disabled = "disabled"
            override val username = "Username"
            override val password = "Password"
            override val state = "disabled"
            override val enabled = "enabled"
            override val columnDisabled = "Disabled"
        }
        override val keyValue = object : LocalizedTextsScreenKeyValue() {
            override val key = "Key"
            override val value = "Value"
        }
        override val login = object : LocalizedTextsLogin() {
            override val username = "Username"
            override val password = "Password"
            override val login = "Login"
        }
        override val home = object : LocalizedTextsHome() {
            override val contact = "Contact"
            override val logout = "Logout"
            override val title = "Project name"
            override val menuAdmin = "Admin"
            override val menuAccounts = "Accounts"
            override val menuOptions = "Options"
            override val menuKeyValue = "KeyValue"
        }

    }
}

val commonMsg = msg.common
val homeScreenMsg = msg.screen.home // KotlinJS currently sucks, so it must be here