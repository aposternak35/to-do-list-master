package com.example.demo.repository;

import com.example.demo.model.TDList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListRepository extends JpaRepository<TDList, Long> {

    List<TDList> findAll();

    List<TDList> findByUserEmail(String userEmail);

    TDList findByIdEquals(Long id);
}
