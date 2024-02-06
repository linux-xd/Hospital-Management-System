package HospitalManagementSystem;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;

import Secrets.Secrets;

public class HospitalManagementSystem {
    private static final String url = Secrets.getUrl();
    private static final String username = Secrets.getUsername();
    private static final String password = Secrets.getPassword();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            while (true) {
                System.out.println("<--HOSPITAL MANAGEMENT SYSTEM-->");
                System.out.println("1. Add Patient.");
                System.out.println("2. View Patients.");
                System.out.println("3. View Doctors.");
                System.out.println("4. Book Appointment.");
                System.out.println("5. Exit");
                System.out.println();
                System.out.print("Enter Your Choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        // add patient
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        // view patient
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        // view doctors
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        // book appointment
                        bookAppointment(patient, doctor, connection, scanner);
                        System.out.println();
                        break;
                    case 5:
                        // exit
                        return;
                    default:
                        System.out.println("Enter valid choice!!");
                        System.out.println();
                        break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        scanner.close();
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.print("Enter Patient ID: ");
        int patientId = scanner.nextInt();

        System.out.print("Enter Doctor ID: ");
        int doctorId = scanner.nextInt();

        System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();

        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
            if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
                String appointmentQuerry = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";

                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuerry);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);

                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("Appointment Booked!!");
                    } else {
                        System.out.println("Failed To Book Appointment!!");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Either Doctor Or PAtient Doesn't Exists!!");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String querry = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND patient_id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(querry);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
