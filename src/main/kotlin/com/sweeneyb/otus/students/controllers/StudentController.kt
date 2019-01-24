package com.sweeneyb.otus.students.controllers

import com.sweeneyb.otus.students.model.Class
import com.sweeneyb.otus.students.model.Student
import com.sweeneyb.otus.students.views.DetailsView
import com.sweeneyb.otus.students.views.SearchView
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import kotlin.reflect.KMutableProperty1

@RestController
@EnableWebFlux
@Component
@Configuration
open class StudentController {

    @Bean
    open fun routeFun(@Autowired courses: Map<String, String>, @Autowired studentService: StudentsService): RouterFunction<ServerResponse> {
        return router {
            (GET("/foo/{id}") and accept(MediaType.APPLICATION_JSON)) { request -> ok().body(fromObject(Student(request.pathVariable("id")))) }
            (GET("/food/{id}") and accept(MediaType.APPLICATION_XML) ) { request -> ok()
                    .contentType(MediaType.APPLICATION_XML)
                 .body(fromObject(Student(request.pathVariable("id")))) }
            (GET("/foo") and accept(MediaType.APPLICATION_XML)) {
                ok().contentType(MediaType.APPLICATION_XML).body(fromObject(Student("7")))
            }
            (GET("/course/{id}")) { request ->
                val courseId = request.pathVariable("id")
                val courseName = courses.get(courseId)
                ok().body(fromObject(Class(courseId, courseName)))
            }
            (GET("/student/last/{name}")) { request ->
                println(request.pathVariable("name"))
                val filtered = studentService.getStudentData().filter { request.pathVariable("name").equals(it.last) }
                ok().body(fromObject(filtered))
            }
//            GET(accept(MediaType.APPLICATION_XML).nest())
            GET("/student/search", handleSearch(studentService) { SearchView(it) })
            GET("/student/details", handleSearch(studentService) { DetailsView(it, courses) } )
        }
    }
    fun handleSearch(studentsService:StudentsService, toView: (Student) -> Any): (ServerRequest)->Mono<ServerResponse> {
        return {request:ServerRequest ->
            val filtered = studentsService.filterForStudent(request.queryParam("first"), request.queryParam("last"))
            val transformed = filtered.map { toView(it) }
            ServerResponse.ok().body(fromObject(transformed))
        }
    }

    fun foo(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok().body(BodyInserters.fromObject(Student("1")))
    }

    @GetMapping("/api/student/{id}")
    open fun handleDetails(@PathVariable id: String): Mono<Student> {
        val sanitizedId = sanitize(id)
        return Mono.just(Student(sanitizedId))
    }

    fun sanitize(input: String): String {
        // TODO
        return input
    }
}