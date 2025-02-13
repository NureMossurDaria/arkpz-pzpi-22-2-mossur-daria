package ua.nure.mossurd.blooddosyst.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.nure.mossurd.blooddosyst.entity.Notification;
import ua.nure.mossurd.blooddosyst.entity.User;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> getAllByTargetUser(User targetUser);
    List<Notification> getAllByCreatedBy(String createdBy);
    void deleteAllByTargetUser(User targetUser);
}
