package refeicoes;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MealsBookingService {
    private final Map<String, Reservation> reservationsByToken = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> studentReservationsByShift = new ConcurrentHashMap<>();
    private final Map<String, Integer> shiftCapacity = new ConcurrentHashMap<>();
    private final Map<String, Integer> currentBookingsPerShift = new ConcurrentHashMap<>();
    private final int defaultCapacity = 100;
    
    public MealsBookingService() {
        // Initialize default capacities
        shiftCapacity.put("lunch", defaultCapacity);
        shiftCapacity.put("dinner", defaultCapacity);
    }
    
    public Reservation bookMeal(MealBookingRequest request) {
        validateBookingRequest(request);
        
        String shiftKey = request.getServiceShift();
        currentBookingsPerShift.putIfAbsent(shiftKey, 0);
        
        // Check capacity
        if (currentBookingsPerShift.get(shiftKey) >= shiftCapacity.getOrDefault(shiftKey, defaultCapacity)) {
            throw new IllegalStateException("No available spots for this shift");
        }
        
        // Create reservation
        Reservation reservation = new Reservation(request.getStudentId(), request.getServiceShift());
        
        // Store reservation
        reservationsByToken.put(reservation.getToken(), reservation);
        studentReservationsByShift
            .computeIfAbsent(request.getStudentId(), k -> new HashSet<>())
            .add(request.getServiceShift());
        currentBookingsPerShift.merge(shiftKey, 1, Integer::sum);
        
        return reservation;
    }
    
    public Optional<Reservation> getReservation(String token) {
        return Optional.ofNullable(reservationsByToken.get(token));
    }
    
    public boolean verifyReservation(String token) {
        Reservation reservation = reservationsByToken.get(token);
        return reservation != null && !reservation.isUsed() && !reservation.isCancelled();
    }
    
    public boolean checkIn(String token) {
        Reservation reservation = reservationsByToken.get(token);
        if (reservation == null || reservation.isUsed() || reservation.isCancelled()) {
            return false;
        }
        
        reservation.markAsUsed();
        currentBookingsPerShift.merge(reservation.getServiceShift(), -1, Integer::sum);
        return true;
    }
    
    public boolean cancelReservation(String token) {
        Reservation reservation = reservationsByToken.get(token);
        if (reservation == null || reservation.isUsed() || reservation.isCancelled()) {
            return false;
        }
        
        reservation.cancel();
        studentReservationsByShift.get(reservation.getStudentId()).remove(reservation.getServiceShift());
        currentBookingsPerShift.merge(reservation.getServiceShift(), -1, Integer::sum);
        return true;
    }
    
    public void setShiftCapacity(String shift, int capacity) {
        shiftCapacity.put(shift, capacity);
    }
    
    public int getAvailableSpots(String shift) {
        int current = currentBookingsPerShift.getOrDefault(shift, 0);
        int capacity = shiftCapacity.getOrDefault(shift, defaultCapacity);
        return Math.max(0, capacity - current);
    }
    
    private void validateBookingRequest(MealBookingRequest request) {
        if (request.getStudentId() == null || request.getStudentId().trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID is required");
        }
        if (request.getServiceShift() == null || request.getServiceShift().trim().isEmpty()) {
            throw new IllegalArgumentException("Service shift is required");
        }
        
        // Check if student already has reservation for this shift
        Set<String> studentShifts = studentReservationsByShift.get(request.getStudentId());
        if (studentShifts != null && studentShifts.contains(request.getServiceShift())) {
            throw new IllegalStateException("Student already has a reservation for this shift");
        }
    }
}