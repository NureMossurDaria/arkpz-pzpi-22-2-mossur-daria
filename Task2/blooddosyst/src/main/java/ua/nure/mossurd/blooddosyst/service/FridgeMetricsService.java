package ua.nure.mossurd.blooddosyst.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.nure.mossurd.blooddosyst.dto.FridgeMetricDto;
import ua.nure.mossurd.blooddosyst.entity.Blood;
import ua.nure.mossurd.blooddosyst.entity.Fridge;
import ua.nure.mossurd.blooddosyst.entity.FridgeMetric;
import ua.nure.mossurd.blooddosyst.repository.BloodRepository;
import ua.nure.mossurd.blooddosyst.repository.FridgeMetricsRepository;
import ua.nure.mossurd.blooddosyst.repository.FridgeRepository;
import ua.nure.mossurd.blooddosyst.repository.NotificationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FridgeMetricsService {

    private final FridgeRepository fridgeRepository;
    private final FridgeMetricsRepository fridgeMetricsRepository;
    private final BloodRepository bloodRepository;

    public void createMetric(FridgeMetricDto metricDto) {
        FridgeMetric metric = new FridgeMetric();
        Fridge fridge = fridgeRepository.getReferenceById(metricDto.fridgeId());
        metric.setFridge(fridge);
        metric.setDateTime(metricDto.dateTime());
        metric.setHumidityPercent(metricDto.humidityPercent());
        metric.setTempCelsius(metricDto.tempCelsius());
        fridgeMetricsRepository.saveAndFlush(metric);

        if (metric.getTempCelsius().compareTo(fridge.getTempCelsiusMin()) < 0
                || metric.getTempCelsius().compareTo(fridge.getTempCelsiusMax()) > 0
                || metric.getHumidityPercent().compareTo(fridge.getHumidityPercentMin()) < 0
                || metric.getHumidityPercent().compareTo(fridge.getHumidityPercentMax()) > 0) {
            List<Blood> bloodList = bloodRepository.findAllByFridge(fridge);
            bloodList.forEach(blood -> blood.setSpoiled(true));
            bloodRepository.saveAllAndFlush(bloodList);
        }
    }
}
