package com.football.dev.footballapp.services.impl;
import com.football.dev.footballapp.dto.UserEntityDto;
import com.football.dev.footballapp.mapper.UserEntityToDTOMapper;
import com.football.dev.footballapp.models.UserEntity;
import com.football.dev.footballapp.models.enums.Gender;
import com.football.dev.footballapp.repository.jparepository.UserRepository;
import com.football.dev.footballapp.services.FileUploadService;
import com.football.dev.footballapp.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.function.Function;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Function<UserEntityDto, UserEntity> userEntityDtoToUserEntity;
    private final FileUploadService fileUploadService;
    private final UserEntityToDTOMapper userEntityMapper;
    public UserServiceImpl(UserRepository userRepository, Function<UserEntityDto, UserEntity> userEntityDtoToUserEntity,
            FileUploadService fileUploadService, UserEntityToDTOMapper userEntityMapper
){
        this.userRepository=userRepository;
        this.userEntityDtoToUserEntity = userEntityDtoToUserEntity;
        this.fileUploadService=fileUploadService;
        this.userEntityMapper = userEntityMapper;
    }
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
    @Override
    public void updateUserStatus(Long userId, boolean enabled) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(enabled);
        userRepository.save(user);

    }
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsDeleted(true);
        userRepository.save(user);
    }

    @Override
    public List<UserEntity> getUsersByRole(String role) {
        return userRepository.findByRoleDescription(role);
    }
    @Override
    public void saveUser(UserEntityDto userDto) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String loggedInUserEmail = authentication.getName();

            UserEntity loggedInUser = userRepository.findByEmail(loggedInUserEmail)
                    .orElseThrow(() -> new RuntimeException("Logged-in user not found"));


            UserEntity userEntity = userEntityDtoToUserEntity.apply(userDto);

            userEntity.setId(loggedInUser.getId());
            userEntity.setEmail(loggedInUserEmail);
            userEntity.setPassword(loggedInUser.getPassword());
            userEntity.setRole(loggedInUser.getRole());
            userRepository.save(userEntity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save user: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateUser(Long userId, UserEntityDto updatedUserDto) {
        if(updatedUserDto == null) return;
        UserEntity userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userToUpdate.setFirstName(updatedUserDto.firstName());
        userToUpdate.setLastName(updatedUserDto.lastName());
        userToUpdate.setPhone(updatedUserDto.phone());
        userToUpdate.setCountry(updatedUserDto.country());
        userToUpdate.setBirthDate(updatedUserDto.birthDate());
        userToUpdate.setProfile_picture(updatedUserDto.profile_picture());
        userToUpdate.setAddress(updatedUserDto.address());
        userToUpdate.setCity(updatedUserDto.city());
        userToUpdate.setPostal_code(updatedUserDto.postal_code());
        userToUpdate.setGender(Gender.valueOf(updatedUserDto.gender()));
        userRepository.save(userToUpdate);

    }

    @Override
    public UserEntity getUserProfile(Long userId) {
        if(userId == null || userId < 0) throw new IllegalArgumentException("User id must be a positive non-zero value");
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public Page<UserEntityDto> getUsersByRoleAndIsDeleted(String role, boolean isDeleted, int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<UserEntity> userPage = userRepository.findByRoleDescriptionAndIsDeleted(role, isDeleted, pageRequest);
        Page<UserEntityDto> userDtos = userPage.map(userEntityMapper);
        return userDtos;
    }


    @Override
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("User not found with that email"));

    }
}
