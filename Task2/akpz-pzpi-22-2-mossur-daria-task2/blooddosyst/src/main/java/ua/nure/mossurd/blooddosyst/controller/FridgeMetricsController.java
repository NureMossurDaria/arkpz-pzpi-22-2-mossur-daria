package ua.nure.mossurd.blooddosyst.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.nure.mossurd.blooddosyst.dto.FridgeMetricDto;
import ua.nure.mossurd.blooddosyst.service.FridgeMetricsService;

@RestController
@RequestMapping(value = "/fridge/metrics")
@RequiredArgsConstructor
public class FridgeMetricsController {

    private final FridgeMetricsService fridgeMetricsService;

    @PostMapping
    public void createMetric(@RequestBody FridgeMetricDto metricDto) {
        fridgeMetricsService.createMetric(metricDto);
    }
}
