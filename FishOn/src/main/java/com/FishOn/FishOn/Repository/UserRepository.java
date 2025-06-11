package com.FishOn.FishOn.Repository;

import com.FishOn.FishOn.Model.UserModel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID> {

    Optional<UserModel> findByUserName(String userName);
    Optional<UserModel> findByEmail(String email);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
    List<UserModel> findByEnabledTrue();
}