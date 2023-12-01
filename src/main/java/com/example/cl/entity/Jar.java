package com.example.cl.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Jar {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String jarPath;

	public Jar(String jarPath) {
		this.jarPath = jarPath;
	}

	public Jar() {
	}

	public Long getId() {
		return id;
	}

	public String getJarPath() {
		return jarPath;
	}
}
