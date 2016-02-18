package hu.nevermind.reakt.bootstrap

import com.github.andrewoma.react.*
import org.w3c.dom.events.Event

fun <P> Component.externalReactClass(thisComp: ReactComponent<P, *>,
                                     prop: P,
                                     properties: P.() -> Unit = {},
                                     init: Component.() -> Unit = {}) {
    this.constructAndInsert(Component({
        react.createElement(thisComp, initProps(prop, properties), it.transformChildren())
    }), init)
}

fun <P> Component.externalReactClassWithoutChild(thisComp: ReactComponent<P, *>,
                                                 prop: P,
                                                 properties: P.() -> Unit = {},
                                                 init: Component.() -> Unit = {}) {
    this.constructAndInsert(Component({
        react.createElement(thisComp, initProps(prop, properties), null)
    }), init)
}

fun createReactElement(init: Component.() -> Unit): Any {
    return Component({ 0 }).run {
        this.init()
        this.children[0].transform()
    }
}

object BsStyle {
    val Primary: BsStyle = js("'primary'")
    val Default: BsStyle = js("'default'")
    val Success: BsStyle = js("'success'")
    val Info: BsStyle = js("'info'")
    val Warning: BsStyle = js("'warning'")
    val Danger: BsStyle = js("'danger'")
    val Error: BsStyle = js("'error'")
    val Link: BsStyle = js("'link'")
}

object BsSize {
    val Large: BsStyle = js("'Large'")
    val Small: BsStyle = js("'small'")
    val ExtraSmall: BsStyle = js("'xsmall'")
}

class MenuItemProperties {
    var eventKey: Any by Property()
    var onClick: () -> Unit by Property()
}

fun Component.bsMenuItem(
        properties: MenuItemProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass(
            js("ReactBootstrap.MenuItem"),
            MenuItemProperties(), properties,
            init)
}

fun Component.bsMenuItemDivider(
        properties: MenuItemProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClassWithoutChild(
            js("ReactBootstrap.MenuItem"),
            MenuItemProperties(), properties,
            init)
}

class ButtonToolbarProperties {
}

fun Component.bsButtonToolbar(
        properties: ButtonToolbarProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass<ButtonToolbarProperties>(
            js("ReactBootstrap.ButtonToolbar"),
            ButtonToolbarProperties(), properties,
            init)
}

class ButtonGroupProperties {
    var bsSize: BsStyle by Property()
    var vertical: Boolean by Property()
    var block: Boolean by Property()
    var justified: Boolean by Property()
}

fun Component.bsButtonGroup(
        properties: ButtonGroupProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass(
            js("ReactBootstrap.ButtonGroup"),
            ButtonGroupProperties(), properties,
            init)
}

class ButtonProperties : HtmlGlobalProperties() {
    var bsStyle: BsStyle by Property()
    var bsSize: BsStyle by Property()
    var block: Boolean by Property()
    var active: Boolean by Property()
    var disabled: Boolean by Property()
    var href: String by Property()
}

fun Component.bsButton(
        properties: ButtonProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass(
            js("ReactBootstrap.Button"),
            ButtonProperties(), properties,
            init)
}

class DropdownButtonProperties {
    var bsStyle: BsStyle by Property()
    var bsSize: BsStyle by Property()
    var block: Boolean by Property()
    var active: Boolean by Property()
    var disabled: Boolean by Property()
    var href: String by Property()
    var title: String by Property()
}

fun Component.bsDropdownButton(
        properties: DropdownButtonProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass(
            js("ReactBootstrap.DropdownButton"),
            DropdownButtonProperties(), properties,
            init)
}


class GridProperties : HtmlGlobalProperties() {
    var fluid: Boolean by Property()
}

fun Component.bsGrid(
        properties: GridProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass(
            js("ReactBootstrap.Grid"),
            GridProperties(), properties,
            init)
}

class RowProperties {

}

fun Component.bsRow(
        properties: RowProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass<RowProperties>(
            js("ReactBootstrap.Row"),
            RowProperties(), properties,
            init)
}

class ColProperties {

    var lg: Int by Property()
    var lgHidden: Boolean by Property()
    var lgOffset: Int by Property()
    var lgPush: Int by Property()
    var lgPull: Int by Property()

    var md: Int by Property()
    var mdHidden: Boolean by Property()
    var mdOffset: Int by Property()
    var mdPush: Int by Property()
    var mdPull: Int by Property()

    var sm: Int by Property()
    var smHidden: Boolean by Property()
    var smOffset: Int by Property()
    var smPush: Int by Property()
    var smPull: Int by Property()

