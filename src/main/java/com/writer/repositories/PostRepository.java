package com.writer.repositories;

import com.writer.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByUserId(Integer userId, Pageable paging);

    @Query(value = "Select * from posts order by created_date desc", nativeQuery = true)
    List<Post> findAllSorted();

    Page<Post> findAllByIsPrivate(boolean isPrivate, Pageable paging);

}
