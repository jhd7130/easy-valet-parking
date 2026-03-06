package com.easyvalet.backend.application.port.in;

import com.easyvalet.backend.domain.Affiliation;
import java.util.List;

public interface GetAffiliationsUseCase {
    List<Affiliation> getPaidAffiliations();
}
