package vn.tayjava.backend_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.tayjava.backend_service.model.AddressEntity;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    AddressEntity findByUserIdAndAddressType(Long userId, int addressType);
}
