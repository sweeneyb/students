package com.sweeneyb.students

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.config.EnableWebFlux
import reactor.netty.http.server.HttpServer
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext
import java.time.Duration

@EnableWebFlux
@Configuration
@Component
@ComponentScan
@EnableAutoConfiguration
open class StudentsApplication {


	@Bean
	@Qualifier("webHandler")
	open fun getServer(context: ApplicationContext): HttpServer {
		val handler = WebHttpHandlerBuilder.applicationContext(context).build()
		val adapter = ReactorHttpHandlerAdapter(handler)
		val server = HttpServer.create().host("0.0.0.0").port(8080)
		return server.handle(adapter)
	}

}
fun main(args: Array<String>) {
	runApplication<StudentsApplication>(*args)
//	val context = AnnotationConfigReactiveWebApplicationContext()
//	context.scan("com.sweeneyb")
//	context.refresh()
//	val handler = WebHttpHandlerBuilder.applicationContext(context).build()
//	val adapter = ReactorHttpHandlerAdapter(handler)
//	val server = HttpServer.create().host("0.0.0.0").port(8080).handle(adapter)
//	server.bindUntilJavaShutdown(Duration.ZERO, null)
}

