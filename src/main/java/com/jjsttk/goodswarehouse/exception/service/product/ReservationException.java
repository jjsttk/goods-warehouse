package com.jjsttk.goodswarehouse.exception.service.product;

import com.jjsttk.goodswarehouse.shared.enums.product.ReservationStatus;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class ReservationException extends RuntimeException {
    private static final String HEADER = "Problems occurred during reservation: ";
    private static final String ENTRY_FORMAT = "Id: %s, Problem: %s";
    private final Map<UUID, ReservationStatus> reservationProblemMap;

    public ReservationException(@NonNull Map<UUID, ReservationStatus> reservationProblemMap) {
        super(formatMessage(reservationProblemMap));
        this.reservationProblemMap = reservationProblemMap;
    }

    private static String formatMessage(Map<UUID, ReservationStatus> problems) {
        var details = problems.entrySet().stream()
                .map(entry -> String.format(ENTRY_FORMAT, entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("; "));

        return HEADER + details;
    }
}
