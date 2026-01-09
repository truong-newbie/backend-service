package vn.tayjava.backend_service.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.tayjava.backend_service.common.Gender;
import vn.tayjava.backend_service.common.UserStatus;
import vn.tayjava.backend_service.common.UserType;
import vn.tayjava.backend_service.controller.request.AddressRequest;
import vn.tayjava.backend_service.controller.request.UserCreationRequest;
import vn.tayjava.backend_service.controller.request.UserUpdateRequest;
import vn.tayjava.backend_service.controller.response.UserPageResponse;
import vn.tayjava.backend_service.controller.response.UserResponse;
import vn.tayjava.backend_service.exception.ResourceNotFoundException;
import vn.tayjava.backend_service.model.UserEntity;
import vn.tayjava.backend_service.repository.AddressRepository;
import vn.tayjava.backend_service.repository.UserRepository;
import vn.tayjava.backend_service.service.impl.UserServiceImpl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private UserService userService;

    private @Mock UserRepository userRepository;
    private @Mock AddressRepository addressRepository;
    private @Mock PasswordEncoder passwordEncoder;
    private @Mock EmailService emailService;

    private static UserEntity truong;
    private static UserEntity nam;
    @BeforeAll
    static void sbeforeAll(){
        truong=new UserEntity();
        UserEntity truong = new UserEntity();
        truong.setId(1L);
        truong.setFirstName("truong");
        truong.setLastName("Java");
        truong.setGender(Gender.MALE);
        truong.setBirthday(new Date());
        truong.setEmail("truong@gmail.com");
        truong.setPhone("0975118228");
        truong.setUsername("truong");
        truong.setPassword("password");
        truong.setType(UserType.USER);
        truong.setStatus(UserStatus.ACTIVE);

         nam = new UserEntity();
        nam.setId(2L);
        nam.setFirstName("Nam");
        nam.setLastName("Nguyen");
        nam.setGender(Gender.MALE);
        nam.setBirthday(new Date());
        nam.setEmail("nam.nguyen@example.com");
        nam.setPhone("0987654321");
        nam.setUsername("namnguyen");
        nam.setPassword("password123");
        nam.setType(UserType.USER);
        nam.setStatus(UserStatus.ACTIVE);



    }

    @BeforeEach
    void setUp() {
        //khoi tao buoc trien khai la userService
        userService= new UserServiceImpl(userRepository, addressRepository, passwordEncoder, emailService);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetListUsers_Success() {
        // gia lap phuong thuc
        Page<UserEntity> userPage= new PageImpl<>(Arrays.asList(truong, nam));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        //goi phuong thuc can test
        UserPageResponse result= userService.findAll(null, null, 0,20);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testSearchUser_Success() {
        // gia lap phuong thuc
        Page<UserEntity> userPage= new PageImpl<>(Arrays.asList(truong, nam));
        when(userRepository.searchByKeyword(any(), any(Pageable.class))).thenReturn(userPage);

        // goi phuong thuc can test
        UserPageResponse result= userService.findAll(null, null, 0,20);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testGetListUsers_Empty() {
        // gia lap phuong thuc
        Page<UserEntity> userPage= new PageImpl<>(List.of());
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        //goi phuong thuc can test
        UserPageResponse result= userService.findAll(null, null, 0,20);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testGetUserById_Success() {
        // gia lap phuong thuc
        when(userRepository.findById(1L)).thenReturn(Optional.of(truong));

        UserResponse result= userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetUserById_Failure() {
        // gia lap phuong thuc
        ResourceNotFoundException exception= assertThrows(ResourceNotFoundException.class,() -> userService.findById(2L));
        assertEquals("User not found", exception.getMessage());
    }


    @Test
    void findByUsername() {
    }

    @Test
    void findByEmail() {
    }

    @Test
    void testSaveUser() {

        //gia lap phuong thuc
        when(userRepository.save(any(UserEntity.class))).thenReturn(truong);

        //tao request
        UserCreationRequest userCreationRequest = new UserCreationRequest();
        userCreationRequest.setFirstName("Tay");
        userCreationRequest.setLastName("Java");
        userCreationRequest.setGender(Gender.MALE);
        userCreationRequest.setBirthday(new Date());
        userCreationRequest.setEmail("quoctay87@gmail.com");
        userCreationRequest.setPhone("0975118228");
        userCreationRequest.setUsername("tayjava");

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setApartmentNumber("ApartmentNumber");
        addressRequest.setFloor("Floor");
        addressRequest.setBuilding("Building");
        addressRequest.setStreetNumber("StreetNumber");
        addressRequest.setStreet("Street");
        addressRequest.setCity("City");
        addressRequest.setCountry("Country");
        addressRequest.setAddressType(1);

        userCreationRequest.setAddresses(List.of(addressRequest));

        long userId =userService.save(userCreationRequest);

        assertEquals(1L, userId);
    }

    @Test
    void update() {
        Long userId= 2L;

        UserEntity updatedUser = new UserEntity();
        updatedUser.setId(userId);
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Smith");
        updatedUser.setGender(Gender.FEMALE);
        updatedUser.setBirthday(new Date());
        updatedUser.setEmail("janesmith@gmail.com");
        updatedUser.setPhone("0123456789");
        updatedUser.setUsername("janesmith");
        updatedUser.setType(UserType.USER);
        updatedUser.setStatus(UserStatus.ACTIVE);

        //gia lap hanh vi cua userrpository
        when(userRepository.findById(userId)).thenReturn(Optional.of(nam));
        when(userRepository.save(any(UserEntity.class))).thenReturn(updatedUser);

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setId(userId);
        updateRequest.setFirstName("Jane");
        updateRequest.setLastName("Smith");
        updateRequest.setGender(Gender.MALE);
        updateRequest.setBirthday(new Date());
        updateRequest.setEmail("janesmith@gmail.com");
        updateRequest.setPhone("0123456789");
        updateRequest.setUsername("janesmith");

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setApartmentNumber("ApartmentNumber");
        addressRequest.setFloor("Floor");
        addressRequest.setBuilding("Building");
        addressRequest.setStreetNumber("StreetNumber");
        addressRequest.setStreet("Street");
        addressRequest.setCity("City");
        addressRequest.setCountry("Country");
        addressRequest.setAddressType(1);

//        updateRequest.setAddresses(List.of(addressRequest));
//        userService update(updateRequest);

        UserResponse result= userService.findById(userId);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
    }

    @Test
    void changePassword() {
    }

    @Test
    void testDeleteUser_Success() {
        Long userId=1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(truong));

        userService.delete(userId);

        assertEquals(UserStatus.INACTIVE , truong.getStatus());
        verify(userRepository, times(1)).save(truong);
    }
}