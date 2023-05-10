package com.elevatorsystem.repositories.elevator;

import com.elevatorsystem.domain.Elevator.Elevator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ElevatorJpaRepository extends JpaRepository<Elevator, UUID>, ElevatorRepository {
}
