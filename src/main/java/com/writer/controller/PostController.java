package com.writer.controller;

import com.writer.domain.Post;
import com.writer.domain.User;
import com.writer.repositories.PostRepository;
import com.writer.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
class PostController {

    private final Logger log = LoggerFactory.getLogger(PostController.class);
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostController(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/my-posts/{page}/{size}")
    public ResponseEntity<Map<String, Object>> posts(@PathVariable int page,
                                                     @PathVariable int size,
                                                     Principal principal) {
        log.info("Request to get my posts");
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
            Pageable paging = PageRequest.of(page - 1, size, sort);
            Page<Post> pagePost = postRepository.findAllByUserId(Integer.parseInt(principal.getName()), paging);

            List<Post> posts = pagePost.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("posts", posts);
            response.put("currentPage", pagePost.getNumber());
            response.put("totalItems", pagePost.getTotalElements());
            response.put("totalPages", pagePost.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    
    @GetMapping("posts/{page}/{size}")
    public ResponseEntity<Map<String, Object>> allPosts(@PathVariable int page, @PathVariable int size){
        log.debug("requests to get all posts");
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
            Pageable paging = PageRequest.of(page - 1, size, sort);
            Page<Post> pagePost = postRepository.findAllByIsPrivate(false, paging);

            List<Post> posts = pagePost.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("posts", posts);
            response.put("currentPage", pagePost.getNumber());
            response.put("totalItems", pagePost.getTotalElements());
            response.put("totalPages", pagePost.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/post/{id}")
    ResponseEntity<?> getPost(@PathVariable Long id) {
        Optional<Post> post = postRepository.findById(id);
        return post.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/post")
    ResponseEntity<Post> createPost(@Valid @RequestBody Post post, @AuthenticationPrincipal OAuth2User principal) throws URISyntaxException {
        log.info("Request to create post: {}", post);
        Map<String, Object> details = principal.getAttributes();
        Integer userId = Integer.parseInt(principal.getName());

        Optional<User> user = userRepository.findById(userId);
        post.setUser(user.orElseGet(() -> userRepository.save(new User(userId,
                Optional.ofNullable(details.get("name")).map(Object::toString).orElse(""),
                Optional.ofNullable(details.get("login")).map(Objects::toString).orElse("")))));
        post.setCreatedDate(LocalDateTime.now());

        Post result = postRepository.save(post);
        return ResponseEntity.created(new URI("/api/post/" + result.getId()))
                .body(result);
    }

    @PutMapping("/post/{id}")
    ResponseEntity<Post> updatePost(@Valid @RequestBody Post post) {
        log.info("Request to update post: {}", post);
        Post result = postRepository.save(post);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        log.info("Request to delete post: {}", id);
        postRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}