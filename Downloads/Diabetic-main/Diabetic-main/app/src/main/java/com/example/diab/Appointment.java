package com.example.diab;

public class Appointment {
    private String patientName;
    private String doctorSpeciality;
    private String reasonForAppointment;
    private String patientHistory;
    private String appointmentDateTime;

    // Constructor
    public Appointment(String patientName, String doctorSpeciality, String reasonForAppointment,
                       String patientHistory, String appointmentDateTime) {
        this.patientName = patientName;
        this.doctorSpeciality = doctorSpeciality;
        this.reasonForAppointment = reasonForAppointment;
        this.patientHistory = patientHistory;
        this.appointmentDateTime = appointmentDateTime;
    }

    // Getters and Setters
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorSpeciality() {
        return doctorSpeciality;
    }

    public void setDoctorSpeciality(String doctorSpeciality) {
        this.doctorSpeciality = doctorSpeciality;
    }

    public String getReasonForAppointment() {
        return reasonForAppointment;
    }

    public void setReasonForAppointment(String reasonForAppointment) {
        this.reasonForAppointment = reasonForAppointment;
    }

    public String getPatientHistory() {
        return patientHistory;
    }

    public void setPatientHistory(String patientHistory) {
        this.patientHistory = patientHistory;
    }

    public String getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(String appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }
}
