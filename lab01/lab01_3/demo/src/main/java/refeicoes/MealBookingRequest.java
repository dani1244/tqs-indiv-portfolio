package refeicoes;

public class MealBookingRequest {
    private String studentId;
    private String serviceShift;
    
    public MealBookingRequest(String studentId, String serviceShift) {
        this.studentId = studentId;
        this.serviceShift = serviceShift;
    }
    
    // Getters
    public String getStudentId() { 
        return studentId; }
    public String getServiceShift() { 
        return serviceShift; }
}