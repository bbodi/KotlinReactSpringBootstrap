package hu.nevermind.common

import org.w3c.dom.Node

@Suppress("UNUSED_PARAMETER")
@native("React.addons.TestUtils") object ReactTestUtils {
    object Simulate {
        fun click(node: Node) = noImpl
        fun change(node: Node, event: dynamic = null) = noImpl
    }
}