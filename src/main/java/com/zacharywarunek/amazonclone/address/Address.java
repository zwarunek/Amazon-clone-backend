package com.zacharywarunek.amazonclone.address;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Address {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

}
