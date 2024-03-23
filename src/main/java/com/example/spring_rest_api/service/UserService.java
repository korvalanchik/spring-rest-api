package com.example.spring_rest_api.service;

import com.example.spring_rest_api.domain.Note;
import com.example.spring_rest_api.domain.Role;
import com.example.spring_rest_api.domain.User;
import com.example.spring_rest_api.model.UserDTO;
import com.example.spring_rest_api.repos.NoteRepository;
import com.example.spring_rest_api.repos.RoleRepository;
import com.example.spring_rest_api.repos.UserRepository;
import com.example.spring_rest_api.util.NotFoundException;
import com.example.spring_rest_api.util.ReferencedWarning;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final NoteRepository noteRepository;

    public UserService(final UserRepository userRepository, final RoleRepository roleRepository,
            final NoteRepository noteRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.noteRepository = noteRepository;
    }

    public List<UserDTO> findAll() {
        final List<User> users = userRepository.findAll(Sort.by("id"));
        return users.stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .toList();
    }

    public UserDTO get(final Long id) {
        return userRepository.findById(id)
                .map(user -> mapToDTO(user, new UserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);
        return userRepository.save(user).getId();
    }

    public void update(final Long id, final UserDTO userDTO) {
        final User user = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(userDTO, user);
        userRepository.save(user);
    }

    public void delete(final Long id) {
        userRepository.deleteById(id);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setRoles(user.getRoles().stream()
                .map(role -> role.getId())
                .toList());
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        final List<Role> roles = roleRepository.findAllById(
                userDTO.getRoles() == null ? Collections.emptyList() : userDTO.getRoles());
        if (roles.size() != (userDTO.getRoles() == null ? 0 : userDTO.getRoles().size())) {
            throw new NotFoundException("one of roles not found");
        }
        user.setRoles(new HashSet<>(roles));
        return user;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final User user = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Note userNote = noteRepository.findFirstByUser(user);
        if (userNote != null) {
            referencedWarning.setKey("user.note.user.referenced");
            referencedWarning.addParam(userNote.getUserid());
            return referencedWarning;
        }
        return null;
    }

}
