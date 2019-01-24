package com.sweeneyb.students.views

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.sweeneyb.students.model.Grade
import com.sweeneyb.students.model.Student
import org.junit.Test


class DetailsViewTest {

    @Test
    fun `should inject course names`() {
        val student = Student("1", "first", "last", "email", listOf(Grade("1", "4.0")))
        val courseMap = mapOf<String, String>("1" to "foo", "2" to "bar")
        val fixture = DetailsView(student, courseMap)
        assertThat(fixture.studentCourses[0].id,  equalTo("foo") )
        assertThat(fixture.studentCourses.size, equalTo(1))
    }
}