package com.example.spring_rest_api.repos;

import com.example.spring_rest_api.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<Role, Long> {
}
