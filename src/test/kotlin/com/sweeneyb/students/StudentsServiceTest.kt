package com.sweeneyb.students

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.isEmpty
import com.natpryce.hamkrest.isIn
import com.sweeneyb.students.model.Student
import org.junit.Test
import java.util.*


class StudentsServiceTest {

    val students = listOf<Student>(
            Student("1", "first", "last", "email", emptyList()),
            Student("1", "jon", "doe", "email", emptyList()),
            Student("1", "jon", "bar", "email", emptyList()),
            Student("1", "jane", "doe", "email", emptyList()),
            Student("1", "Ada", "Lovelace", "email", emptyList())
    )

    @Test
    fun `retuns values with empty last name`() {
        val fixture = StudentsService()
        fixture.students = students
        val results = fixture.filterForStudent(Optional.of("jon"), Optional.empty())
        assertThat(students[1], isIn(results) )
        assertThat(students[2], isIn(results) )
    }

    @Test
    fun `returns values with empty first name`() {
        val fixture = StudentsService()
        fixture.students = students
        val results = fixture.filterForStudent(Optional.empty(), Optional.of("doe"))
        assertThat(students[1], isIn(results) )
        assertThat(students[3], isIn(results) )
    }

    @Test
    fun `returns nothing on empty string`() {
        val fixture = StudentsService()
        fixture.students = students
        val results = fixture.filterForStudent(Optional.empty(), Optional.of(""))
        assertThat(results, isEmpty)
    }

    @Test
    fun `loads data`(){
        val fixture = StudentsService()
        fixture.getData()
        assertThat(fixture.students, !isEmpty)
    }
}