package com.foreach.demo.dfm.application.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.annotations.InstallerMethod;
import com.foreach.across.core.installers.InstallerPhase;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.services.RoleService;
import com.foreach.across.modules.user.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
@Installer(name = "user-installer", description = "Creates the users and roles", phase = InstallerPhase.AfterContextBootstrap, version = 1)
@Slf4j
@Order(20)
public class DefaultUserInstaller {

    private final UserService userService;
    private final RoleService roleService;

    @Transactional
    @InstallerMethod
    public void createUsers() {
        createUser(-1L, "john", "John", "Doe", "john.doe@localhost", Collections.emptyList(), DefaultRoleInstaller.ROLE_TRAINER);
        createUser(-2L, "jane", "Jane", "Doe", "jane.doe@localhost", Collections.emptyList(), DefaultRoleInstaller.ROLE_TRAINER);
    }

    private void createUser(Long userId, String username, String firstName, String lastName, String email, Collection<Group> groups,
                            String... roles) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            user = new User();
            user.setNewEntityId(userId);
        } else {
            user = user.toDto();
        }

        user.setUsername(username);
        user.setPassword(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setDisplayName(firstName + ' ' + lastName);
        user.setEmail(email);
        user.setDeleted(false);
        user.setEmailConfirmed(true);
        user.setGroups(groups);

        for (String roleAuthority : roles) {
            user.addRole(roleService.getRole(roleAuthority));
        }

        userService.save(user);
    }
}
