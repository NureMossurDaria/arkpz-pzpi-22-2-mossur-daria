package ua.nure.mossurd.blooddosyst.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ua.nure.mossurd.blooddosyst.dto.*;
import ua.nure.mossurd.blooddosyst.service.AdminService;
import ua.nure.mossurd.blooddosyst.service.UserService;

@RestController
@RequestMapping(value = "/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // Hospital operations

    @PostMapping("/hospital/new")
    public HospitalDto createHospital(@RequestBody HospitalDto hospitalDto) {
        return adminService.createHospital(hospitalDto);
    }

    @PutMapping("/hospital/{id}/update")
    public HospitalDto updateHospital(@PathVariable("id") Integer id, @RequestBody HospitalDto hospitalDto) {
        return adminService.updateHospital(id, hospitalDto);
    }

    @DeleteMapping("/hospital/{id}/delete")
    public void deleteHospital(@PathVariable("id") Integer id) {
        throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Coming soon...");
    }

    // User operations

    @PostMapping("/medic/new")
    public MedicDto createMedic(@RequestBody MedicDto medicDto) {
        return adminService.createMedic(medicDto);
    }

    @PutMapping("/medic/{id}/update")
    public MedicDto updateMedic(@PathVariable("id") Integer id, @RequestBody MedicDto medicDto) {
        return adminService.updateMedic(id, medicDto);
    }
}
