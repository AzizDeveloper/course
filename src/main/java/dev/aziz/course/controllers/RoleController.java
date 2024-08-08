package dev.aziz.course.controllers;

import dev.aziz.course.entities.Role;
import dev.aziz.course.services.RoleService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<?> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody Role role) {
        Role savedRole = roleService.createRole(role);
        return ResponseEntity.ok(savedRole);
    }

    @PutMapping("/{oldName}/{newName}")
    public ResponseEntity<?> updateRoleByName(@PathVariable String oldName, @PathVariable String newName) {
        return ResponseEntity.ok(roleService.updateRole(oldName, newName));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteRoleById(@PathVariable String name) {
        roleService.deleteRoleByName(name);
        return ResponseEntity.ok("Role deleted.");
    }

}
