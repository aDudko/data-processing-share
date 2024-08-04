package com.data.processing.elastic.query.service.dataaccess.entity;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Entity
public class UserPermission {

    @Id
    @NotNull
    private UUID id;

    @NotNull
    private String username;

    @NotNull
    private String documentId;

    @NotNull
    private String permissionType;

}
