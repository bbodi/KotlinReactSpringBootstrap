package hu.nevermind.reakt

enum class Keys(val keyCode: Int) {
    TAB(9),
    E(69),
    Enter(13),
    Insert(45),
    Del(46),
    F(70),
    D(68),
    ESC(27),
    S(83),
    Q(81),
    UP(38),
    DOWN(40),
    LEFT(37),
    RIGHT(39);


    fun eq(key_code: Double): Boolean {
        return key_code.toInt() == this.keyCode
    }
}