package hu.nevermind.app.screen

import com.github.andrewoma.react.Component
import com.github.andrewoma.react.ComponentSpec
import com.github.andrewoma.react.FormEvent
import com.github.andrewoma.react.InputType
import com.github.andrewoma.react.Ref
import com.github.andrewoma.react.form
import com.github.andrewoma.react.option
import com.github.andrewoma.react.react
import com.github.andrewoma.react.text
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
            val editButton: (Int) -> String = { rowIndex -> "${id}_editButton_$rowIndex" }
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

private val accountScreenMsg = msg.screen.account

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
            addButton()
            bsRow {
                bsCol ({ md = 10 }) {
                    bootstrapTable<Account>({
                        data = accounts.toTypedArray()
                        hover = true
                        selectRow = SelectRowProp(SelectionMode.radio, clickToSelect = true, hideSelectColumn = true)
                        search = true
                    }) {
                        buttonColumn(rowIndex++)
                        usernameColumn()
                        stateColumn()
                    }
                }
            }
            if (editingAccount != null) {
                editorDialog(editingAccount)
            }
        }
    }

    private fun Component.addButton() {
        bsButton ({
            id = AccountScreenIds.addButton
            bsStyle = BsStyle.Primary
            onClick = { Actions.setEditingAccount(globalDispatcher, EditingAccount(Account("", false, Role.ROLE_USER, ""), true)) }
        }) { text(commonMsg.add) }
    }

    private fun Component.buttonColumn(rowIndex: Int) {
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
                        }) { text(commonMsg.edit) }
                    }
                }
            }
        })
    }

    private fun Component.usernameColumn() {
        tableHeaderColumn<Account, String>({
            isKey = true
            dataField = "username"
            width = "100"
        }) { text(accountScreenMsg.username) }
    }

    private fun Component.stateColumn() {
        tableHeaderColumn<Account, String>({
            width = "75"
            dataFormat = { cell, account ->
                createReactElement {
                    if (account.disabled) {
                        bsLabel({ bsStyle = BsStyle.Error }) { text(accountScreenMsg.disabled) }
                    } else {
                        bsLabel({ bsStyle = BsStyle.Success }) {text(accountScreenMsg.enabled)}
                    }
                }
            }
        }) { text(accountScreenMsg.state) }
    }

    private fun Component.editorDialog(editingAccount: EditingAccount) {
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


data class AccountEditorDialogProps(val editedAccount: EditingAccount, val close: (ModalResult, Account?) -> Unit)
data class AccountEditorDialogState(val editedAccount: Account)

class AccountEditorDialog() : ComponentSpec<AccountEditorDialogProps, AccountEditorDialogState>() {

    companion object {
        val factory = react.createFactory(AccountEditorDialog())
    }

    override fun componentDidMount() {
        AccountStore.addChangeListener(this) {
            val newAccount = AccountStore.editingAccount
            if (newAccount != null) {
                state = AccountEditorDialogState(newAccount.account.copy())
            }
        }
    }

    override fun componentWillUnmount() {
        AccountStore.removeListener(this)
    }

    override fun initialState(): AccountEditorDialogState? {
        return AccountEditorDialogState(props.editedAccount.account)
    }

    private fun updateEntity(event: FormEvent, updater: (String) -> Account) {
        val value = event.currentTarget.value
        state = AccountEditorDialogState(updater(value))
    }

    override fun Component.render() {
        bsModal ({
            id = AccountScreenIds.modal.id
            show = true
            onHide = { props.close(ModalResult.Close, null) }
        }) {
            bsModalHeader ({ closeButton = true }) {
                bsModalTitle { text(commonMsg.edit) }
            }
            val errors = hashMapOf<String, String>()
            fillWithErrors(errors)
            body(errors)
            footer(errors)

        }
    }

    private fun Component.body(errors: Map<String, String>) {
        val account = state.editedAccount
        bsModalBody ({ closeButton = true }) {
            bsRow {
                bsCol({ md = 12 }) {
                    form {
                        bsRow {
                            bsCol({ md = 4 }) {
                                usernameInput(account, errors)
                            }
                            bsCol({ md = 4 }) {
                                passwordInput(account, errors)
                            }
                            bsCol({ md = 4 }) {
                                disabledInput(account)
                            }
                        }
                        bsRow {
                            bsCol({ md = 4 }) {
                                roleInput(account)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun Component.usernameInput(account: Account, errors: Map<String, String>) {
        bsInput({
            id = AccountScreenIds.modal.inputs.username
            type = InputType.Text
            label = accountScreenMsg.username
            if (!props.editedAccount.new) {
                readOnly = true
            }
            autoComplete = "off"
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

    private fun Component.passwordInput(account: Account, errors: Map<String, String>) {
        bsInput({
            id = AccountScreenIds.modal.inputs.password
            type = InputType.Password
            label = accountScreenMsg.password
            autoComplete = "off"
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

    private fun Component.disabledInput(account: Account) {
        bsInput({
            id = AccountScreenIds.modal.inputs.disabled
            type = InputType.Checkbox
            checked = account.disabled
            label = accountScreenMsg.columnDisabled
            onChange = { event ->
                val checked = event.currentTarget.asDynamic().checked
                state = AccountEditorDialogState(account.copy(disabled = checked))
            }
        })
    }

    private fun Component.roleInput(account: Account) {
        bsInput({
            type = InputType.Select
            defaultValue = account.role
            onChange = {
                updateEntity(it) {value ->
                    account.copy(role = Role.valueOf(value))
                }
            }
        }) {
            Role.values().forEach { role ->
                option({value = role.name}) {
                    text(role.name)
                }
            }
        }
    }

    private fun Component.footer(errors: Map<String, String>) {
        bsModalFooter {
            bsButtonGroup {
                if (errors.isEmpty()) {
                    bsButton ({
                        id = AccountScreenIds.modal.buttons.save
                        bsStyle = BsStyle.Success
                        onClick = { props.close(ModalResult.Save, state.editedAccount) }
                    }) { text(commonMsg.save) }
                }
                bsButton ({
                    id = AccountScreenIds.modal.buttons.close
                    bsStyle = BsStyle.Danger
                    onClick = { props.close(ModalResult.Close, null) }
                }) { text(commonMsg.cancel) }
            }
        }
    }





    private fun fillWithErrors(errors: MutableMap<String, String>) {
        val account = state.editedAccount
        var errorMsgList = validate(account.username, Min(3), Max(100))
        if (errorMsgList.isNotEmpty()) {
            errors["username"] = errorMsgList.joinToString("\n")
        }
        errorMsgList = validate(account.plainPassword, EmptyOr(Min(3)), Max(100))
        if (errorMsgList.isNotEmpty()) {
            errors["password"] = errorMsgList.joinToString("\n")
        }
    }
}

private fun Component.editorDialog(props: AccountEditorDialogProps): Component {
    return constructAndInsert(Component({ AccountEditorDialog.factory(Ref(props)) }))
}

fun Component.accountScreen(): Component {
    return constructAndInsert(Component({ AccountScreen.factory(Ref(null)) }))
}