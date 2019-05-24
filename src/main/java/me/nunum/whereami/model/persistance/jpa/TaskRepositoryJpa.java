package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Task;
import me.nunum.whereami.model.persistance.TaskRepository;
import me.nunum.whereami.utils.AppConfig;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Iterator;
import java.util.stream.Stream;

public class TaskRepositoryJpa
        extends JpaRepository<Task, Long>
        implements TaskRepository {

    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }


    @Override
    public Stream<Task> openTasks() {

        final CriteriaBuilder criteriaBuilder = super.entityManager().getCriteriaBuilder();

        CriteriaQuery<Task> builderQuery = criteriaBuilder.createQuery(Task.class);

        final Root<Task> taskRoot = builderQuery.from(Task.class);

        CriteriaQuery<Task> where = builderQuery.where(criteriaBuilder.equal(taskRoot.get("state"), Task.STATE.RUNNING));

        return entityManager().createQuery(where).getResultStream();
    }
}
