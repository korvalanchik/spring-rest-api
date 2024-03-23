package com.example.spring_rest_api.rest;

import com.example.spring_rest_api.model.NoteDTO;
import com.example.spring_rest_api.service.NoteService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/notes", produces = MediaType.APPLICATION_JSON_VALUE)
public class NoteResource {

    private final NoteService noteService;

    public NoteResource(final NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<List<NoteDTO>> getAllNotes() {
        return ResponseEntity.ok(noteService.findAll());
    }

    @GetMapping("/{userid}")
    public ResponseEntity<NoteDTO> getNote(@PathVariable(name = "userid") final Long userid) {
        return ResponseEntity.ok(noteService.get(userid));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createNote(@RequestBody @Valid final NoteDTO noteDTO) {
        final Long createdUserid = noteService.create(noteDTO);
        return new ResponseEntity<>(createdUserid, HttpStatus.CREATED);
    }

    @PutMapping("/{userid}")
    public ResponseEntity<Long> updateNote(@PathVariable(name = "userid") final Long userid,
            @RequestBody @Valid final NoteDTO noteDTO) {
        noteService.update(userid, noteDTO);
        return ResponseEntity.ok(userid);
    }

    @DeleteMapping("/{userid}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteNote(@PathVariable(name = "userid") final Long userid) {
        noteService.delete(userid);
        return ResponseEntity.noContent().build();
    }

}
