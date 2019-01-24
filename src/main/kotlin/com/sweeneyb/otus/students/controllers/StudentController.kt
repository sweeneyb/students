package com.sweeneyb.otus.students.controllers

import com.sun.security.ntlm.Server
import com.sweeneyb.otus.students.model.Class
import com.sweeneyb.otus.students.model.Student
import com.sweeneyb.otus.students.views.DetailsList
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
import java.awt.image.MemoryImageSource
import javax.print.attribute.standard.Media
import kotlin.reflect.KMutableProperty1

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
//            (GET("/foo/{id}") and accept(MediaType.APPLICATION_JSON)) {request -> handleSearch(request){ SearchView(it)}}
            (GET("/foo/{id}")) {request ->
                val type = resolveContentType(request.headers().accept())
                withContentType(type, handleSearch(request) {SearchView(it)})}
//            { request -> ok().body(fromObject(Student(request.pathVariable("id")))) }
            (GET("/student/search")) {request ->
                val type = resolveContentType(request.headers().accept())
                withContentType(type, handleSearch(request) {SearchView(it)})}
            (GET("/student/details")) {request ->
                val type = resolveContentType(request.headers().accept())
                withContentType(type, handleSearch(request) {DetailsView(it, courses)}.toTypedArray() as Array<DetailsView> )}
//            GET("/student/search", handleSearch { SearchView(it) })
//            GET("/student/details", handleSearch { DetailsView(it, courses) } )
//            GET("/student/search" , handleSearch { SearchView(it) })
//            GET("/student/details", handleSearch { DetailsView(it, courses) } )
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



    fun handleSearch(request: ServerRequest, toView: (Student) -> Any): List<Any> {
        return handleSearch(toView)(request)
    }

    fun handleSearch( toView: (Student) -> Any): (ServerRequest)->List<Any> {
        return {request:ServerRequest ->
            val filtered = studentService.filterForStudent(request.queryParam("first"), request.queryParam("last"))
            filtered.map { toView(it) }
//            ServerResponse.ok().body(fromObject(transformed))
        }
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