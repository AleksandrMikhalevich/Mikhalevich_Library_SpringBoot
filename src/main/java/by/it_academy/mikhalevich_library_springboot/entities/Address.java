package by.it_academy.mikhalevich_library_springboot.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class Address {

    private String country;

    private String city;

    private String street;

    private String house;

    private String zipcode;
}