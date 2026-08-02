package com.technical.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "shop_evidence")
public class ShopEvidence {

    @Id
    @Column(name = "shopEvidenceId")
    private Long shopEvidenceId;

    @Column(name = "SellerType")
    private String sellerType;

    @Column(name = "IDType")
    private String idType;

    @Column(name = "NRCPhotos")
    private String nrcPhotos;

    @Column(name = "PassportPhotos")
    private String passportPhotos;

    @Column(name = "FacePhotos")
    private String facePhotos;

    @Column(name = "CarLicense")
    private String carLicense;

    @Column(name = "FullName")
    private String fullName;

    @Column(name = "Passport number")
    private String passportNumber;

    private String birthdate;

    @Column(name = "Bank Name")
    private String bankName;

    @Column(name = "Bank Account Name")
    private String bankAccountName;

    @Column(name = "Bank Account Number")
    private String bankAccountNumber;

    @Column(name = "BankImages")
    private String bankImages;
}
