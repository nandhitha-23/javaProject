# 🎓 Smart Lab Allocation System — Beginner's Guide to Every File

> **Who is this guide for?**  
> If you are a student who knows basic programming or Java, but you have **never used Spring Boot before**, this guide is made specially for you! No confusing jargon, no complex academic words — just clear, real-world explanations.

---

## 💡 The Big Picture: How Does This Web App Work?

Before looking at any files, imagine this project as a **Restaurant**:

```
 ┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
 │   1. CUSTOMER   │ ───►  │    2. WAITER    │ ───►  │    3. CHEF      │ ───►  │ 4. STOREKEEPER  │ ───► ┌─────────────┐
 │  (Web Browser)  │       │  (Controller)   │       │    (Service)    │       │  (Repository)   │      │ 5. PANTRY   │
 │  Sees HTML/CSS  │ ◄───  │ Takes order &   │ ◄───  │ Checks rules &  │ ◄───  │ Fetches data    │      │  (Database) │
 │  clicks buttons │       │ serves the page │       │ does calculations│      │ from storage    │      │ PostgreSQL  │
 └─────────────────┘       └─────────────────┘       └─────────────────┘       └─────────────────┘      └─────────────┘
```

1. **The Customer (HTML & CSS):** The screen the student or teacher looks at. They fill in a form or click "Book Seat".
2. **The Waiter (Controller):** Takes the request from the browser. The waiter doesn't cook the food; they just say: *"Hey Chef, someone wants to book Lab 2 at 10 AM!"*
3. **The Chef (Service):** The brain of the application. The chef checks the rules: *"Is the student valid? Is that time slot already taken? Is end time after start time?"*
4. **The Storekeeper (Repository):** The only one who goes into the storage room to get or save data.
5. **The Storage Room (Database - PostgreSQL):** The hard drive where student names, labs, and bookings are saved forever.
6. **The Food Box (Entity / Model):** A simple Java class used to pack up data (like a student's name and roll number) so it can be moved around.

---

## 🗂️ Quick Index of All Project Files

Click any file to jump directly to its explanation:

1. [Project Setup & Build Files](#1-project-setup--build-tools)
   - [pom.xml](#-pomxml) • [mvnw & mvnw.cmd](#-mvnw--mvnwcmd) • [maven-wrapper.properties](#-mvnwrapperproperties) • [.gitignore](#-gitignore-root) • [.gitattributes](#-gitattributes) • [README.md](#-readmemd)
2. [Project Settings & Startup](#2-settings--starting-the-app)
   - [application.properties](#-srcmainresourcesapplicationproperties) • [DemoApplication.java](#-demoapplicationjava)
3. [Admin Login & Security](#3-admin-login--security)
   - [Admin.java](#-adminjava) • [AdminRepository.java](#-adminrepositoryjava) • [AdminDetailsService.java](#-admindetailsservicejava) • [AdminDataInitializer.java](#-admindatainitializerjava) • [AdminController.java](#-admincontrollerjava) • [SecurityConfig.java](#-securityconfigjava)
4. [Student Management](#4-student-management)
   - [Student.java](#-studentjava) • [StudentRepository.java](#-studentrepositoryjava) • [StudentService.java](#-studentservicejava) • [StudentController.java](#-studentcontrollerjava) • [StudentPortalController.java](#-studentportalcontrollerjava)
5. [Laboratory Management](#5-laboratory-management)
   - [Lab.java](#-labjava) • [LabRepository.java](#-labrepositoryjava) • [LabService.java](#-labservicejava) • [LabController.java](#-labcontrollerjava)
6. [Lab Allocations & Seat Booking](#6-lab-allocations--seat-booking)
   - [LabAllocation.java](#-laballocationjava) • [LabAllocationRepository.java](#-laballocationrepositoryjava) • [LabAllocationService.java](#-laballocationservicejava) • [LabAllocationController.java](#-laballocationcontrollerjava) • [LabAvailabilityController.java](#-labavailabilitycontrollerjava)
7. [Dashboard & Analytics](#7-dashboard--analytics)
   - [DashboardController.java](#-dashboardcontrollerjava)
8. [Design & Styling](#8-styling--design)
   - [app.css](#-appcss)
9. [Web Pages (HTML Templates)](#9-web-pages-html-templates)
   - [navigation.html](#-navigationhtml) • [login.html](#-loginhtml) • [dashboard.html](#-dashboardhtml) • [students.html](#-studentshtml) • [edit-student.html](#-edit-studenthtml) • [labs.html](#-labshtml) • [add-lab.html](#-add-labhtml) • [allocations.html](#-allocationshtml) • [add-allocation.html](#-add-allocationhtml) • [lab-availability.html](#-lab-availabilityhtml) • [student-portal.html](#-student-portalhtml) • [student-dashboard.html](#-student-dashboardhtml) • [student-availability.html](#-student-availabilityhtml) • [student-labs.html](#-student-labshtml)
10. [Tests & Extra Files](#10-testing--extra-files)
   - [DemoApplicationTests.java](#-demoapplicationtestsjava) • [Nested .gitignore](#-nested-gitignore)

---

## 1. Project Setup & Build Tools

---

### 📄 `pom.xml`
- **Link:** [pom.xml](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/pom.xml)
- **What is it?** The **Shopping List** for your project.
- **Why do we need it?** When you write Java code, you don't want to code a web server or database connection from scratch. You tell Maven: *"Please download Spring Boot, PostgreSQL tools, and Web tools for me."* This file is that exact list.
- **What happens if it's missing?** Java wouldn't know which helper libraries to download, and nothing would compile.

---

### 📄 `mvnw` & `mvnw.cmd`
- **Links:** [mvnw](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/mvnw) (Linux/Mac) and [mvnw.cmd](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/mvnw.cmd) (Windows)
- **What is it?** The **Instant Maven Installer and Runner**.
- **Why do we need it?** If your friend downloads this project but doesn't have Maven installed on their laptop, they can just run `./mvnw.cmd spring-boot:run`. This script downloads Maven automatically in the background and runs the app.

---

### 📄 `.mvn/wrapper/maven-wrapper.properties`
- **Link:** [maven-wrapper.properties](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/.mvn/wrapper/maven-wrapper.properties)
- **What is it?** The settings file for the `mvnw` script.
- **What does it do?** It simply contains the download link for Maven version `3.10.0` so your computer knows where to grab Maven from.

---

### 📄 `.gitignore` (Root)
- **Link:** [.gitignore](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/.gitignore)
- **What is it?** A **"Do Not Upload" checklist** for Git and GitHub.
- **Why do we need it?** When Java compiles code, it creates hundreds of temporary `.class` files in the `target/` folder. This file tells Git: *"Never upload the `target/` folder or my private VS Code settings to GitHub."*

---

### 📄 `.gitattributes`
- **Link:** [.gitattributes](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/.gitattributes)
- **What is it?** An **Operating System Translator** for text lines.
- **Why do we need it?** Windows and Mac/Linux save "Enter" (new lines) differently. This file makes sure script files run cleanly on both Windows and Linux without breaking.

---

### 📄 `README.md`
- **Link:** [README.md](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/README.md)
- **What is it?** The **Welcome Notice** on GitHub describing the project name and short description.

---

## 2. Settings & Starting the App

---

### 📄 `src/main/resources/application.properties`
- **Link:** [application.properties](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/resources/application.properties)
- **What is it?** The **Master Control Panel** of the app.
- **Why do we need it?** It stores all settings in one easy-to-read text file:
  - Where the PostgreSQL database is: `localhost:5432/smart_lab`
  - The database username and password: `postgres` / `kashyap`
  - What port the website runs on: `8080` (so you visit `http://localhost:8080`)
  - The default admin username and password: `admin` / `java`

---

### 📄 `src/main/java/com/smartlab/demo/DemoApplication.java`
- **Link:** [DemoApplication.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/DemoApplication.java)
- **What is it?** The **Car Key / Power Button**.
- **What does it do?** It contains the `public static void main(String[] args)` method. When you click **Run** in Eclipse, IntelliJ, or VS Code, this is the file that boots up the website.

---

## 3. Admin Login & Security

---

### 📄 `Admin.java`
- **Link:** [Admin.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/Admin.java)
- **What is it?** The **Admin ID Card (Entity)**.
- **What does it do?** A standard Java class with variables: `id`, `username`, `password`, and `role`. It tells the database: *"Create a table called `admins` with these columns."*

---

### 📄 `AdminRepository.java`
- **Link:** [AdminRepository.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/AdminRepository.java)
- **What is it?** The **Admin Search Assistant**.
- **What does it do?** It gives you ready-made functions like `findByUsername("admin")` to find an administrator in the database without having to write raw SQL commands.

---

### 📄 `AdminDetailsService.java`
- **Link:** [AdminDetailsService.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/AdminDetailsService.java)
- **What is it?** The **Credential Checker**.
- **What does it do?** When someone types their username on the login screen, this file looks up that user in the database and gives the details to Spring Security to verify if the password matches.

---

### 📄 `AdminDataInitializer.java`
- **Link:** [AdminDataInitializer.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/AdminDataInitializer.java)
- **What is it?** The **First-Day Setup Worker**.
- **What does it do?** The very first time you start the app with an empty database, you wouldn't have any account to log in with! This file automatically creates an admin account (`admin` / `java`) with an encrypted password so you can log in immediately.

---

### 📄 `AdminController.java`
- **Link:** [AdminController.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/AdminController.java)
- **What is it?** The **Login Page Waiter**.
- **What does it do?** When someone types `http://localhost:8080/login`, this file catches that request and shows the `login.html` web page on their screen.

---

### 📄 `SecurityConfig.java`
- **Link:** [SecurityConfig.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/SecurityConfig.java)
- **What is it?** The **Security Guard / Bouncer**.
- **What does it do?** It decides who is allowed into which room:
  - **Public (Anyone can enter without logging in):** The login page (`/login`), the Student Portal (`/student/**`), and style sheets (`/css/**`).
  - **Private (Admin only):** All other pages (like adding students, modifying labs, or deleting allocations).
  - Also sets up the password hasher so passwords are never stored in plain text.

---

## 4. Student Management

---

### 📄 `Student.java`
- **Link:** [Student.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/model/Student.java)
- **What is it?** The **Student Record Card (Entity)**.
- **What does it do?** Holds information for a single student:
  - `studentId` (Unique ID number, 1, 2, 3...)
  - `studentName` (e.g., "John Doe")
  - `rollNo` (e.g., "21CS045" — must be unique)
  - `department` (e.g., "Computer Science")

---

### 📄 `StudentRepository.java`
- **Link:** [StudentRepository.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/repository/StudentRepository.java)
- **What is it?** The **Student Filing Cabinet Assistant**.
- **What does it do?** Provides easy methods to query the database:
  - `findByRollNo("21CS045")` — Finds a student by their roll number.
  - `existsByRollNo("21CS045")` — Checks if someone with this roll number is already registered so we don't allow duplicates.

---

### 📄 `StudentService.java`
- **Link:** [StudentService.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/service/StudentService.java)
- **What is it?** The **Student Office Manager (Business Logic)**.
- **What does it do?** Performs the actual work and checks the rules:
  - When adding a student: Checks if the roll number is already taken. If yes, stops and throws an error message.
  - When editing a student: Updates their name, roll number, and department.
  - When deleting a student: Removes their record from storage.

---

### 📄 `StudentController.java`
- **Link:** [StudentController.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/controller/StudentController.java)
- **What is it?** The **Admin Student Web Desk (Waiter)**.
- **What does it do?** Listens for clicks from the Admin Student Management page:
  - `GET /students` ➔ Shows the student list and registration form.
  - `POST /students/add` ➔ Reads form data and tells `StudentService` to save the student.
  - `GET /students/edit/{id}` ➔ Opens the edit page for that student.
  - `POST /students/update` ➔ Saves the updated details.
  - `POST /students/delete/{id}` ➔ Deletes that student.

---

### 📄 `StudentPortalController.java`
- **Link:** [StudentPortalController.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/controller/StudentPortalController.java)
- **What is it?** The **Student Self-Service Portal Desk**.
- **What does it do?** This is made specifically for students (no admin login needed!):
  - `GET /student/portal` ➔ Shows the friendly screen where students enter their Roll Number.
  - `GET /student/dashboard` ➔ Finds the student by their roll number and shows their personal timetable (Today's labs, Upcoming sessions, Past history).
  - `GET /student/availability` ➔ Lets students search which labs are free at a certain time.
  - `GET /student/labs` ➔ Shows students a list of all campus computer labs.

---

## 5. Laboratory Management

---

### 📄 `Lab.java`
- **Link:** [Lab.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/Lab.java)
- **What is it?** The **Laboratory Blueprint (Entity)**.
- **What does it do?** Represents a computer lab with variables:
  - `id` (e.g., 1)
  - `labName` (e.g., "AI & Machine Learning Lab")
  - `location` (e.g., "Science Block, Room 304")
  - `capacity` (e.g., 40 seats/computers)

---

### 📄 `LabRepository.java`
- **Link:** [LabRepository.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/LabRepository.java)
- **What is it?** The **Lab Database Assistant**.
- **What does it do?** Provides built-in methods to save labs, find all labs, find a lab by ID, or delete a lab from PostgreSQL.

---

### 📄 `LabService.java`
- **Link:** [LabService.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/LabService.java)
- **What is it?** The **Lab Facility Manager (Business Logic)**.
- **What does it do?** Handles the tasks of getting all labs, fetching a specific lab, creating/editing a lab, or removing a lab.

---

### 📄 `LabController.java`
- **Link:** [LabController.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/LabController.java)
- **What is it?** The **Admin Lab Web Desk (Waiter)**.
- **What does it do?** Handles URLs for lab management:
  - `GET /labs` ➔ Shows the table of all labs.
  - `GET /labs/add` ➔ Shows the "Create Lab" form.
  - `POST /labs/save` ➔ Saves the lab name, location, and seat count.
  - `GET /labs/edit/{id}` ➔ Loads lab info into the form for editing.
  - `GET /labs/delete/{id}` ➔ Removes a lab.

---

## 6. Lab Allocations & Seat Booking

---

### 📄 `LabAllocation.java`
- **Link:** [LabAllocation.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/LabAllocation.java)
- **What is it?** The **Booking Ticket (Entity)**.
- **What does it do?** Links everything together into one reservation:
  - Which **Student** booked it?
  - Which **Lab** is booked?
  - Which **Seat Number** (e.g., Seat 12)?
  - What **Date** (e.g., 2026-10-25)?
  - What **Start Time** (e.g., 10:00 AM) and **End Time** (e.g., 12:00 PM)?

---

### 📄 `LabAllocationRepository.java`
- **Link:** [LabAllocationRepository.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/LabAllocationRepository.java)
- **What is it?** The **Seat Conflict Detector**.
- **What does it do?** Contains a custom search query: `isSeatBooked(...)`. Given a lab, seat number, date, and time, it checks the database to see: *"Is someone already sitting in this seat right now?"* Returns `true` if occupied, `false` if free.

---

### 📄 `LabAllocationService.java`
- **Link:** [LabAllocationService.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/LabAllocationService.java)
- **What is it?** The **Allocation Rule Inspector (Business Logic)**.
- **What does it do?** Enforces strict rules before saving any booking:
  - ❌ Rejects if no student or lab is selected.
  - ❌ Rejects if end time is before start time (e.g., 11:00 AM to 10:00 AM is invalid!).
  - ❌ Checks all existing bookings to prevent the same lab from being double-booked at the same time.

---

### 📄 `LabAllocationController.java`
- **Link:** [LabAllocationController.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/LabAllocationController.java)
- **What is it?** The **Allocation & Instant Seat Booking Hub**.
- **What does it do?** Handles all reservation interactions for both administrators and students:
  - `GET /allocations` ➔ Shows the master table of all bookings.
  - `GET /allocations/add` ➔ Shows the booking form with student and lab dropdowns.
  - `POST /allocations/save` ➔ Saves whole-lab or session bookings.
  - `GET /allocations/delete/{id}` ➔ Cancels a reservation.
  - `POST /book-seat` ➔ Handles real-time workstation seat booking with conflict checks, verifying that the seat is free and reserving it for the student.

---

### 📄 `LabAvailabilityController.java`
- **Link:** [LabAvailabilityController.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/LabAvailabilityController.java)
- **What is it?** The **Availability Finder (Waiter)**.
- **What does it do?** Handles `GET /lab-availability`. When you pick a date and time range, this file loops through every seat in every lab. If a lab has open seats, it shows up as available!

---

## 7. Dashboard & Analytics

---

### 📄 `DashboardController.java`
- **Link:** [DashboardController.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/DashboardController.java)
- **What is it?** The **Homepage Summary Calculator**.
- **What does it do?** When an admin visits the homepage (`GET /`), this file asks the repositories:
  - *"How many students do we have?"* ➔ `studentRepository.count()`
  - *"How many labs do we have?"* ➔ `labRepository.count()`
  - *"How many active allocations?"* ➔ `allocationRepository.count()`  
  Then sends those numbers to `dashboard.html` to display the big counter cards.

---

## 8. Styling & Design

---

### 🎨 `app.css`
- **Link:** [app.css](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/resources/static/css/app.css)
- **What is it?** The **Painter & Decorator** of the website.
- **What does it do?** Without this file, the website would look like plain black-and-white 1995 text! This file provides:
  - Dark navy sidebar styling (`#0b1329`)
  - Beautiful font (`Plus Jakarta Sans`)
  - Modern buttons (blue for submit, red for delete)
  - Clean card layouts, rounded corners, and soft shadows
  - Responsive tables and badge colors (emerald green for success, purple for badges)

---

## 9. Web Pages (HTML Templates)

> *Note:* All these files are written in **HTML + Thymeleaf**. Thymeleaf allows HTML to display Java data using `th:text="${variable}"`.

---

### 🧩 `navigation.html` (Fragment)
- **Link:** [navigation.html](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/resources/templates/fragments/navigation.html)
- **What is it?** The **Sidebar Menu**.
- **What does it do?** Instead of copy-pasting the sidebar menu into every single HTML file, this one file is imported everywhere. It contains the menu links (Dashboard, Students, Labs, Allocations, Student Portal) and the "Sign Out" button.

---

### 🖥️ `login.html`
- **Link:** [login.html](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/resources/templates/login.html)
- **What is it?** The **Admin Login Screen**.
- **What does it do?** Shows a sleek, centered login box asking for Admin username and password. Also includes a link for students: *"Looking for Student Portal? Click here"*.

---

### 🖥️ `dashboard.html`
- **Link:** [dashboard.html](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/resources/templates/dashboard.html)
- **What is it?** The **Admin Homepage**.
- **What does it do?** Displays the big statistics cards (Total Students, Total Labs, Active Bookings) and shortcut buttons to quickly add new allocations.

---

### 🖥️ `students.html`
- **Link:** [students.html](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/resources/templates/students.html)
- **What is it?** The **Student Management Page**.
- **What does it do?** Has two parts:
  1. Top: A form to register a new student (Name, Roll No, Department).
  2. Bottom: A table listing all existing students with "Edit" and "Delete" buttons.

---

### 🖥️ `edit-student.html`
- **Link:** [edit-student.html](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/resources/templates/edit-student.html)
- **What is it?** The **Edit Student Screen**.
- **What does it do?** Shows the student's current name and roll number in editable text boxes so you can fix typos and click "Save Changes".

---

### 🖥️ `labs.html`
- **Link:** [labs.html](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/resources/templates/labs.html)
- **What is it?** The **Laboratories List**.
- **What does it do?** Displays all computer labs in a table showing their room location and total seats, with an "+ Add New Lab" button at the top.

---

### 🖥️ `add-lab.html`
- **Link:** [add-lab.html](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/resources/templates/add-lab.html)
- **What is it?** The **Create/Edit Lab Form**.
- **What does it do?** Provides text fields for Lab Name, Room/Location, and Maximum Seating Capacity.

---

### 🖥️ `allocations.html`
- **Link:** [allocations.html](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/resources/templates/allocations.html)
- **What is it?** The **Master Bookings Schedule**.
- **What does it do?** A table showing every scheduled session: Student Name, Roll Number, Lab Name, Date, and Time Slot.

---

### 🖥️ `add-allocation.html`
- **Link:** [add-allocation.html](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/resources/templates/add-allocation.html)
- **What is it?** The **New Reservation Form**.
- **What does it do?** Has dropdown menus to pick a student and a lab, along with date and time pickers.

---

### 🖥️ `lab-availability.html`
- **Link:** [lab-availability.html](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/resources/templates/lab-availability.html)
- **What is it?** The **Visual Seat Map (Like Booking Movie Tickets!)**.
- **What does it do?** You pick a date and time, and it draws little boxes for every seat in the lab:
  - 🟩 **Green Box:** The seat is empty! You can click it to book it.
  - 🟥 **Red Box:** The seat is already occupied by another student.

---

### 🖥️ `student-portal.html`
- **Link:** [student-portal.html](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/resources/templates/student-portal.html)
- **What is it?** The **Student Roll Number Login Page**.
- **What does it do?** Students don't need a password; they just type their roll number (e.g. `21CS001`) to view their timetable.

---

### 🖥️ `student-dashboard.html`
- **Link:** [student-dashboard.html](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/resources/templates/student-dashboard.html)
- **What is it?** The **Student's Personal Dashboard**.
- **What does it do?** Displays the student's name, department, and three clear tabs:
  - 📅 **Today's Labs:** Sessions happening today.
  - ⏳ **Upcoming Bookings:** Future lab slots.
  - 📜 **History:** Past attended sessions.

---

### 🖥️ `student-availability.html`
- **Link:** [student-availability.html](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/resources/templates/student-availability.html)
- **What is it?** The **Student Lab Search Page**.
- **What does it do?** Allows students to search which labs are free during their free periods so they can go practice coding.

---

### 🖥️ `student-labs.html`
- **Link:** [student-labs.html](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/resources/templates/student-labs.html)
- **What is it?** The **Campus Lab Directory for Students**.
- **What does it do?** A guide showing students all available computer labs on campus, their room numbers, and how many computers each lab has.

---

## 10. Testing & Extra Files

---

### 🧪 `DemoApplicationTests.java`
- **Link:** [DemoApplicationTests.java](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/test/java/com/smartlab/demo/DemoApplicationTests.java)
- **What is it?** The **Health Check Test**.
- **What does it do?** A small automated test. When you run `mvn test`, it starts the app for 2 seconds to make sure there are no typos or broken configs, then exits.

---

### 📄 Nested `.gitignore`
- **Link:** [src/main/java/com/smartlab/demo/.gitignore](file:///c:/Users/alpha/OneDrive/Documents/Projects/smart-lab-allocation/src/main/java/com/smartlab/demo/.gitignore)
- **What is it?** A safety reminder file reminding developers never to commit database passwords or build caches.

---

## 🚀 Cheat Sheet: Where Does Data Flow When You Click a Button?

Here is an example: **Adding a New Student**:

```
1. You fill out the form in students.html and click "Add Student"
         ▼
2. The browser sends data to StudentController.java (@PostMapping("/students/add"))
         ▼
3. StudentController gives the data to StudentService.java
         ▼
4. StudentService checks: "Does this roll number already exist?"
   - If YES ➔ Sends an error message back to the screen: "Roll number already exists!"
   - If NO  ➔ Tells StudentRepository.java to save it
         ▼
5. StudentRepository saves the student into the PostgreSQL table ("students")
         ▼
6. StudentController reloads students.html showing the new student in the table!
```

---

*This document was written to make the Smart Lab Allocation System completely clear and enjoyable to learn for any student!*
