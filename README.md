# testClassLoader

# ClassLoader
```
This class loader is used to load classes and resources from a search path of URLs referring to both JAR files and directories.
```
참조 : [docs-oracle](https://docs.oracle.com/javase/8/docs/api/java/net/URLClassLoader.html)


```
  Class loaders are responsible for loading Java classes dynamically to the JVM (Java Virtual Machine) during runtime.
```
참조 : [Baeldung](https://www.baeldung.com/java-classloaders)

Java Application의 Runtime에 외부 경로의 Java Class를 동적으로 이용할 수 있는 기능 제공

## 구현 예시
1. Loading할 Class의 Interface 정의
``` java
package com.example.interfaces;

public interface TestInterface {
	String print();
	String print(String message);
}

```

2. Loading할 Class를 Interface에 맞춰 구현
  - Hello
``` java
package com.example;

import com.example.interfaces.TestInterface;

public class Hello implements TestInterface {
	@Override
	public String print() {
		String hello = "Hello World!";
		System.out.println(hello);
		return hello;
	}

	@Override
	public String print(String s) {
		String hello = String.format("Hello %s!", s);
		System.out.println(hello);

		return hello;
	}
}

```

  - Bye
``` java
package com.example;

import com.example.interfaces.TestInterface;

public class Bye implements TestInterface {
	@Override
	public String print() {
		String bye = "Good Bye world!";
		System.out.println(bye);
		return bye;
	}

	@Override
	public String print(String s) {
		String bye = String.format("Good Bye %s!", s);
		System.out.println(bye);

		return bye;
	}
}

```

3. ClassLoading
  - TestFactory
``` java
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


  	// 'Hello' Class(Instance) Loading 
	@PostConstruct
	public void initHello() throws
		IOException,
		ClassNotFoundException,
		NoSuchMethodException,
		InvocationTargetException,
		InstantiationException,
		IllegalAccessException {

		// 1. DB를 통해 Class Loading할 Jar 경로를 조회하는 시나리오
		jarRepository.save(new Jar("C:/libs/genA-0.0.1-SNAPSHOT.jar"));
		List<Jar> jars = jarRepository.findAll();
		URL[] urls = jars.stream().map(jar -> {
			try {
				return new File(jar.getJarPath()).toURI().toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}).toArray(URL[]::new);

		// 2. Hello Class Instance 생성 후 TestInterface 객체로 저장  
		try (URLClassLoader classLoader = new URLClassLoader(urls)) {
			// 2-1. Loading 할 Class의 타입을 알 수 없기 때문에 강제타입변환(Casting) 필요
			Class<TestInterface> testInterfaceClass = (Class<TestInterface>)classLoader.loadClass("com.example.Hello");
			TestInterface instance = testInterfaceClass.getDeclaredConstructor().newInstance();

			hello = instance;
		}

		log.info("Create 'Hello' Instance");

		if (jars.size() > 0) {
			jarRepository.delete(jars.get(0));
		}
	}

	  // 'Bye' Class(Instance) Loading (Hello Class와 동일 과정 진행, TestInerface bye 객체에 저장)
	@PostConstruct
	public void initBye() throws
		IOException,
		ClassNotFoundException,
		NoSuchMethodException,
		InvocationTargetException,
		InstantiationException,
		IllegalAccessException {

		jarRepository.save(new Jar("C:/libs/genB-0.0.1-SNAPSHOT.jar"));
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

```

4. Loading한 Class 이용
  - TestController
``` java
package com.example.cl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	private final TestFactory testFactory;

	public TestController(TestFactory testFactory) {
		this.testFactory = testFactory;
	}

  	// '/hello?message={message}' URI 호출 시 `Hello {message}!` 문구가 console 출력 & response로 반환
	@GetMapping("/hello")
	ResponseEntity<String> hello(String message) {
		return ResponseEntity.ok(testFactory.getHello().print(message));
	}

  	// '/bye?message={message}' URI 호출 시 `Good Bye {message}!` 문구가 console 출력 & response로 반환
	@GetMapping("/bye")
	ResponseEntity<String> bye(String message) {
		return ResponseEntity.ok(testFactory.getBye().print(message));
	}
}
```

## 정리
1. Loading할 Class의 Interface 정의 (TestInterface)
2. Loading하여 이용할 Class 정의 (Hello, Bye)
3. 외부 경로에 실제로 작동할 Java Application에서 Loading할 Class(Jar)를 Upload
4. ClassLoading(TestFactory)

## @PostConstruct
```
Be aware that @PostConstruct and initialization methods in general are executed within the container’s singleton creation lock. The bean instance is only considered as fully initialized and ready to be published to others after returning from the @PostConstruct method. 
```
참조 : [docs-spring]([https://docs.spring.io/spring-framework/reference/core/beans/annotation-config/postconstruct-and-predestroy-annotations.html](https://docs.spring.io/spring-framework/reference/core/beans/factory-nature.html#beans-factory-lifecycle-combined-effects))

```
Spring calls the methods annotated with @PostConstruct only once, just after the initialization of bean properties. 
```
참조 : [Baeldung](https://www.baeldung.com/spring-postconstruct-predestroy#postConstruct)

`@PostConstruct`을 이용하면 `Bean` 설정 직후에 진행할 작업을 지정하여 수행할 수 있음

## 문제점
``` java
  Class<TestInterface> testInterfaceClass = (Class<TestInterface>)classLoader.loadClass("com.example.Hello");
  TestInterface instance = testInterfaceClass.getDeclaredConstructor().newInstance();
```

1. 일반적인 구현에서 벗어난 대가 : ClassLoading이 이루어지는 부분에서 다음과 같은 Exception이 발생할 수 있음
- ClassNotFoundException
- NoSuchMethodException
- InvocationTargetException
- InstantiationException
- IllegalAccessException
2. 보안 취약성 증가 : 외부 경로의 Class 바꿔치기를 통해 Class Loading을 받아 이용하는 Main Application에서 특정 기능을 수행하도록 악용할 수 있는 가능성이 열림
3. 유지보수 복잡도 증가 : Interface 등의 구현 없이 Class Loading을 이용하게 된다면 더욱 많은 Class/Method Name들이 Hard Cording 된 Application이 탄생할 여지가 있음

## 대안
- 추가 기능을 수행하는 `별도의 어플리케이션`을 구현하여, 기능 수행 요청/결과 응답 받는 구조 채택
- Message Queue, In-Memory DB(Redis), Process Context Switching
