package vn.tayjava.backend_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.tayjava.backend_service.common.UserStatus;
import vn.tayjava.backend_service.controller.request.UserCreationRequest;
import vn.tayjava.backend_service.controller.request.UserUpdatePassword;
import vn.tayjava.backend_service.controller.request.UserUpdateRequest;
import vn.tayjava.backend_service.controller.response.UserPageResponse;
import vn.tayjava.backend_service.controller.response.UserResponse;
import vn.tayjava.backend_service.exception.ResourceNotFoundException;
import vn.tayjava.backend_service.model.AddressEntity;
import vn.tayjava.backend_service.model.UserEntity;
import vn.tayjava.backend_service.repository.AddressRepository;
import vn.tayjava.backend_service.repository.UserRepository;
import vn.tayjava.backend_service.service.UserService;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j(topic=" USER-SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserPageResponse findAll(String keyword, String sort, int page, int size) {


        //sort
        Sort.Order order= new Sort.Order(Sort.Direction.ASC,"id");
        if(StringUtils.hasLength(keyword)){
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");  //tencot : asc| desc
            Matcher matcher = pattern.matcher(sort);
            if(matcher.find()){
                if(matcher.group(3).equalsIgnoreCase("asc")){
                    String columnName = matcher.group(1);
                     order= new Sort.Order(Sort.Direction.ASC,"columnName");
                }else{
                    order= new Sort.Order(Sort.Direction.DESC,"columnName");
                }
            }
        }

        //paging
        Pageable pageable= PageRequest.of(page, size, Sort.by(order));

        Page<UserEntity> entityPage;

        if(StringUtils.hasLength(keyword)){
            keyword="%" + keyword.toLowerCase() + "%";
            entityPage = userRepository.searchByKeyword(keyword, pageable);
        }else{
            entityPage = userRepository.findAll(pageable);
        }

        Page<UserEntity> userEntities= userRepository.findAll(pageable);
        UserPageResponse response= getUserPageResponse(page, size, entityPage);
        return response;

    }

    @Override
    public UserResponse findById(Long id) {
        log.info("findById" ,id);

        UserEntity userEntity =getUserEntityById(id);

        return UserResponse.builder()
                .id(id)
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .gender(userEntity.getGender())
                .birthday(userEntity.getBirthday())
                .username(userEntity.getUsername())
                .phone(userEntity.getPhone())
                .email(userEntity.getEmail())
                .build();
    }

    @Override
    public UserResponse findByUsername(String username) {
        return null;
    }

    @Override
    public UserResponse findByEmail(String email) {
        return null;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public long save(UserCreationRequest req) {
        log.info("saving user {}", req);
        UserEntity user = new UserEntity();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setGender(req.getGender());
        user.setBirthday(req.getBirthday());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setUsername(req.getUsername());
        user.setType(req.getType());
        user.setStatus(UserStatus.NONE);

        userRepository.save(user);
        log.info("saved user:{}", user);

        if (user.getId() != null) {
            log.info("userid {}", user.getId());
            List<AddressEntity> addresses = new ArrayList<>();
            req.getAddresses().forEach(address -> {
                AddressEntity addressEntity = new AddressEntity();
                addressEntity.setApartmentNumber(address.getApartmentNumber());
                addressEntity.setFloor(address.getFloor());
                addressEntity.setBuilding(address.getBuilding());
                addressEntity.setStreetNumber(address.getStreetNumber());
                addressEntity.setStreet(address.getStreet());
                addressEntity.setCity(address.getCity());
                addressEntity.setCountry(address.getCountry());
                addressEntity.setAddressType(address.getAddressType());
                addressEntity.setUserId(user.getId());
                addresses.add(addressEntity);
            });

            addressRepository.saveAll(addresses);
            log.info("Saved addresses: {}", addresses);
        }
        return user.getId();

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void update(UserUpdateRequest req) {
        log.info("Updating user: {}" , req);
        //get user by id
        UserEntity user= getUserEntityById(req.getId());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setGender(req.getGender());
        user.setBirthday(req.getBirthday());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setUsername(req.getUsername());

        userRepository.save(user);

        log.info("Updated user: {}", user);

        //save address
        List<AddressEntity> addresses = new ArrayList<>();
        req.getAddresses().forEach(address -> {
            AddressEntity addressEntity = addressRepository.findByUserIdAndAddressType(user.getId(), address.getAddressType());

            if (addressEntity == null) {
                addressEntity = new AddressEntity();
            }
            addressEntity.setApartmentNumber(address.getApartmentNumber());
            addressEntity.setFloor(address.getFloor());
            addressEntity.setBuilding(address.getBuilding());
            addressEntity.setStreetNumber(address.getStreetNumber());
            addressEntity.setStreet(address.getStreet());
            addressEntity.setCity(address.getCity());
            addressEntity.setCountry(address.getCountry());
            addressEntity.setAddressType(address.getAddressType());
            addressEntity.setUserId(user.getId());

            addresses.add(addressEntity);
        });
        addressRepository.saveAll(addresses);
        //set data

        //save to db

    }

    @Override
    public void changePassword(UserUpdatePassword req) {
        log.info("changing password for user:{}");

        // get user by id
        UserEntity user= getUserEntityById(req.getId());
        if(req.getPassword().equals(req.getConfirmPassword())) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        userRepository.save(user);
        log.info("changed password for user:{}", user);
    }

    @Override
    public void delete(Long id) {
        log.info("deleting user: {}", id);

        //get user by id
        UserEntity user= getUserEntityById(id);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        log.info("deleted user:{}" , user);
    }

    private UserEntity getUserEntityById(Long id){
        return userRepository.findById(id).orElseThrow( ()-> new ResourceNotFoundException("User not found"));
    }

    //convert userentities to userRes
    private static UserPageResponse getUserPageResponse( int page, int size , Page<UserEntity> userEntities) {
        List<UserResponse> userList= userEntities.stream().map(
                entity -> UserResponse.builder()
                        .id(entity.getId())
                        .firstName(entity.getFirstName())
                        .lastName(entity.getLastName())
                        .gender(entity.getGender())
                        .birthday((entity.getBirthday()))
                        .username(entity.getUsername())
                        .phone(entity.getPhone())
                        .email(entity.getEmail())
                        .build()
        ).toList();

        //xu li truong hop FE muon bat dau voi page=1
        int pageNo=0;
        if(page>0){
            pageNo= page-1;
        }
        UserPageResponse response= new UserPageResponse();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(userEntities.getTotalElements());
        response.setTotalPages(userEntities.getTotalPages());
        response.setUsers(userList);
        return response;
    }

}
