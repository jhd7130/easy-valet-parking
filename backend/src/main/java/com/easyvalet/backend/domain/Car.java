package com.easyvalet.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car {
    private Long id;
    private String carNumber;

    @Builder.Default
    private List<Customer> owners = new ArrayList<>();
}
