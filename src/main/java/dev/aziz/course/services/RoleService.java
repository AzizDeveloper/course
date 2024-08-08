package dev.aziz.course.services;

import dev.aziz.course.entities.Role;
import dev.aziz.course.exceptions.AppException;
import dev.aziz.course.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleByName(String name) {
        return roleRepository.findRoleByName(name)
                .orElseThrow(() -> new AppException("Role not found.", HttpStatus.NOT_FOUND));
    }

    public Role getRoleById(Long id) {
        return roleRepository.findRoleById(id)
                .orElseThrow(() -> new AppException("Role not found.", HttpStatus.NOT_FOUND));
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public Role updateRole(String oldName, String newName) {
        Role oldRole = getRoleByName(oldName);
        oldRole.setName(newName);
        Role newRole = roleRepository.save(oldRole);
        return newRole;
    }

    public void deleteRoleByName(String name) {
        Role role = getRoleByName(name);
        roleRepository.delete(role);
    }
}
