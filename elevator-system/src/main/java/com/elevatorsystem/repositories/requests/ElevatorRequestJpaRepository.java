package com.elevatorsystem.repositories.requests;

import com.elevatorsystem.domain.Request.ElevatorRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ElevatorRequestJpaRepository extends JpaRepository<ElevatorRequest, UUID>, ElevatorRequestRepository {
    List<ElevatorRequest> findByElevatorId(UUID id);
}