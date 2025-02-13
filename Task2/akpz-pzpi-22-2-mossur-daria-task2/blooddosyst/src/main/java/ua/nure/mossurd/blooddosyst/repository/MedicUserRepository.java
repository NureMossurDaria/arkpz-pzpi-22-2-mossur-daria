package ua.nure.mossurd.blooddosyst.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.nure.mossurd.blooddosyst.entity.MedicUser;

public interface MedicUserRepository extends JpaRepository<MedicUser, Integer> {
    MedicUser getByUser_username(String username);
}
