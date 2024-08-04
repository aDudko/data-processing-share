package com.data.processing.elastic.query.service.transformer;

import com.data.processing.elastic.query.service.dataaccess.entity.UserPermission;
import com.data.processing.elastic.query.service.security.PermissionType;
import com.data.processing.elastic.query.service.security.SourceQueryUser;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserPermissionsToUserDetailTransformer {

    public SourceQueryUser getUserDetails(List<UserPermission> userPermissions) {
        return SourceQueryUser.builder()
                .username(userPermissions.get(0).getUsername())
                .permissions(userPermissions.stream()
                        .collect(Collectors.toMap(
                                UserPermission::getDocumentId,
                                permission -> PermissionType.valueOf(permission.getPermissionType()))))
                .build();
    }

}
