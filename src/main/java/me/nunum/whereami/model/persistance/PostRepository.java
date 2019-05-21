package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository
        extends Repository<Post, Long>, AutoCloseable {

    List<Post> paginate(Optional<Integer> page);

}
