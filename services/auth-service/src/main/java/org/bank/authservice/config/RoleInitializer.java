package org.bank.authservice.config;

import lombok.RequiredArgsConstructor;
import org.bank.authservice.entity.RoleEntity;
import org.bank.authservice.enums.Role;
import org.bank.authservice.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        if (roleRepository.count() == 0) {

            roleRepository.save(
                    new RoleEntity(Role.USER)
            );

            roleRepository.save(
                    new RoleEntity(Role.ADMIN)
            );
        }
    }
}