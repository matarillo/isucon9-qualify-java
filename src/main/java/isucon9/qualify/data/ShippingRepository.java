package isucon9.qualify.data;

import org.springframework.data.repository.CrudRepository;

import isucon9.qualify.dto.Shipping;

public interface ShippingRepository extends CrudRepository<Shipping, Long> {
    
}
