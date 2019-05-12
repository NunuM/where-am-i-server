package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Post;
import me.nunum.whereami.model.persistance.PostRepository;
import me.nunum.whereami.utils.AppConfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class PostRepositoryJpa
        extends JpaRepository<Post, Long>
        implements PostRepository {


    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }


    @Override
    public List<Post> paginate(Optional<Integer> page) {

        List<Post> posts = new ArrayList<>();

        final Integer currentPage = page.map(p -> {
            if (p < 1) {
                return 1;
            } else return p;
        }).orElse(1);

        final Iterator<Post> thePostIterator = this.iterator(currentPage);

        thePostIterator.forEachRemaining(posts::add);

        return posts;
    }

}
