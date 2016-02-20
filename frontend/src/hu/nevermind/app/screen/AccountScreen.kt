package hu.nevermind.app.screen

import com.github.andrewoma.react.*
import hu.nevermind.app.*
import hu.nevermind.app.store.*
import hu.nevermind.reakt.bootstrap.*
import hu.nevermind.reakt.bootstrap.table.*
import kotlin.browser.window

private object AccountScreenIds {
    val screenId = "accountScreen"
    val addButton = "${screenId}_addButton"
    object table {
        val id = "${screenId}_table"
        object row {
            val editButton: (Int)->String = {rowIndex -> "${id}_editButton_$rowIndex"}
        }
    }
    object modal {
        val id = "${screenId}_modal"
        object inputs {
            val username = "${id}_username"
            val password = "${id}_password"
            val disabled = "${id}_disabled"
        }
        object buttons {
            val save = "${id}_saveButton"
            val close = "${id}_closeButton"
        }
    }
}

class AccountScreen : ComponentSpec<Unit, Unit>() {

    companion object {
        val factory = react.createFactory(AccountScreen())
    }

    override fun componentDidMount() {
        AccountStore.addChangeListener(this) {
            forceUpdate()
        }
    }

    override fun componentWillUnmount() {
        AccountStore.removeListener(this)
    }

    override fun Component.render() {
        val accounts = AccountStore.accounts()
        val editingAccount = AccountStore.editingAccount
        var rowIndex = 0
        bsGrid({ id = AccountScreenIds.screenId }) {
            bsButton ({
                id = AccountScreenIds.addButton
                bsStyle = BsStyle.Primary
                onClick = { Actions.setEditingAccount(globalDispatcher, Account("", false, arrayListOf(), "")) }
            }) { text("Hozzáadás") }
            bsRow {
                bsCol ({ md = 10 }) {
                    bootstrapTable<Account>({
                        data = accounts.toTypedArray()
                        hover = true
                        selectRow = SelectRowProp(SelectionMode.radio, clickToSelect = true, hideSelectColumn = true)
                        search = true
                    }) {
                        tableHeaderColumn<Account, Unit>({
                            width = "50"
                            dataFormat = { cell, account ->
                                createReactElement {
                                    bsButtonGroup ({ bsSize = BsSize.ExtraSmall }) {
                                        bsButton ({
                                            id = AccountScreenIds.table.row.editButton(rowIndex)
                                            bsStyle = BsStyle.Primary
                                            onClick = {
                                                window.location.hash = Path.account.withOpenedEditorModal(account.username)
                                            }
                                        }) { text("Szerkesztés") }
                                    }
                                }
                            }
                        })
                        tableHeaderColumn<Account, String>({
                            isKey = true
                            dataField = "username"
                            width = "100"
                        }) { text("Username") }
                        tableHeaderColumn<Account, String>({
                            width = "75"
                            dataFormat = { cell, account ->
                                createReactElement {
                                    bsInput({
                                        type = InputType.Checkbox
                                        checked = if (account.disabled) "checked" else ""
                                        readOnly = true
                                    })
                                }
                            }
                        }) { text("Disabled") }
                    }
                }
            }
            if (editingAccount != null) {
                fun closeModal(result: ModalResult, entity: Account?) {
                    if (result == ModalResult.Save) {
                        Actions.modifyAccount(globalDispatcher, entity!!)
                    }
                    Actions.setEditingAccount(globalDispatcher, null)
                    window.location.hash = Path.account.root
                }
                editorDialog(AccountEditorDialogProps(editingAccount, ::closeModal))
            }
        }
    }
}


data class AccountEditorDialogProps(val editedAccount: Account, val close: (ModalResult, Account?) -> Unit)
data class AccountEditorDialogState(val editedAccount: Account)

class AccountEditorDialog() : ComponentSpec<AccountEditorDialogProps, AccountEditorDialogState>() {

    companion object {
        val factory = react.createFactory(AccountEditorDialog())
    }

