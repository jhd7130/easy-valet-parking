package com.easyvalet.backend.adapter.in.web;

import com.easyvalet.backend.application.port.in.CustomerUseCase;
import com.easyvalet.backend.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerUseCase customerUseCase;

    @GetMapping("/search")
    public ResponseEntity<List<CustomerDto.Response>> search(
            @RequestParam(required = false) String carNumber,
            @RequestParam(required = false) String phoneNumber) {
        List<Customer> customers;
        if (carNumber != null && !carNumber.isEmpty()) {
            customers = customerUseCase.findCustomersByCarNumber(carNumber);
        } else if (phoneNumber != null && !phoneNumber.isEmpty()) {
            customers = customerUseCase.findCustomersByPhoneNumber(phoneNumber);
        } else {
            return ResponseEntity.badRequest().build();
        }

        List<CustomerDto.Response> responses = customers.stream()
                .map(CustomerDto.Response::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
