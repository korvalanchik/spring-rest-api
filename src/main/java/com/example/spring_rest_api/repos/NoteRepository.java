package com.example.spring_rest_api.repos;

import com.example.spring_rest_api.domain.Note;
import com.example.spring_rest_api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NoteRepository extends JpaRepository<Note, Long> {

    Note findFirstByUser(User user);

}
