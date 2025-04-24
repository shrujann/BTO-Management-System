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

### Running the Project

To run the project, you can should run the `MainMenu.java` class through your IDE or from the command line:

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
