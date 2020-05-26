package com.valentine.demo.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfilePictureRepository extends JpaRepository<ProfilePicture, Long> {

    @Query(nativeQuery = true, value = "SELECT user_id, location " +
            "FROM profile_picture " +
            "WHERE user_id = ?1")
    ProfilePicture getProfilePictureById(long id);

}
