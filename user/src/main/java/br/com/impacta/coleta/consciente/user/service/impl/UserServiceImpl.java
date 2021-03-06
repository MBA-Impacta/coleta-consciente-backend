package br.com.impacta.coleta.consciente.user.service.impl;

import br.com.impacta.coleta.consciente.user.domain.User;
import br.com.impacta.coleta.consciente.user.dto.*;
import br.com.impacta.coleta.consciente.user.repository.AuthorizationServerClient;
import br.com.impacta.coleta.consciente.user.repository.UserRepository;
import br.com.impacta.coleta.consciente.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorizationServerClient authorizationServerClient;

    @Override
    public List<UserDtoResponse> findAll() {
        var listUserResponse = this.userRepository.findAll()
                .stream()
                .map(UserServiceImpl::mapToDto)
                .collect(Collectors.toList());

        return listUserResponse;
    }

    @Override
    public UserDtoResponse findById(Long id) {

        var userDtoResponse = this.userRepository.findById(id)
                .map(UserServiceImpl::mapToDto)
                .orElse(null);

        return userDtoResponse;
    }

    @Override
    public UserDtoResponse findByEmail(String email) {
        var userDtoResponse = this.userRepository.findByEmail(email);

        return mapToDto(userDtoResponse);

    }

    @Override
    public UserDtoResponse createUser(UserDtoRequest userDtoRequest) {

        var user = mapToDto(
            this.userRepository.save(
                new User(
                    userDtoRequest.getName(),
                    userDtoRequest.getEmail()
                )
            )
        );


        this.authorizationServerClient.create(
            getHeaderMap(),
            new UserDto(
                    user.getName(),
                    userDtoRequest.getPassword(),
                    user.getEmail())
        );

        return user;
    }

    @Override
    public void delete(Long id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find Customer with id " + id));
        this.userRepository.delete(user);
    }

    @Override
    public UserDtoResponse update(UserDtoUpdate userDtoRequest, Long id) {

        var user = this.userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find Customer with id " + id));

        user.setName(userDtoRequest.getName());

        return mapToDto(this.userRepository.save(user));
    }

    public static UserDtoResponse mapToDto(User user) {
        if (user != null) {
            return new UserDtoResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail()
            );
        }
        return null;
    }

    public Map<String, String> getHeaderMap(){
        TokenDto tokenDto = this.authorizationServerClient.getToken(getTokenHeader(), "admin@admin.com", "123456");
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + tokenDto.getAccess_token());
        return headers;
    }

    public Map<String, String> getTokenHeader(){
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Basic Y29kZXJlZjokMmEkMTAkcDlQazBmUU5BUVNlc0k0dnV2S0EwT1phbkREMg==");
        return headers;
    }
}
