package com.buildit.common.dto;

import com.buildit.common.rest.ResourceSupport;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * Created by minhi_000 on 22.03.2017.
 */
@Data
@AllArgsConstructor(staticName = "of")
public class BusinessPeriodDTO extends ResourceSupport{

    private LocalDate startDate;
    private LocalDate endDate;

    public BusinessPeriodDTO(){
        startDate = LocalDate.MIN;
        endDate = LocalDate.MIN;
    }
}
