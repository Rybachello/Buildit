package com.buildit.common.domain.infastructure;

import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by rybachello on 06/03/2017.
 */
@Service
public class IdentifierFactory {
    public static String nextID() {
        return UUID.randomUUID().toString();
    }
}
