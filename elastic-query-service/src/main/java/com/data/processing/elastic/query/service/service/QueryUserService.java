package com.data.processing.elastic.query.service.service;


import com.data.processing.elastic.query.service.dataaccess.entity.UserPermission;

import java.util.List;
import java.util.Optional;

public interface QueryUserService {

    Optional<List<UserPermission>> findAllPermissionsByUsername(String username);

}
