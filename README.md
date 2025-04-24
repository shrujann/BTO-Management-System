# SC2002 Project

## 1. Introduction

### Project Overview

This project is a **BTO Management System** built in Java. It manages the application and processing of BTO (Build-To-Order) projects, with the ability to handle users (Applicants, Officers, Managers), project data, applications, and user enquiries through various CSV files.

### Purpose

The main goal of this project is to streamline and automate the management of BTO projects, ensuring data is handled efficiently and users are able to interact with the system through a smooth and user-friendly interface.

---

## 2. Getting Started

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

2. Build the project using Maven:

    ```bash
    mvn clean install
    ```

### Running the Project

To run the project, you can either run the `MainMenu.java` class through your IDE or from the command line:

```bash
mvn exec:java -Dexec.mainClass="MainMenu"
```

### Project Structure

```
SC2002_Project/
├── constants/          # Constants used in the project
├── control/            # Control logic for managing the application
├── csv/                # Classes for handling CSV file reading and writing
├── data/               # Data handling related files
├── entity/             # Entity classes for user, project, etc.
├── excel/              # Excel file handling, if applicable
├── logic/              # Core business logic of the system
├── util/               # Utility classes (e.g., password hashing)
├── .DS_Store           # macOS file (can be ignored)
├── README.md           # This readme file
└── MainMenu.java       # Main entry point to run the application
```
