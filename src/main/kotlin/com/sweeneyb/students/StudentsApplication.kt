package com.sweeneyb.students

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.reactive.HttpHandler
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.stereotype.Component
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.reactive.config.EnableWebFlux
import reactor.netty.http.server.HttpServer
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import org.springframework.web.reactive.DispatcherHandler
import org.springframework.web.reactive.config.DelegatingWebFluxConfiguration
import com.sun.jndi.ldap.LdapCtx.DEFAULT_HOST
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
//		HttpServer.create(host, port).newHandler(adapter).block();
		return server.handle(adapter)
	}

}
fun main(args: Array<String>) {
//	runApplication<StudentsApplication>(*args)
	val context = AnnotationConfigReactiveWebApplicationContext()
	context.scan("com.sweeneyb")
	context.refresh()
//	context.beanDefinitionNames.forEach { println(it) }
//	val context = AnnotationConfigApplicationContext(DelegatingWebFluxConfiguration::class.java) //   DelegatingWebReactiveConfiguration::class.java)  // (1)
	val handler = WebHttpHandlerBuilder.applicationContext(context).build()
////	val handler = DispatcherHandler.toHttpHandler(context)  // (2)
	val adapter = ReactorHttpHandlerAdapter(handler)
	val server = HttpServer.create().host("0.0.0.0").port(8080).handle(adapter)
	server.bindUntilJavaShutdown(Duration.ZERO, null)
}

