package ua.nure.mossurd.blooddosyst.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ua.nure.mossurd.blooddosyst.dto.DonationEventDto;
import ua.nure.mossurd.blooddosyst.entity.DonationAppointment;
import ua.nure.mossurd.blooddosyst.entity.DonationEvent;
import ua.nure.mossurd.blooddosyst.enums.DonationEventStatus;
import ua.nure.mossurd.blooddosyst.repository.DonationAppointmentRepository;
import ua.nure.mossurd.blooddosyst.repository.DonationEventRepository;
import ua.nure.mossurd.blooddosyst.repository.DonorDataRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonorService {

    private final DonorDataRepository donorDataRepository;
    private final DonationEventRepository donationEventRepository;
    private final DonationAppointmentRepository donationAppointmentRepository;

    public List<DonationEventDto> getAvailableEvents() {
        return donationEventRepository
                .getByEventStatusInAndDateTimeAfter(List.of(DonationEventStatus.PLANNED, DonationEventStatus.ONGOING), LocalDateTime.now()).stream()
                .map(DonationEventDto::new)
                .toList();
    }

    public List<DonationEventDto> getEventsWithAppointments() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return donationEventRepository.findAllById(
                donationAppointmentRepository.getAllByDonorData(donorDataRepository.getByUser_username(username))
                        .stream().map(DonationAppointment::getDonationEvent).map(DonationEvent::getId).toList()).stream()
                .map(DonationEventDto::new)
                .toList();
    }

    public void createAppointment(Integer eventId) {
        DonationAppointment donationAppointment = new DonationAppointment();
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        donationAppointment.setDonorData(donorDataRepository.getByUser_username(username));
        donationAppointment.setDonationEvent(donationEventRepository.getReferenceById(eventId));
        donationAppointmentRepository.saveAndFlush(donationAppointment);
    }

    public void deleteAppointment(Integer eventId) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        donationAppointmentRepository.deleteAllByDonorDataAndDonationEvent_id(donorDataRepository.getByUser_username(username), eventId);
        donationAppointmentRepository.flush();
    }
}
