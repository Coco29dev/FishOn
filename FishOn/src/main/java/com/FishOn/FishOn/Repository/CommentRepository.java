package com.FishOn.FishOn.Repository;

import com.FishOn.FishOn.Model.CommentModel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<CommentModel, UUID> {

    List<CommentModel> findByUserId(UUID id);
    List<CommentModel> findByPostId(UUID id);

}