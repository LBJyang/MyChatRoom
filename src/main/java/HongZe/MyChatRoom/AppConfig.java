package HongZe.MyChatRoom;

import java.io.File;

import javax.sql.DataSource;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import HongZe.MyChatRoom.orm.MyTemplate;
import HongZe.MyChatRoom.web.ChatHandler;
import HongZe.MyChatRoom.web.ChatHandshakeInterceptor;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.Servlet5Loader;
import io.pebbletemplates.spring.servlet.PebbleViewResolver;
import jakarta.servlet.ServletContext;

@ComponentScan
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:/jdbc.properties")
@EnableWebMvc
@EnableWebSocket
public class AppConfig {

	public static void main(String[] args) throws LifecycleException {
		// TODO Auto-generated method stub
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(Integer.getInteger("port", 8080));
		tomcat.getConnector();

		Context ctx = tomcat.addWebapp("", new File("src/main/webapp").getAbsolutePath());
		WebResourceRoot resources = new StandardRoot(ctx);
		resources.addPreResources(
				new DirResourceSet(resources, "/WEB-INF/classes", new File("target/classes").getAbsolutePath(), "/"));
		ctx.setResources(resources);
		tomcat.start();
		tomcat.getServer().await();
	}

//---jdbc configuration---------------------------------------------------------

	@Bean
	DataSource getDataSource(@Value("${jdbc.url}") String jdbcUrl, @Value("${jdbc.username}") String jdbcUsername,
			@Value("${jdbc.password}") String jdbcPassword) {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(jdbcUsername);
		config.setPassword(jdbcPassword);
		config.addDataSourceProperty("autoCommit", "false");
		config.addDataSourceProperty("connectionTimeout", "5");
		config.addDataSourceProperty("idleTimeout", "60");
		return new HikariDataSource(config);
	}

	@Bean
	JdbcTemplate jdbcTemplate(@Autowired DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	MyTemplate myTemplate(@Autowired JdbcTemplate jdbcTemplate) {
		return new MyTemplate(jdbcTemplate, "HongZe.MyChatRoom.entity");
	}

	@Bean
	PlatformTransactionManager createTxManager(@Autowired DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

//	---mvc configuration-------------------------------------------------------
	@Bean
	WebMvcConfigurer createMvcConfigurer(@Autowired HandlerInterceptor[] interceptors) {
		return new WebMvcConfigurer() {
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				for (var interceptor : interceptors) {
					registry.addInterceptor(interceptor);
				}
			}

			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/static/**").addResourceLocations("/static/");
			}
		};
	}

	@Bean
	ViewResolver createViewResolver(@Autowired ServletContext servletContext) {
		var engine = new PebbleEngine.Builder().autoEscaping(true).cacheActive(false)
				.loader(new Servlet5Loader(servletContext)).build();
		var viewResolver = new PebbleViewResolver(engine);
		viewResolver.setPrefix("/WEB-INF/templates/");
		viewResolver.setSuffix("");
		return viewResolver;
	}

//	---websocket configuration--------------------------------------------------
	@Bean
	WebSocketConfigurer createWebSocketConfigurer(@Autowired ChatHandler chatHandler,
			@Autowired ChatHandshakeInterceptor chatHandshakeInterceptor) {

		return new WebSocketConfigurer() {

			@Override
			public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
				// TODO Auto-generated method stub
				registry.addHandler(chatHandler, "/chat").addInterceptors(chatHandshakeInterceptor);
			}
		};
	}

	@Bean
	ObjectMapper createObjectMapper() {
		var om = new ObjectMapper();
		om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return om;
	}
}
