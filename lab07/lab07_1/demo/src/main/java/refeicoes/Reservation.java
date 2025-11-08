package refeicoes;

import java.time.LocalDateTime;
import java.util.UUID;

public class Reservation {
    private String token;
    private String studentId;
    private String serviceShift;
    private LocalDateTime reservationTime;
    private boolean used;
    private boolean cancelled;
    
    public Reservation(String studentId, String serviceShift) {
        this.token = UUID.randomUUID().toString().substring(0, 8);
        this.studentId = studentId;
        this.serviceShift = serviceShift;
        this.reservationTime = LocalDateTime.now();
        this.used = false;
        this.cancelled = false;
    }
    
    // Getters
    public String getToken() { return token; }
    public String getStudentId() { return studentId; }
    public String getServiceShift() { return serviceShift; }
    public LocalDateTime getReservationTime() { return reservationTime; }
    public boolean isUsed() { return used; }
    public boolean isCancelled() { return cancelled; }
    
    // Setters
    public void markAsUsed() { this.used = true; }
    public void cancel() { this.cancelled = true; }
}