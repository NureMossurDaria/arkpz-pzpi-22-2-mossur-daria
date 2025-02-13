package ua.nure.mossurd.blooddosyst.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.nure.mossurd.blooddosyst.dto.NotificationDto;
import ua.nure.mossurd.blooddosyst.enums.Language;
import ua.nure.mossurd.blooddosyst.service.UserService;

import java.util.List;

@RestController
@RequestMapping(value = "/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/password")
    public void updatePassword(@RequestBody String newPassword) {
        userService.updatePassword(newPassword);
    }

    @PostMapping("/language/{lang}")
    public void updateLanguage(@PathVariable("lang") Language language) {
        userService.updateLanguage(language);
    }

    @DeleteMapping
    public void deleteSelf() {
        userService.deleteSelf();
    }

    @GetMapping("/notifications/all")
    public List<NotificationDto> getAllNotifications() {
        return userService.getAllNotifications();
    }

    @GetMapping("/notifications/new")
    public List<NotificationDto> getNewNotifications() {
        return userService.getNewNotifications();
    }
}
