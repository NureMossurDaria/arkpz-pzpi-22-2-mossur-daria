package ua.nure.mossurd.blooddosyst.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.nure.mossurd.blooddosyst.dto.NotificationDto;
import ua.nure.mossurd.blooddosyst.dto.UserDto;
import ua.nure.mossurd.blooddosyst.entity.*;
import ua.nure.mossurd.blooddosyst.enums.Language;
import ua.nure.mossurd.blooddosyst.enums.UserType;
import ua.nure.mossurd.blooddosyst.repository.*;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DonorDataRepository donorDataRepository;
    private final DonationAppointmentRepository donationAppointmentRepository;
    private final DonationRepository donationRepository;
    private final NotificationRepository notificationRepository;
    private final BloodRepository bloodRepository;
    private final FridgeRepository fridgeRepository;

    public UserDto getUser(String username) {
        Optional<User> userOptional = userRepository.findById(username);
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "User not found");
        }
        User user = userOptional.get();
        return new UserDto(user.getUsername(), null);
    }

    public void updatePassword(String password) {
        User user = userRepository.getReferenceById((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        user.setPassword(passwordEncoder.encode(password));
        userRepository.saveAndFlush(user);
    }

    public void updateLanguage(Language language) {
        User user = userRepository.getReferenceById((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        user.setLanguage(language);
        userRepository.saveAndFlush(user);
    }

    public void deleteSelf() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        if (authorities.stream().anyMatch(
                s -> s.contains(UserType.MEDIC.name()) || s.contains(UserType.ADMIN.name()))) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Currently only donors can be deleted from the system");
        }
        User user = userRepository.getReferenceById(username);
        DonorData donorData = donorDataRepository.getByUser_username(username);
        donationAppointmentRepository.deleteAllByDonorData(donorData);
        donationAppointmentRepository.flush();
        List<Donation> donations = donationRepository.getAllByDonorData(donorData);
        donations.forEach(donation -> donation.setDonorData(null));
        donationRepository.saveAllAndFlush(donations);

        List<Notification> notificationsByCreatedBy = notificationRepository.getAllByCreatedBy(user.getUsername());
        notificationsByCreatedBy.forEach(notification -> notification.setCreatedBy(null));
        notificationRepository.saveAllAndFlush(notificationsByCreatedBy);
        notificationRepository.deleteAllByTargetUser(user);
        notificationRepository.flush();
        List<Blood> bloodByCreatedBy = bloodRepository.findAllByCreatedBy(user.getUsername());
        bloodByCreatedBy.forEach(blood -> blood.setCreatedBy(null));
        bloodRepository.saveAllAndFlush(bloodByCreatedBy);
        List<Fridge> fridgeList = fridgeRepository.findAllByCreatedBy(user.getUsername());
        fridgeList.forEach(fridge -> fridge.setCreatedBy(null));
        fridgeRepository.saveAllAndFlush(fridgeList);

        donorDataRepository.delete(donorData);
        donorDataRepository.flush();
        user.setEnabled(false);
        userRepository.saveAndFlush(user);
    }

    public List<NotificationDto> getAllNotifications() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getReferenceById(username);
        return notificationRepository.getAllByTargetUser(user).stream()
                .map(NotificationDto::new)
                .toList();
    }

    public List<NotificationDto> getNewNotifications() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getReferenceById(username);
        return notificationRepository.getAllByTargetUser(user).stream()
                .filter(notification -> !notification.getDelivered())
                .map(NotificationDto::new)
                .toList();
    }
}
