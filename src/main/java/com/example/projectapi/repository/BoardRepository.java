package com.example.projectapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.projectapi.model.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board,Integer> {

}
