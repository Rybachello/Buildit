package com.buildit.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import javax.persistence.Embeddable;
import java.time.LocalDate;

/**
 * Created by minhi_000 on 20.03.2017.
 */
@Embeddable
@Value
@AllArgsConstructor(staticName = "of")
public class BusinessPeriod {
    LocalDate startDate;
    LocalDate endDate;
}
