package com.foreach.demo.dfm.application.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.annotations.InstallerMethod;
import com.foreach.across.core.installers.InstallerPhase;
import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.services.PermissionService;
import com.foreach.across.modules.user.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Steven Gentens
 * @since 0.0.1
 */
@RequiredArgsConstructor
@Installer(description = "Installs the default roles", phase = InstallerPhase.AfterContextBootstrap)
@Order(10)
public class DefaultRoleInstaller {
    static final String ROLE_TRAINER = "ROLE_TRAINER";

    private final RoleService roleService;
    private final PermissionService permissionService;

    @InstallerMethod
    public void createRoles() {
        createRole(ROLE_TRAINER, "Trainer", "A person who gives trainings");
    }

    private void createRole(String authority, String name, String description, String... permissionNames) {
        Role role = roleService.getRole(authority);
        if (role == null) {
            role = new Role();
            role.setAuthority(authority);
            role.setName(name);
            role.setDescription(description);

            List<Permission> permissions = Stream.of(permissionNames).map(permissionService::getPermission)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (!permissions.isEmpty()) {
                role.setPermissions(permissions);
            }

            roleService.save(role);
        }
    }
}
