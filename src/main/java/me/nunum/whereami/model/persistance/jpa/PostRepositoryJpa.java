package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Post;
import me.nunum.whereami.model.persistance.PostRepository;
import me.nunum.whereami.utils.AppConfig;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostRepositoryJpa
        extends JpaRepository<Post, Long>
        implements PostRepository {


    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<Post> paginate(Optional<Integer> page) {

        List<Post> posts = new ArrayList<>();

        final Integer currentPage = page.map(p -> {
            if (p < 1) {
                return 1;
            } else return p;
        }).orElse(1);

        final EntityManager manager = entityManager();

        return manager.createNamedQuery("Post.all")
                .setMaxResults(DEFAULT_PAGE_SIZE)
                .setFirstResult((currentPage - 1) * DEFAULT_PAGE_SIZE)
                .getResultList();
    }

}