    var xs: Int by Property()
    var xsHidden: Boolean by Property()
    var xsOffset: Int by Property()
    var xsPush: Int by Property()
    var xsPull: Int by Property()
}

fun Component.bsCol(
        properties: ColProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass<ColProperties>(
            js("ReactBootstrap.Col"),
            ColProperties(), properties,
            init)
}


class NavbarProperties() {

}

class HeaderProperties() {

}

class BrandProperties() {

}

fun Component.bsNavbarHeader(
        properties: HeaderProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass<HeaderProperties>(
            js("ReactBootstrap.Navbar. Header"),
            HeaderProperties(), properties,
            init)
}


fun Component.bsNavbarBrand(
        properties: BrandProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass<BrandProperties>(
            js("ReactBootstrap.Navbar. Brand"),
            BrandProperties(), properties,
            init)
}

fun Component.bsNavbar(
        properties: NavbarProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass<NavbarProperties>(
            js("ReactBootstrap.Navbar"),
            NavbarProperties(), properties,
            init)
}

class NavProperties {
    var pullRight: Boolean by Property()
    var activeKey: Any by Property()
}

fun Component.bsNav(
        properties: NavProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass<NavProperties>(
            js("ReactBootstrap.Nav"),
            NavProperties(), properties,
            init)
}

val nullHref = js("'javascript:;'")

class NavItemProperties : HtmlGlobalProperties() {
    var eventKey: Any by Property()
    var href: String by Property()
    var active: Boolean by Property()
    var divider: Boolean by Property()
}

fun Component.bsNavItem(
        properties: NavItemProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass<NavItemProperties>(
            js("ReactBootstrap.NavItem"),
            NavItemProperties(), properties,
            init)
}

class NavDropdownProperties : HtmlGlobalProperties() {
    var eventKey: Any by Property()
    var title: String by Property()
}

fun Component.bsNavDropdown(
        properties: NavDropdownProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass<NavDropdownProperties>(
            js("ReactBootstrap.NavDropdown"),
            NavDropdownProperties(), properties,
            init)
}


class ModalProperties : HtmlGlobalProperties() {
    var show: Boolean by Property()
    var onHide: () -> Unit by Property()
}

fun Component.bsModal(
        properties: ModalProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass<ModalProperties>(
            js("ReactBootstrap.Modal"),
            ModalProperties(), properties,
            init)
}

class ModalHeaderProperties {
    var closeButton: Boolean by Property()
}

fun Component.bsModalHeader(
        properties: ModalHeaderProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass<ModalHeaderProperties>(
            js("ReactBootstrap.ModalHeader"),
            ModalHeaderProperties(), properties,
            init)
}

class ModalFooterProperties {
    var closeButton: Boolean by Property()
}

fun Component.bsModalFooter(
        properties: ModalFooterProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass<ModalFooterProperties>(
            js("ReactBootstrap.ModalFooter"),
            ModalFooterProperties(), properties,
            init)
}

class ModalBodyProperties {
    var closeButton: Boolean by Property()
}

fun Component.bsModalBody(
        properties: ModalBodyProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass<ModalBodyProperties>(
            js("ReactBootstrap.ModalBody"),
            ModalBodyProperties(), properties,
            init)
}

class ModalTitleProperties {
    var closeButton: Boolean by Property()
}

fun Component.bsModalTitle(
        properties: ModalTitleProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClass<ModalTitleProperties>(
            js("ReactBootstrap.ModalTitle"),
            ModalTitleProperties(), properties,
            init)
}

object InputType {
    val Text: InputType = js("'text'")
    val Button: InputType = js("'button'")
    val Color: InputType = js("'color'")
    val Date: InputType = js("'date'")
    val Datetime: InputType = js("'datetime'")
    val Hidden: InputType = js("'hidden'")
    val Number: InputType = js("'number'")
    val Password: InputType = js("'password'")
    val Textarea: InputType = js("'textarea'")
    val Checkbox: InputType = js("'checkbox'")
}

class InputProperties : HtmlGlobalProperties() {
    var type: InputType by Property()
    var label: String by Property()
    var bsStyle: BsStyle by Property()
    var value: String by Property()
    var help: String by Property()
    var defaultValue: String by Property()
    var wrapperClassName: String by Property()
    var labelClassName: String by Property()

}

fun Component.bsInput(
        properties: InputProperties.() -> Unit = {},
        init: Component.() -> Unit = {}) {
    externalReactClassWithoutChild(
            js("ReactBootstrap.Input"),
            InputProperties(), properties,
            init)
}