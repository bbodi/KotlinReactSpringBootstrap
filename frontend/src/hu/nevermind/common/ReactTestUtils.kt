package hu.nevermind.common

import org.w3c.dom.Node

@native("React.addons.TestUtils") object ReactTestUtils {
    object Simulate {
        fun click(node: Node)
        fun change(node: Node, event: dynamic = null)
    }
}