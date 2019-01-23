package com.sweeneyb.otus.students.views

import com.sweeneyb.otus.students.model.Student
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement
class SearchView(student: Student) {
    val last = student.last
    val first = student.first
    // TODO error handling on grade
    val gpa: Double = (student.studentClasses.foldRight(0.0  ){grade,acc -> acc + grade.grade!!.toDouble()} ) /student.studentClasses.size
}

@XmlRootElement
class DetailsView(student: Student, courses: Map<String, String>)  {
    var email = student.email
    val last = student.last
    val first = student.first
    // TODO error handling on grade
    val gpa: Double = (student.studentClasses.foldRight(0.0  ){grade,acc -> acc + grade.grade!!.toDouble()} ) /student.studentClasses.size
    val studentCourses: List<Pair<String?, String?>> = student.studentClasses.map {courses.get(it.id) to it.grade  }
}