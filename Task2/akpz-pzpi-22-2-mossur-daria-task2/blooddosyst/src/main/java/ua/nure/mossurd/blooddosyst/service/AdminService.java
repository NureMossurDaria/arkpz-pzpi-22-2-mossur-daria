package ua.nure.mossurd.blooddosyst.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.nure.mossurd.blooddosyst.dto.HospitalDto;
import ua.nure.mossurd.blooddosyst.dto.MedicDto;
import ua.nure.mossurd.blooddosyst.entity.BloodNeeds;
import ua.nure.mossurd.blooddosyst.entity.Hospital;
import ua.nure.mossurd.blooddosyst.entity.MedicUser;
import ua.nure.mossurd.blooddosyst.enums.UserType;
import ua.nure.mossurd.blooddosyst.repository.BloodNeedsRepository;
import ua.nure.mossurd.blooddosyst.repository.HospitalRepository;
import ua.nure.mossurd.blooddosyst.repository.MedicUserRepository;
import ua.nure.mossurd.blooddosyst.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final HospitalRepository hospitalRepository;
    private final BloodNeedsRepository bloodNeedsRepository;
    private final UserRepository userRepository;
    private final MedicUserRepository medicUserRepository;
    private final AuthService authService;

    public HospitalDto createHospital(HospitalDto hospitalDto) {
        Hospital hospital = new Hospital();
        hospital.setHospitalAddress(hospitalDto.address());
        Hospital newHospital = hospitalRepository.saveAndFlush(hospital);
        BloodNeeds bloodNeeds = new BloodNeeds();
        bloodNeeds.setHospital(newHospital);
        BloodNeeds newBloodNeeds = bloodNeedsRepository.saveAndFlush(bloodNeeds);
        return new HospitalDto(newHospital, newBloodNeeds);
    }

    public HospitalDto updateHospital(Integer id, HospitalDto hospitalDto) {
        Hospital hospital = hospitalRepository.getReferenceById(id);
        hospital.setHospitalAddress(hospitalDto.address());
        Hospital newHospital = hospitalRepository.saveAndFlush(hospital);
        BloodNeeds bloodNeeds = bloodNeedsRepository.getByHospitalId(newHospital.getId());
        return new HospitalDto(newHospital, bloodNeeds);
    }

    public MedicDto createMedic(MedicDto medicDto) {
        authService.createUser(medicDto.username(), UserType.MEDIC);
        MedicUser medicUser = new MedicUser();
        medicUser.setUser(userRepository.getReferenceById(medicDto.username()));
        medicUser.setHospital(hospitalRepository.getReferenceById(medicDto.hospitalId()));
        medicUser.setFirstName(medicDto.firstName());
        medicUser.setLastName(medicDto.lastName());
        medicUser.setPhoneNumber(medicDto.phone());
        MedicUser newMedicUser = medicUserRepository.saveAndFlush(medicUser);
        return new MedicDto(newMedicUser);
    }

    public MedicDto updateMedic(Integer id, MedicDto medicDto) {
        MedicUser medicUser = medicUserRepository.getReferenceById(id);
        medicUser.setFirstName(medicDto.firstName());
        medicUser.setLastName(medicDto.lastName());
        medicUser.setPhoneNumber(medicDto.phone());
        medicUser.setHospital(hospitalRepository.getReferenceById(medicDto.hospitalId()));
        MedicUser newMedicUser = medicUserRepository.saveAndFlush(medicUser);
        return new MedicDto(newMedicUser);
    }
}
