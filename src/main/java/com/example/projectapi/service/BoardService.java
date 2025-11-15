package com.example.projectapi.service;

import com.example.projectapi.model.Board;
import com.example.projectapi.repository.BoardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<Board> findAllByProjectId(Integer projectId) {
        return boardRepository.findByProjectId(projectId);
    }

    public Optional<Board> findById(Integer id) {
        return boardRepository.findById(id);
    }

    public Board create(Board board) {
        return boardRepository.save(board);
    }

    public Board update(Integer id, Board updatedBoard) {
        return boardRepository.findById(id)
                .map(board -> {
                    board.setName(updatedBoard.getName());
                    return boardRepository.save(board);
                })
                .orElseThrow(() -> new RuntimeException("Board no encontrado"));
    }

    public void deleteById(Integer id) {
        boardRepository.deleteById(id);
    }
}
