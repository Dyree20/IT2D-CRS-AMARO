============================
CAR RENTAL SYSTEM - USER & ADMIN MANUAL
============================

1. INTRODUCTION
----------------------------
This Car Rental System is a desktop application for managing a car rental business. It allows admins and employees to manage vehicles, clients, and rentals, and provides secure login and password reset features.

2. SYSTEM REQUIREMENTS
----------------------------
- Java JDK 8 or higher
- MySQL Database
- NetBeans IDE (recommended for development)

3. SETUP INSTRUCTIONS
----------------------------
1. Import the project into NetBeans or your preferred Java IDE.
2. Ensure your MySQL server is running and create a database named `vrs`.
3. Import the provided SQL file (`vrs (2).sql`) into your MySQL database to create the necessary tables.
4. Update the database connection details in `src/config/dbConnector.java` if your MySQL username or password is different.
5. Build and run the project from your IDE.

4. LOGIN
----------------------------
- Start the application.
- Enter your username and password.
- Click the 'LOGIN' button.
- If you do not have an account, click 'Register' to create one (admin approval may be required).
- If you forgot your password, click 'Forgot Password?' and follow the instructions to reset it using your email and security questions.

5. VEHICLE MANAGEMENT
----------------------------
- Only 4-wheel vehicles are supported: AUV, SUV, and VAN.
- To add a vehicle:
  1. Go to the 'Add Vehicles' section.
  2. Fill in the vehicle details (Make, Model, Year, Plate Number, Rate, Status, Type).
  3. Select the type (AUV, SUV, or VAN).
  4. Optionally upload an image.
  5. Click 'Save' to add the vehicle.
- To edit or delete a vehicle, select it from the list and use the 'Edit' or 'Delete' buttons.
- Use the search and filter options to find vehicles by type or keyword.

6. CLIENT & RENTAL MANAGEMENT
----------------------------
- Add, edit, or delete client records from the Clients section.
- Manage rental transactions, including assigning vehicles to clients and tracking rental status.

7. PASSWORD RESET
----------------------------
- Click 'Forgot Password?' on the login screen.
- Enter your registered email address.
- Answer your security questions.
- Set a new password if your answers are correct.

8. ADMIN FEATURES
----------------------------
- Admins can approve new user registrations.
- Admins can view logs of user activity.
- Admins have full access to all vehicle, client, and rental management features.

9. TROUBLESHOOTING
----------------------------
- If you encounter database connection errors, check your MySQL server and credentials in `dbConnector.java`.
- If images do not display, ensure the image files are in the correct directory (`src/images/`).
- For any UI issues, use NetBeans GUI Builder to adjust layouts.

10. SUPPORT
----------------------------
For further assistance, contact your system administrator or the project maintainer.

============================
END OF MANUAL
============================ 