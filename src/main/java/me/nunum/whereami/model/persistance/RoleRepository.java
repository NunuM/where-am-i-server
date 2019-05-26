package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.Role;

public interface RoleRepository extends Repository<Role, Long>, AutoCloseable {
}
