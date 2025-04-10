package com.example.VideoSpringM03.controller;

import com.example.VideoSpringM03.exception.ResourceNotFoundException;
import com.example.VideoSpringM03.model.User;
import com.example.VideoSpringM03.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // ================== API REST ==================
    @PostMapping
    @ResponseBody
    public ResponseEntity<User> createUserApi(@RequestBody User user) {
        User savedUser = userRepository.save(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteUserApi(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<User> updateUserApi(@PathVariable Long id, @RequestBody User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());

        return ResponseEntity.ok(userRepository.save(user));
    }

    // Nuevo método para JSON Patch
    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    @ResponseBody
    public ResponseEntity<User> patchUser(@PathVariable Long id, @RequestBody JsonPatch patch) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        try {
            User patchedUser = applyPatchToUser(patch, user);
            userRepository.save(patchedUser);
            return ResponseEntity.ok(patchedUser);
        } catch (JsonPatchException | IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private User applyPatchToUser(JsonPatch patch, User targetUser) throws JsonPatchException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode userNode = objectMapper.valueToTree(targetUser);
        JsonNode patchedNode = patch.apply(userNode);
        return objectMapper.treeToValue(patchedNode, User.class);
    }

    // ================== THYMELEAF ==================
    @GetMapping("/view")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "list-users";
    }

    @GetMapping("/view/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "user-form";
    }

    @GetMapping("/view/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        model.addAttribute("user", user);
        return "user-form";
    }

    @PostMapping("/view/save")
    public String saveUser(@ModelAttribute User user) {
        userRepository.save(user);
        return "redirect:/api/users/view";
    }

    @GetMapping("/view/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/api/users/view";
    }
}