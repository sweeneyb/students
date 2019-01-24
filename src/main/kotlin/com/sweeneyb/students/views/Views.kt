package com.sweeneyb.students.views

import com.sweeneyb.students.model.Grade
import com.sweeneyb.students.model.Student
import javax.xml.bind.annotation.XmlRootElement


// This is all gross, hacky, and only half works.  JAXB assumes it can fill stuff in later, which isn't
// really a Kotlin way of doing things.  So this breaks all sorts of ideas.  Implementing a better message encoder
// would be the real way to go.

@XmlRootElement
class SearchView(student: Student?) {
    constructor(): this(null)
    var last = student!!.last
    var first = student!!.first
    // TODO error handling on grade
    var gpa: Double = (student!!.studentClasses.foldRight(0.0  ){grade,acc -> acc + grade.grade!!.toDouble()} ) /student.studentClasses.size
}


@XmlRootElement
class SearchList(var students: List<SearchView>) {
    constructor(): this(ArrayList())
}

@XmlRootElement
class DetailsList(var students: List<DetailsView>) {
    constructor(): this(ArrayList())
}

@XmlRootElement
class DetailsView(@Transient val student: Student?, @Transient val courses: Map<String, String>)  {
    constructor(): this(null, HashMap())
    var email = student!!.email
    var last = student!!.last
    var first = student!!.first
    var gpa: Double = (student!!.studentClasses.foldRight(0.0  ){grade,acc -> acc + grade.grade!!.toDouble()} ) /student.studentClasses.size

//    var studentCourses: List<Pair<String?, String?>> = student!!.studentClasses.map {courses.get(it.id) to it.grade  }
    val studentCourses: List<Grade>
      get() = student!!.studentClasses.map { Grade(courses.get(it.id), it.grade) }

//    var studentCourses: Map<String?, String?>> = student!!.studentClasses.map {courses.get(it.id) to it.grade  }
}