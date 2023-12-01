package com.example.cl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.example.cl.entity.Jar;
import com.example.cl.repo.JarRepository;
import com.example.interfaces.TestInterface;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TestFactory {

	private TestInterface hello;

	private TestInterface bye;

	private final JarRepository jarRepository;

	public TestFactory(JarRepository jarRepository) {
		this.jarRepository = jarRepository;
	}

	@Bean
	public void initHello() throws
		IOException,
		ClassNotFoundException,
		NoSuchMethodException,
		InvocationTargetException,
		InstantiationException,
		IllegalAccessException {

		jarRepository.save(new Jar("C:/logs/genA-0.0.1-SNAPSHOT.jar"));
		List<Jar> jars = jarRepository.findAll();
		URL[] urls = jars.stream().map(jar -> {
			try {
				return new File(jar.getJarPath()).toURI().toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}).toArray(URL[]::new);

		try (URLClassLoader classLoader = new URLClassLoader(urls)) {
			Class<TestInterface> testInterfaceClass = (Class<TestInterface>)classLoader.loadClass("com.example.Hello");
			TestInterface instance = testInterfaceClass.getDeclaredConstructor().newInstance();

			hello = instance;
		}

		log.info("Create 'Hello' Instance");

		if (jars.size() > 0) {
			jarRepository.delete(jars.get(0));
		}
	}

	@Bean
	public void initBye() throws
		IOException,
		ClassNotFoundException,
		NoSuchMethodException,
		InvocationTargetException,
		InstantiationException,
		IllegalAccessException {

		jarRepository.save(new Jar("C:/logs/genB-0.0.1-SNAPSHOT.jar"));
		List<Jar> jars = jarRepository.findAll();
		URL[] urls = jars.stream().map(jar -> {
			try {
				return new File(jar.getJarPath()).toURI().toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}).toArray(URL[]::new);

		try (URLClassLoader classLoader = new URLClassLoader(urls)) {
			Class<TestInterface> testInterfaceClass = (Class<TestInterface>)classLoader.loadClass("com.example.Bye");
			TestInterface instance = testInterfaceClass.getDeclaredConstructor().newInstance();

			bye = instance;
		}

		log.info("Create 'Bye' Instance");

		if (jars.size() > 0) {
			jarRepository.delete(jars.get(0));
		}
	}

	public TestInterface getHello() {
		return hello;
	}

	public TestInterface getBye() {
		return bye;
	}
}
