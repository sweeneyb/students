package com.sweeneyb.students.model

import java.util.*
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement
data class Student(var id: String?, var last: String?, var first: String?, var email: String?, var studentClasses: List<Grade> ){
    constructor() : this(null, null, null, null, Collections.emptyList<Grade>())
    constructor(id: String) : this(id, null, null, null, Collections.emptyList<Grade>())
}

data class Grade(var id: String?, var grade: String?) {
    constructor(): this( null, null)
}

data class Class( var id: String?, var name: String?) {
    constructor(): this( null, null)
}