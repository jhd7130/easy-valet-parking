package com.easyvalet.backend.adapter.in.web;

import com.easyvalet.backend.domain.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CustomerDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String name;
        private String phoneNumber;

        public static Response from(Customer customer) {
            return Response.builder()
                    .id(customer.getId())
                    .name(customer.getName())
                    .phoneNumber(customer.getPhoneNumber())
                    .build();
        }
    }
}
