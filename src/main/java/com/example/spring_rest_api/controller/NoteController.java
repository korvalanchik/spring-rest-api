package com.example.spring_rest_api.controller;

import com.example.spring_rest_api.domain.User;
import com.example.spring_rest_api.model.NoteDTO;
import com.example.spring_rest_api.repos.UserRepository;
import com.example.spring_rest_api.service.NoteService;
import com.example.spring_rest_api.util.CustomCollectors;
import com.example.spring_rest_api.util.WebUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;
    private final UserRepository userRepository;

    public NoteController(final NoteService noteService, final UserRepository userRepository) {
        this.noteService = noteService;
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("userValues", userRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getId, User::getUsername)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("notes", noteService.findAll());
        return "note/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("note") final NoteDTO noteDTO) {
        return "note/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("note") @Valid final NoteDTO noteDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "note/add";
        }
        noteService.create(noteDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("note.create.success"));
        return "redirect:/notes";
    }

    @GetMapping("/edit/{userid}")
    public String edit(@PathVariable(name = "userid") final Long userid, final Model model) {
        model.addAttribute("note", noteService.get(userid));
        return "note/edit";
    }

    @PostMapping("/edit/{userid}")
    public String edit(@PathVariable(name = "userid") final Long userid,
            @ModelAttribute("note") @Valid final NoteDTO noteDTO, final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "note/edit";
        }
        noteService.update(userid, noteDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("note.update.success"));
        return "redirect:/notes";
    }

    @PostMapping("/delete/{userid}")
    public String delete(@PathVariable(name = "userid") final Long userid,
            final RedirectAttributes redirectAttributes) {
        noteService.delete(userid);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("note.delete.success"));
        return "redirect:/notes";
    }

}
