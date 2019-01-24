package com.sweeneyb.students.controllers

import com.sweeneyb.students.StudentsService
import com.sweeneyb.students.model.Class
import com.sweeneyb.students.model.Student
import com.sweeneyb.students.views.DetailsList
import com.sweeneyb.students.views.DetailsView
import com.sweeneyb.students.views.SearchList
import com.sweeneyb.students.views.SearchView
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.util.*

@RestController
@EnableWebFlux
@Component
@Configuration
open class StudentController {

    @Autowired
    lateinit var studentService: StudentsService

    @Bean
    open fun routeFun(@Autowired courses: Map<String, String>): RouterFunction<ServerResponse> {
        return router {
            // undocumented endpoint
            (GET("/course/{id}")) { request ->
                val courseId = request.pathVariable("id")
                val courseName = courses.get(courseId)
                ok().body(fromObject(Class(courseId, courseName)))
            }
            (GET("/student/search")) {request ->
                val type = resolveContentType(request.headers().accept())
                withContentType(type, SearchList(handleSearch(request) { SearchView(it) }))}
            (GET("/student/details")) {request ->
                val type = resolveContentType(request.headers().accept())
                withContentType(type, DetailsList(handleSearch(request) { DetailsView(it, courses) }))}
        }
    }

    fun resolveContentType(type: List<MediaType>) : MediaType {
        return if(type.contains(MediaType.APPLICATION_XML)) {
             MediaType.APPLICATION_XML
        } else {
            MediaType.APPLICATION_JSON
        }
    }

    fun withContentType(type:MediaType,any: Any): Mono<ServerResponse> = ServerResponse.ok().contentType(type).body(fromObject(any))

    fun <T> handleSearch(request: ServerRequest, toView: (Student) -> T): List<T> {
        return handleSearch(toView)(request)
    }

    fun <T> handleSearch( toView: (Student) -> T): (ServerRequest)->List<T> {
        return {request:ServerRequest ->
            val filtered = studentService.filterForStudent(sanitizeInput(request.queryParam("first")), sanitizeInput(request.queryParam("last")))
            filtered.map { toView(it) }
        }
    }

    fun sanitizeInput(input : Optional<String>) : Optional<String> {
        // really, we'd defer out to a 3rd party library here
        return input
    }

    // old style controller
    // content negotiation is way easier the old way
    @GetMapping("/api/student/{id}", produces = ["application/xml", "application/json"])
    open fun handleDetails(@PathVariable id: String): Mono<Student> {
        val sanitizedId = sanitizeInput(Optional.of(id)).get()
        return Mono.just(Student(sanitizedId))
    }
}