package com.spotroute.service;

import com.spotroute.core.enums.BookingStatus;
import com.spotroute.core.enums.DriverStatus;
import com.spotroute.core.enums.PaymentStatus;
import com.spotroute.core.enums.RideStatus;
import com.spotroute.dto.request.CreateBookingRequest;
import com.spotroute.dto.response.BookingResponse;
import com.spotroute.exception.ForbiddenException;
import com.spotroute.persistence.entity.*;
import com.spotroute.exception.BadRequestException;
import com.spotroute.exception.ResourceNotFoundException;
import com.spotroute.repository.*;
import com.spotroute.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final StopRepository stopRepository;

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest req, UUID rideId) {
        User user = SecurityUtil.getLoggedInUserFromContext();
        Ride ride = rideRepository.findById(rideId.toString())
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

        if (ride.getStatus() != RideStatus.SCHEDULED) {
            throw new BadRequestException("Ride is no longer available for booking");
        }
        if (ride.availableSeats() < req.getSeatCount()) {
            throw new BadRequestException("Not enough seats available. Only " + ride.availableSeats() + " left.");
        }
        Stop pickup = stopRepository.findById(req.getPickupStopId())
                .orElseThrow(() -> new ResourceNotFoundException("Pickup stop not found"));
        Stop destination = stopRepository.findById(req.getDestinationStopId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination stop not found"));

        if (!pickup.getRoute().getId().equals(ride.getRoute().getId())
                || !destination.getRoute().getId().equals(ride.getRoute().getId())) {
            throw new BadRequestException("Stops must belong to this ride's route");
        }
        if (pickup.getSequenceOrder() >= destination.getSequenceOrder()) {
            throw new BadRequestException("Pickup must come before destination on the route");
        }
        BigDecimal total = ride.getRoute().getPricePerSeat()
                .multiply(BigDecimal.valueOf(req.getSeatCount()));

        ride.setBookedSeats(ride.getBookedSeats() + req.getSeatCount());
        rideRepository.save(ride); //Version fires here

        Booking booking = Booking.builder()
                .user(user)
                .ride(ride)
                .pickupStop(pickup)
                .destinationStop(destination)
                .seatCount(req.getSeatCount())
                .totalAmount(total)
                .status(BookingStatus.PENDING)
                .build();

        return BookingResponse.from(bookingRepository.save(booking));
    }

    public List<BookingResponse> getUserBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(BookingResponse::from)
                .toList();
    }

    public BookingResponse getBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return BookingResponse.from(booking);
    }
}
