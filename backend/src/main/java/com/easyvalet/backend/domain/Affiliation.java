package com.easyvalet.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Affiliation {
    private Long id;
    private String name;
    private boolean isPaid;
}
