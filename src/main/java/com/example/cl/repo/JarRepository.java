package com.example.cl.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cl.entity.Jar;

public interface JarRepository extends JpaRepository<Jar, Long> {
}
