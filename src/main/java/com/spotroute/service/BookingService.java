package com.spotroute.service;

import com.spotroute.core.enums.BookingStatus;
import com.spotroute.core.enums.PaymentStatus;
import com.spotroute.core.enums.RideStatus;
import com.spotroute.dto.request.CreateBookingRequest;
import com.spotroute.dto.response.BookingResponse;
import com.spotroute.entity.Booking;
import com.spotroute.entity.Ride;
import com.spotroute.entity.User;
import com.spotroute.exception.BadRequestException;
import com.spotroute.exception.ResourceNotFoundException;
import com.spotroute.repository.BookingRepository;
import com.spotroute.repository.RideRepository;
import com.spotroute.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingResponse createBooking(String userEmail, CreateBookingRequest req) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Ride ride = rideRepository.findById(req.getRideId())
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

        if (ride.getStatus() != RideStatus.SCHEDULED) {
            throw new BadRequestException("Ride is no longer available for booking");
        }

        if (ride.availableSeats() < req.getSeatCount()) {
            throw new BadRequestException("Not enough seats available. Only " + ride.availableSeats() + " left.");
        }

        boolean validPickup = ride.getRoute().getPickupPoints().contains(req.getPickupPoint());
        if (!validPickup) {
            throw new BadRequestException("Invalid pickup point for this route");
        }

        BigDecimal total = ride.getRoute().getPricePerSeat()
                .multiply(BigDecimal.valueOf(req.getSeatCount()));

        Booking booking = Booking.builder()
                .user(user)
                .ride(ride)
                .seatCount(req.getSeatCount())
                .pickupPoint(req.getPickupPoint())
                .totalAmount(total)
                .status(BookingStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        bookingRepository.save(booking);

        // Reserve seats immediately (released if payment fails)
        ride.setBookedSeats(ride.getBookedSeats() + req.getSeatCount());
        rideRepository.save(ride);

        return BookingResponse.from(booking);
    }

    public List<BookingResponse> getUserBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(BookingResponse::from)
                .toList();
    }

    public BookingResponse getBooking(String bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new ResourceNotFoundException("Booking not found");
        }

        return BookingResponse.from(booking);
    }
}
