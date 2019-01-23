package com.sweeneyb.otus.students.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sweeneyb.otus.students.model.Student
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Service
class StudentsService {
    constructor()

    lateinit var students: List<Student>
    lateinit var courses: Map<String, String>

    init {
        var studentUrl = "https://gist.githubusercontent.com/edotus/bd63eefb9b4b1eacb641811f9a1a780d/raw/60e04520584f7a436917b0d5be2b6c18f039fadb/students_classes.json"
        val data = WebClient.create(studentUrl).get().retrieve().bodyToMono(String::class.java).block()
        val mapper = ObjectMapper()
        val nodes = mapper.readTree(data)
        val classes = nodes.get("classes")
        courses = mapper.readValue(classes.toString())
        println(courses)

        val studentData = nodes.get("students")
        students = mapper.treeToValue(studentData, Array<Student>::class.java).asList()
    }

    fun filterForStudent(first: Optional<String>, last: Optional<String>) : List<Student>{
        var filteredList = students
        if (first.isPresent) {
            filteredList = filteredList.filter { first.get().equals(it.first )}
        }
        if (last.isPresent) {
            filteredList = filteredList.filter { last.get().equals(it.last )}
        }
        return filteredList
    }

    @Bean
    fun getStudentData() = students

    @Bean
    fun getClasses() = courses

}
