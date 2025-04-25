# 🏘️ SC2002 Build-To-Order (BTO) Management System

## 📌 1. Introduction

### Project Overview

This project is a **Build-To-Order (BTO) Management System** built in Java. It manages the application and processing of BTO (Build-To-Order) projects, with the ability to handle users (Applicants, Officers, Managers), project data, applications, and user enquiries through various CSV files.

### Purpose

The main goal of this project is to streamline and automate the management of BTO projects, ensuring data is handled efficiently and users are able to interact with the system through a smooth and user-friendly interface.

---

## ⚙️ 2. Getting Started

### Prerequisites

Before you start, make sure you have the following installed:

- **Java JDK 11** or higher
- **IDE**: IntelliJ IDEA, Eclipse, or any other Java-supported IDE

### Installation

1. Clone the repository:

    ```bash
    git clone https://github.com/shrujann/SC2002_Project.git
    cd SC2002_Project
    ```

### Running the Project

To run the project, you can run the `MainMenu.java` class through your IDE or from the command line:

### Project Structure

```
SC2002_Project/
├── boundary/                             # User interface and input/output handling
│   ├── input/                            # Classes related to user input
│   └── view/                             # Classes for displaying menus
│       └── MainMenu.java                 # Main entry point to run the application
│       └── [other classes...]            # Other menu-related classes
├── constants/                            # Constants used across the project
├── control/                              # Control logic for managing users and projects
├── csv/                                  # CSV file reading and writing
├── data/                                 # Data handling for user, project, and application data
├── entity/                               # Entity classes for representing data models
├── excel/                                # Excel file handling
├── logic/                                # Program Logic
├── util/                                 # Utility classes (eg: for password hashing)
└── README.md                             # This README file

```
## ✨ 3. Core Features

### 🔐 User Authentication
- SHA-256 password hashing for secure login and registration  
- Unique user identification by NRIC

### 🧑‍💼 Role-Based Menus
- Role-specific options for Applicant, Officer, and Manager  
- Menus dynamically presented based on login credentials

### 🏘️ BTO Application Handling
- Applicants can apply for projects within the application window  
- System enforces mutual exclusivity between officer and applicant roles for the same project

### 🧾 Officer Registration
- Officers may register to manage projects, subject to:
  - No existing role conflict  
  - No overlapping assignments across projects  
  - Slot availability

### ⏳ Future Project Assignment
- Approved officers are added to a `futureProjects` list if already handling another project  
- Enables early approvals without assignment overlap

### ✅ Manager Approval
- Managers can approve or reject officer registrations  
- System validates overlapping responsibilities before approval  
- All changes are saved and reflected across sessions

### 📄 Receipt Generation
- Officers can confirm flat bookings and generate applicant receipts containing:
  - Applicant details  
  - Flat type  
  - Project information

### 💾 Persistent CSV Storage
- Data is stored and updated via the provided CSV files  
- Central `DataManager` handles modular access to:
  - `UserDataManager`  
  - `ProjectDataManager`  
  - `ApplicationDataManager`
