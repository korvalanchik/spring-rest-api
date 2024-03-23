package com.example.spring_rest_api.service;

import com.example.spring_rest_api.domain.Note;
import com.example.spring_rest_api.domain.User;
import com.example.spring_rest_api.model.NoteDTO;
import com.example.spring_rest_api.repos.NoteRepository;
import com.example.spring_rest_api.repos.UserRepository;
import com.example.spring_rest_api.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteService(final NoteRepository noteRepository, final UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    public List<NoteDTO> findAll() {
        final List<Note> notes = noteRepository.findAll(Sort.by("userid"));
        return notes.stream()
                .map(note -> mapToDTO(note, new NoteDTO()))
                .toList();
    }

    public NoteDTO get(final Long userid) {
        return noteRepository.findById(userid)
                .map(note -> mapToDTO(note, new NoteDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final NoteDTO noteDTO) {
        final Note note = new Note();
        mapToEntity(noteDTO, note);
        return noteRepository.save(note).getUserid();
    }

    public void update(final Long userid, final NoteDTO noteDTO) {
        final Note note = noteRepository.findById(userid)
                .orElseThrow(NotFoundException::new);
        mapToEntity(noteDTO, note);
        noteRepository.save(note);
    }

    public void delete(final Long userid) {
        noteRepository.deleteById(userid);
    }

    private NoteDTO mapToDTO(final Note note, final NoteDTO noteDTO) {
        noteDTO.setUserid(note.getUserid());
        noteDTO.setTitle(note.getTitle());
        noteDTO.setContent(note.getContent());
        noteDTO.setUser(note.getUser() == null ? null : note.getUser().getId());
        return noteDTO;
    }

    private Note mapToEntity(final NoteDTO noteDTO, final Note note) {
        note.setTitle(noteDTO.getTitle());
        note.setContent(noteDTO.getContent());
        final User user = noteDTO.getUser() == null ? null : userRepository.findById(noteDTO.getUser())
                .orElseThrow(() -> new NotFoundException("user not found"));
        note.setUser(user);
        return note;
    }

}
