# 🏘️ SC2002 Build-To-Order (BTO) Management System

## 📌 1. Introduction

### Project Overview

This project is a **Build-To-Order (BTO) Management System** built in Java. It manages the application and processing of BTO (Build-To-Order) projects, with the ability to handle users (Applicants, Officers, Managers), project data, applications, and user enquiries through various CSV files. Team Members include Nicholas Yoong, Shrujan, Wei Hong, Sabareesh and Ming Tzern.

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
    git clone https://github.com/shrujann/BTO-Management-System.git
    cd BTO-Management-System
    ```

### Running the Project

To run the project, you can run the `MainMenu.java` class through your IDE or from the command line:

### Project Structure

```
BTO-Management-System/
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

## 📚 5. Documentation

### 📊 UML Diagrams

This project includes both class and sequence UML diagrams to illustrate the system's architecture and workflows.

- **[Class Diagram](https://github.com/shrujann/SC2002_Project/tree/main/UML%20Diagrams/Class%20Diagram)**  
  Visualises the relationships within the clases — including key classes like `User`, `Applicant`, `BTOProject`, and their relationships.

- **[Sequence Diagrams](https://github.com/shrujann/SC2002_Project/tree/main/UML%20Diagrams/Sequence%20Diagrams)**  
  Demonstrates the interaction flow for key use cases such as:
  - HDB Officer applying as a BTO applicant
  - HDB Officer registering to manage a project
  - HDB Manager approving/rejecting a HDB officer


> 📂 All diagrams are available in the [`UML Diagrams/`](https://github.com/shrujann/SC2002_Project/tree/main/UML%20Diagrams) directory.

---

### 📄 Javadocs

The application includes a comprehensive set of **Javadocs** to help developers understand the structure and functionality of the codebase.

To view them:

1. Clone the repository.
2. Open `javadoc/index.html` in your web browser.

> 🔍 The Javadocs provide detailed documentation for every class, method, and package in the project.
