package com.data.processing.elastic.query.service.security;

import com.data.processing.elastic.query.service.service.QueryUserService;
import com.data.processing.elastic.query.service.transformer.UserPermissionsToUserDetailTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SourceQueryUserDetailsService implements UserDetailsService {

    private final QueryUserService queryUserService;
    private final UserPermissionsToUserDetailTransformer userPermissionsToUserDetailTransformer;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return queryUserService
                .findAllPermissionsByUsername(username)
                .map(userPermissionsToUserDetailTransformer::getUserDetails)
                .orElseThrow(
                        () -> new UsernameNotFoundException("No user found with name " + username));
    }

}
