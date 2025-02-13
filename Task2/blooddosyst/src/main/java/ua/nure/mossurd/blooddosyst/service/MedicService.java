package ua.nure.mossurd.blooddosyst.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ua.nure.mossurd.blooddosyst.dto.*;
import ua.nure.mossurd.blooddosyst.entity.*;
import ua.nure.mossurd.blooddosyst.enums.BloodStatus;
import ua.nure.mossurd.blooddosyst.enums.UserType;
import ua.nure.mossurd.blooddosyst.repository.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicService {

    private final BloodNeedsRepository bloodNeedsRepository;
    private final MedicUserRepository medicUserRepository;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final DonorDataRepository donorDataRepository;
    private final DonationAppointmentRepository donationAppointmentRepository;
    private final DonationRepository donationRepository;
    private final NotificationRepository notificationRepository;
    private final BloodRepository bloodRepository;
    private final FridgeRepository fridgeRepository;
    private final DonationEventRepository donationEventRepository;
    private final FridgeMetricsRepository fridgeMetricsRepository;

    public BloodNeedsDto getBloodNeeds() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MedicUser medic = medicUserRepository.getByUser_username(username);
        return new BloodNeedsDto(bloodNeedsRepository.getByHospitalId(medic.getHospital().getId()));
    }

    public BloodNeedsDto updateBloodNeeds(BloodNeedsDto bloodNeedsDto) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MedicUser medic = medicUserRepository.getByUser_username(username);
        BloodNeeds bloodNeeds = bloodNeedsRepository.getByHospitalId(medic.getHospital().getId());
        bloodNeeds.setONegative(bloodNeedsDto.oNegative());
        bloodNeeds.setOPositive(bloodNeedsDto.oPositive());
        bloodNeeds.setANegative(bloodNeedsDto.aNegative());
        bloodNeeds.setAPositive(bloodNeedsDto.aPositive());
        bloodNeeds.setBNegative(bloodNeedsDto.bNegative());
        bloodNeeds.setBPositive(bloodNeedsDto.bPositive());
        bloodNeeds.setAbNegative(bloodNeedsDto.abNegative());
        bloodNeeds.setAbPositive(bloodNeedsDto.abPositive());
        return new BloodNeedsDto(bloodNeedsRepository.saveAndFlush(bloodNeeds));
    }

    public DonorDto createDonor(DonorDto donorDto) {
        authService.createUser(donorDto.username(), UserType.DONOR);
        DonorData donorData = new DonorData();
        donorData.setUser(userRepository.getReferenceById(donorDto.username()));
        donorData.setFirstName(donorDto.firstName());
        donorData.setLastName(donorDto.lastName());
        donorData.setPhoneNumber(donorDto.phone());
        donorData.setBloodType(donorDto.bloodType());
        donorData.setRhesusFactor(donorDto.rhesus());
        return new DonorDto(donorDataRepository.saveAndFlush(donorData));
    }

    public DonorDto updateDonor(Integer id, DonorDto donorDto) {
        DonorData donorData = donorDataRepository.getReferenceById(id);
        donorData.setFirstName(donorDto.firstName());
        donorData.setLastName(donorDto.lastName());
        donorData.setPhoneNumber(donorDto.phone());
        donorData.setBloodType(donorDto.bloodType());
        donorData.setRhesusFactor(donorDto.rhesus());
        return new DonorDto(donorDataRepository.saveAndFlush(donorData));
    }

    public void deleteDonor(Integer id) {
        DonorData donorData = donorDataRepository.getReferenceById(id);
        donationAppointmentRepository.deleteAllByDonorData(donorData);
        donationAppointmentRepository.flush();
        List<Donation> donations = donationRepository.getAllByDonorData(donorData);
        donations.forEach(donation -> donation.setDonorData(null));
        donationRepository.saveAllAndFlush(donations);

        User user = donorData.getUser();
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

    public List<DonationEventDto> getAllEvents() {
        return donationEventRepository.findAll().stream()
                .map(DonationEventDto::new)
                .toList();
    }

    public DonationEventDto getEvent(Integer id) {
        return new DonationEventDto(donationEventRepository.getReferenceById(id));
    }

    public DonationEventDto createEvent(DonationEventDto donationEventDto) {
        DonationEvent event = new DonationEvent();
        event.setEventAddress(donationEventDto.eventAddress());
        event.setEventStatus(donationEventDto.status());
        event.setNotes(donationEventDto.notes());
        event.setDateTime(donationEventDto.dateTime());
        event.setCreatedBy((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return new DonationEventDto(donationEventRepository.saveAndFlush(event));
    }

    public DonationEventDto updateEvent(Integer id, DonationEventDto donationEventDto) {
        DonationEvent event = donationEventRepository.getReferenceById(id);
        event.setEventAddress(donationEventDto.eventAddress());
        event.setEventStatus(donationEventDto.status());
        event.setNotes(donationEventDto.notes());
        event.setDateTime(donationEventDto.dateTime());
        return new DonationEventDto(donationEventRepository.saveAndFlush(event));
    }

    public Long getAppointmentQuantityForEvent(Integer eventId) {
        return donationAppointmentRepository.countByDonationEvent_id(eventId);
    }

    public List<FridgeDto> getAllFridges() {
        return fridgeRepository.findAll().stream().map(FridgeDto::new).toList();
    }

    public FridgeDto getFridge(Integer id) {
        return new FridgeDto(fridgeRepository.getReferenceById(id));
    }

    public FridgeDto createFridge(FridgeDto fridgeDto) {
        Fridge fridge = new Fridge();
        fridge.setSerialNumber(fridgeDto.serialNumber());
        fridge.setFridgeAddress(fridgeDto.address());
        fridge.setNotes(fridgeDto.notes());
        fridge.setTempCelsiusMin(fridgeDto.tempCelsiusMin());
        fridge.setTempCelsiusMax(fridgeDto.tempCelsiusMax());
        fridge.setHumidityPercentMin(fridgeDto.humidityPercentMin());
        fridge.setHumidityPercentMax(fridgeDto.humidityPercentMax());
        fridge.setCreatedBy((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return new FridgeDto(fridgeRepository.saveAndFlush(fridge));
    }

    public FridgeDto updateFridge(Integer id, FridgeDto fridgeDto) {
        Fridge fridge = fridgeRepository.getReferenceById(id);
        fridge.setSerialNumber(fridgeDto.serialNumber());
        fridge.setFridgeAddress(fridgeDto.address());
        fridge.setNotes(fridgeDto.notes());
        fridge.setTempCelsiusMin(fridgeDto.tempCelsiusMin());
        fridge.setTempCelsiusMax(fridgeDto.tempCelsiusMax());
        fridge.setHumidityPercentMin(fridgeDto.humidityPercentMin());
        fridge.setHumidityPercentMax(fridgeDto.humidityPercentMax());
        return new FridgeDto(fridgeRepository.saveAndFlush(fridge));
    }

    public List<FridgeMetricDto> getFridgeMetrics(Integer id, LocalDateTime startDate, LocalDateTime endDate) {
        return fridgeMetricsRepository.findAllByFridge_idAndDateTimeBetween(id, startDate, endDate).stream()
                .map(FridgeMetricDto::new)
                .toList();
    }

    public DonationDto getDonation(Integer id) {
        return new DonationDto(donationRepository.getReferenceById(id), bloodRepository.findByDonation_id(id));
    }

    public DonationDto createDonation(DonationDto donationDto) {
        Donation donation = new Donation();
        donation.setDonationEvent(donationEventRepository.getReferenceById(donationDto.eventId()));
        donation.setDonorData(donorDataRepository.getReferenceById(donationDto.donorId()));
        String createdBy = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        donation.setCreatedBy(createdBy);
        Donation newDonation = donationRepository.saveAndFlush(donation);
        Blood blood = new Blood();
        blood.setBloodType(donation.getDonorData().getBloodType());
        blood.setRhesusFactor(donation.getDonorData().getRhesusFactor());
        blood.setBarcode(donationDto.blood().barcode());
        blood.setCreatedBy(createdBy);
        blood.setDonation(newDonation);
        blood.setFridge(fridgeRepository.getReferenceById(donationDto.blood().fridgeId()));
        return new DonationDto(newDonation, bloodRepository.saveAndFlush(blood));
    }

    public List<BloodDto> getAllBlood() {
        return bloodRepository.findAll().stream().map(BloodDto::new).toList();
    }

    public BloodDto getBlood(Integer id) {
        return new BloodDto(bloodRepository.getReferenceById(id));
    }

    public BloodDto updateBloodStatus(Integer id, BloodStatus status) {
        Blood blood = bloodRepository.getReferenceById(id);
        blood.setUseStatus(status);
        return new BloodDto(bloodRepository.saveAndFlush(blood));
    }

    public BloodDto updateBloodSpoiled(Integer id, Boolean spoiled) {
        Blood blood = bloodRepository.getReferenceById(id);
        blood.setSpoiled(spoiled);
        return new BloodDto(bloodRepository.saveAndFlush(blood));
    }
}
