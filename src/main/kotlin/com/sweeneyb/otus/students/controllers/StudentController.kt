package com.sweeneyb.otus.students.controllers

import com.sun.security.ntlm.Server
import com.sweeneyb.otus.students.model.Class
import com.sweeneyb.otus.students.model.DetailsView
import com.sweeneyb.otus.students.model.SearchView
import com.sweeneyb.otus.students.model.Student
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.BodyInserter
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.core.publisher.Mono
import java.util.*
import java.util.function.Predicate
import javax.xml.soap.Detail
import kotlin.properties.Delegates
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty

@RestController
@EnableWebFlux
@Component
@Configuration
open class StudentController {

    @Bean
    open fun routeFun(@Autowired courses: Map<String, String>, @Autowired students: List<Student>): RouterFunction<ServerResponse> {
        return router {
            (GET("/foo/{id}") and accept(MediaType.APPLICATION_JSON)) { request -> ok().body(fromObject(Student(request.pathVariable("id")))) }
            (GET("/foo/{id}") and accept(MediaType.APPLICATION_XML)) { request -> ok().contentType(MediaType.APPLICATION_XML).body(fromObject(Student(request.pathVariable("id")))) }
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
                val filtered = students.filter { request.pathVariable("name").equals(it.last) }
                ok().body(fromObject(filtered))
            }
            GET("/student/search", handleSearch(students) { SearchView(it)})
            GET("/student/details", handleSearch(students ) {DetailsView(it, courses)} )
        }
    }
    fun handleSearch(students:List<Student>, xform: (Student) -> Any): (ServerRequest)->Mono<ServerResponse> {
        return {request:ServerRequest ->
            val firstFilter = getFilterForParam(request, "first", Student::first)
            val lastFilter = getFilterForParam(request, "last", Student::last)
            println(request.queryParam("first"))
            val filtered = students.filter(firstFilter).filter(lastFilter)
            val transformed = filtered.map { xform(it) }
            ServerResponse.ok().body(fromObject(transformed))
        }
    }

    fun getFilterForParam(request: ServerRequest, param: String, toMatch: KMutableProperty1<Student, String?>): (Student) -> Boolean {
        val queryParam = request.queryParam(param)
        return when (queryParam.isPresent) {
            true -> { item: Student -> queryParam.get().equals(toMatch.get(item)) }
            false -> { _ -> true }
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