package edu.cnm.deepdive.seesomethingabq.service.repository;

import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

//  List<UserProfile> getUserProfilesByUserProfileId(Long id);

}
