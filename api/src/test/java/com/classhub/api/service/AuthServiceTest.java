package com.classhub.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.classhub.api.domain.ApiUser;
import com.classhub.api.dto.ApiDtos.ChangePasswordRequest;
import com.classhub.api.dto.ApiDtos.MessageResponse;
import com.classhub.api.repository.ApiUserRepository;
import com.classhub.api.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private ApiUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService; // not used in this test but required for constructor

    @InjectMocks
    private AuthService authService;

    @Test
    void testChangePasswordSuccess() {
        // Arrange
        String email = "usuario@example.com";
        String newPassword = "NovaSenha123";
        String encodedPassword = "encodedNewPassword";

        ApiUser existingUser = mock(ApiUser.class);

        // The service will set the new password hash via setSenhaHash; it's a mock, so no state change needed.
        // Ensure repository save receives this mock.
        

        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(ApiUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChangePasswordRequest request = new ChangePasswordRequest(email, newPassword);

        // Act
        MessageResponse response = authService.changePassword(request);

        // Assert
        assertNotNull(response);
        assertEquals("Senha alterada com sucesso.", response.mensagem());
        verify(existingUser).setSenhaHash(encodedPassword);
        verify(userRepository).save(existingUser);
    }
}
