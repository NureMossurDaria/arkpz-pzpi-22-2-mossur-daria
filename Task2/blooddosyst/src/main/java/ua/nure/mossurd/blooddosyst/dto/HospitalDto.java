package ua.nure.mossurd.blooddosyst.dto;

import ua.nure.mossurd.blooddosyst.entity.BloodNeeds;
import ua.nure.mossurd.blooddosyst.entity.Hospital;

public record HospitalDto(
        Integer id,
        String address,
        BloodNeedsDto bloodNeeds) {
    public HospitalDto(Hospital hospital, BloodNeeds bloodNeeds) {
        this(hospital.getId(), hospital.getHospitalAddress(), new BloodNeedsDto(bloodNeeds));
    }
}
