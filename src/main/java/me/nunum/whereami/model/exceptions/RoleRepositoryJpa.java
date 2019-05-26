package me.nunum.whereami.model.exceptions;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Role;
import me.nunum.whereami.model.persistance.RoleRepository;
import me.nunum.whereami.utils.AppConfig;

public class RoleRepositoryJpa
        extends JpaRepository<Role, Long>
        implements RoleRepository {
    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }
}
