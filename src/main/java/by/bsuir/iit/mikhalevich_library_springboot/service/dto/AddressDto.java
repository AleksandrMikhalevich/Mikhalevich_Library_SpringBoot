package by.bsuir.iit.mikhalevich_library_springboot.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Alex Mikhalevich
 * @created 2022-07-09 15:22
 */
@Data
public class AddressDto implements Serializable {
    private final String country;
    private final String city;
    private final String street;
    private final String house;
    private final String zipcode;
}
