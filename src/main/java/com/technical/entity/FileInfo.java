package com.technical.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "fileinfo")
public class FileInfo {

    @Id
    @Column(name = "fileinfoid")
    private Long fileInfoId;

    @Column(name = "fileLocation")
    private String fileLocation;

    @Column(name = "fileName")
    private String fileName;

    @Column(name = "fileSize")
    private Long fileSize;
}
