package net.minpro.myownflashcard

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class WordDB: RealmObject() {
    @PrimaryKey
    open var strQuestion: String = ""
    open var strAnswer: String = ""
    open var boolMemoryFrag: Boolean = false


}