    override fun componentDidMount() {
        AccountStore.addChangeListener(this) {
            val newAccount = AccountStore.editingAccount
            if (newAccount != null) {
                state = AccountEditorDialogState(newAccount.copy())
            }
        }
    }

    override fun componentWillUnmount() {
        AccountStore.removeListener(this)
    }

    override fun initialState(): AccountEditorDialogState? {
        return AccountEditorDialogState(props.editedAccount)
    }

    private fun updateEntity(event: FormEvent, updater: (String) -> Account) {
        val value = event.currentTarget.value
        state = AccountEditorDialogState(updater(value))
    }

    override fun Component.render() {
        val account = state.editedAccount
        val errors = hashMapOf<String, String>()
        fillWithErrors(errors)
        bsModal ({
            id = AccountScreenIds.modal.id
            show = true
            onHide = { props.close(ModalResult.Close, null) }
        }) {
            bsModalHeader ({ closeButton = true }) {
                bsModalTitle { text("Szerkesztés") }
            }
            bsModalBody ({ closeButton = true }) {
                bsRow {
                    bsCol({ md = 12 }) {
                        form {
                            bsRow {
                                bsCol({ md = 4 }) {
                                    bsInput({
                                        id = AccountScreenIds.modal.inputs.username
                                        type = InputType.Text
                                        label = "Username"
                                        defaultValue = account.username
                                        onChange = {
                                            updateEntity(it) { value -> account.copy(username = value) }
                                        }
                                        errors["username"]?.let { errorMessage ->
                                            bsStyle = BsStyle.Error
                                            help = errorMessage
                                        }
                                    })
                                }
                                bsCol({ md = 4 }) {
                                    bsInput({
                                        id = AccountScreenIds.modal.inputs.password
                                        type = InputType.Password
                                        label = "Password"
                                        defaultValue = ""
                                        onChange = {
                                            updateEntity(it) { value -> account.copy(plainPassword = value) }
                                        }
                                        errors["password"]?.let { errorMessage ->
                                            bsStyle = BsStyle.Error
                                            help = errorMessage
                                        }
                                    })
                                }
                                bsCol({ md = 4 }) {
                                    bsInput({
                                        id = AccountScreenIds.modal.inputs.disabled
                                        type = InputType.Checkbox
                                        checked = account.disabled
                                        label = "Disabled"
                                        onChange = { event ->
                                            val checked = event.currentTarget.asDynamic().checked
                                            state = AccountEditorDialogState(account.copy(disabled = checked))
                                        }
                                    })
                                }
                            }
                            bsRow {
                                bsCol({ md = 4 }) {
                                    bsInput({
                                        type = InputType.Select
                                        onChange = {
                                            console.log(it)
                                            //updateEntity(it) {value -> console.log(value) }
                                        }
                                    }) {
                                        Role.values().forEach {  role ->
                                            option({selected = account.hasRole(role)}) {
                                                text(role.name)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            bsModalFooter {
                bsButtonGroup {
                    if (errors.isEmpty()) {
                        bsButton ({
                            id = AccountScreenIds.modal.buttons.save
                            bsStyle = BsStyle.Success
                            onClick = { props.close(ModalResult.Save, state.editedAccount) }
                        }) { text("Mentés") }
                    }
                    bsButton ({
                        id = AccountScreenIds.modal.buttons.close
                        bsStyle = BsStyle.Danger
                        onClick = { props.close(ModalResult.Close, null) }
                    }) { text("Mégsem") }
                }
            }
        }
    }

    private fun fillWithErrors(errors: MutableMap<String, String>) {
        val account = state.editedAccount
        arrayOf("username" to account.username,
                "password" to account.plainPassword).forEach {
            val errorMessages = validate(it.second, Min(3), Max(100))
            if (errorMessages.isNotEmpty()) {
                errors[it.first] = errorMessages.joinToString("\n")
            }
        }
    }
}

private fun Component.editorDialog(props: AccountEditorDialogProps): Component {
    return constructAndInsert(Component({ AccountEditorDialog.factory(Ref(props)) }))
}

fun Component.accountScreen(): Component {
    return constructAndInsert(Component({ AccountScreen.factory(Ref(null)) }))
}