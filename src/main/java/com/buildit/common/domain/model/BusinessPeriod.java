package com.buildit.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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
    public  BusinessPeriod(){
        startDate = LocalDate.MIN;
        endDate = LocalDate.MIN;
    }

    LocalDate startDate;
    LocalDate endDate;
}
