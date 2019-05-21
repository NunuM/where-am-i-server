package me.nunum.whereami.controller;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.Post;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.persistance.PostRepository;
import me.nunum.whereami.model.persistance.jpa.PostRepositoryJpa;
import me.nunum.whereami.model.request.PostRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PostController implements AutoCloseable {

    private final PostRepository repository;

    public PostController() {
        this.repository = new PostRepositoryJpa();
    }


    public List<DTO> posts(Optional<Integer> page) {

        return this.repository
                .paginate(page)
                .stream()
                .map(Post::toDTO)
                .collect(Collectors.toList());
    }


    public DTO addNewPost(PostRequest postRequest) {
        return this.repository.save(postRequest.build()).toDTO();
    }

    public DTO updatePost(Long id, PostRequest postRequest) {

        final Optional<Post> somePost = this.repository.findById(id);

        if (!somePost.isPresent()) {
            throw new EntityNotFoundException(String
                    .format("Post with id %d not found", id));
        }

        final Post post = somePost.get();

        return this.repository.save(postRequest.edit(post)).toDTO();
    }

    public DTO deletePost(Long id) {

        final Optional<Post> somePost = this.repository.findById(id);

        if (!somePost.isPresent()) {
            throw new EntityNotFoundException(String
                    .format("Post with id %d not found", id));
        }

        final Post post = somePost.get();

        this.repository.delete(post);

        return post.toDTO();
    }

    @Override
    public void close() throws Exception {
        this.repository.close();
    }
}
