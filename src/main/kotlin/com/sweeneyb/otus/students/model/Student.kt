package com.sweeneyb.otus.students.model

import java.util.*
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement
data class Student(var id: String?, var last: String?, var first: String?, var email: String?, var studentClasses: List<Grade> ){
    constructor() : this(null, null, null, null, Collections.emptyList<Grade>())
    constructor(id: String) : this(id, null, null, null, Collections.emptyList<Grade>())
}

class SearchView(student: Student) {
    val last = student.last
    val first = student.first
    // TODO error handling on grade
    val gpa: Double = (student.studentClasses.foldRight(0.0  ){grade,acc -> acc + grade.grade!!.toDouble()} ) /student.studentClasses.size
}

class DetailsView( student: Student, courses: Map<String, String>)  {
    var email = student.email
    val last = student.last
    val first = student.first
    // TODO error handling on grade
    val gpa: Double = (student.studentClasses.foldRight(0.0  ){grade,acc -> acc + grade.grade!!.toDouble()} ) /student.studentClasses.size
    val studentCourses: List<Pair<String?, String?>> = student.studentClasses.map {courses.get(it.id) to it.grade  }
}

data class Grade(var id: String?, var grade: String?) {
    constructor(): this( null, null)
}

data class Class( var id: String?, var name: String?) {
    constructor(): this( null, null)
}