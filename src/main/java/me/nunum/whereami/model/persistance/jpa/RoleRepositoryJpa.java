package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Role;
import me.nunum.whereami.model.persistance.RoleRepository;
import me.nunum.whereami.utils.AppConfig;

import javax.persistence.EntityManager;

public class RoleRepositoryJpa
        extends JpaRepository<Role, Long>
        implements RoleRepository {
    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }

    @Override
    public Role findRole(String role) {
        final EntityManager entityManager = entityManager();
        return (Role) entityManager.createNamedQuery("Role.findByRoleName")
                .setParameter("role", role)
                .getSingleResult();
    }
}
