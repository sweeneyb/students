package com.sweeneyb.students

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sweeneyb.students.model.Student
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.*
import javax.annotation.PostConstruct

@Service
class StudentsService {

    lateinit var students: List<Student>
    lateinit var courses: Map<String, String>

    @PostConstruct
    fun getData() {
        var studentUrl = "https://gist.githubusercontent.com/edotus/bd63eefb9b4b1eacb641811f9a1a780d/raw/60e04520584f7a436917b0d5be2b6c18f039fadb/students_classes.json"
        val data = WebClient.create(studentUrl).get().retrieve().bodyToMono(String::class.java).block()
        // this is to deal with 2 different types in the same file. That's odd
        val mapper = ObjectMapper()
        val nodes = mapper.readTree(data)
        val classes = nodes.get("classes")
        courses = mapper.readValue(classes.toString())
        println(courses)

        val studentData = nodes.get("students")
        students = mapper.treeToValue(studentData, Array<Student>::class.java).asList()
    }

    // I'm simplifying 'search' to be a filter, but this could easily be a regexp find or a deferal out to a elastisearch service
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

}
