# 📚 LMS – Learning Management System (Backend Module)

This project is part of a larger system focused on learning management, and I contributed to the backend development.

## 🛠 Tech Stack

- **Language:** ![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white)  
- **Framework:** Spring Boot ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-green)
- **Database:** ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white) 
- **API Integration:** org.kohsuke.github ![GitHub API](https://img.shields.io/badge/GitHub%20API-Integration-lightgrey)
- **Project Management:** Kanban methodology ![Kanban](https://img.shields.io/badge/Kanban-Methodology-yellowgreen)
- **Version Control:** GitHub (pull requests, branch management, code reviews) ![GitHub](https://img.shields.io/badge/GitHub-Source%20Control-blue)

## 🛠 My Contribution

As a Backend Developer, I worked on the server-side functionality of the Learning Management System. My responsibilities included:

- ✅ **REST API Development:** Implemented secure and efficient APIs using Spring Boot
- ✅ **Cohort Management:** Developed endpoints for creating and editing cohorts
- ✅ **Validation:** Ensured uniqueness of specific cohort fields
- ✅ **Error Handling:** Implemented structured error messages for API responses
- ✅ **GitHub API Integration:** Automated repository creation using org.kohsuke.github
- ✅ **Database Management:** Used MySQL for data storage and optimization
- ✅ **Version Control & Task Management:** Managed tasks in Jira (Kanban) and used GitHub for pull requests and code reviews

## 📌 Implemented Features

### Cohort Management:
- ✅ Create and edit cohorts
- ✅ Ensure unique fields for cohorts
- ✅ Error handling and meaningful validation messages

### GitHub API Integration:
- ✅ Automatically create repositories for new cohorts
- ✅ Manage repositories programmatically

## 🚀 How to Run Locally

1️⃣ Clone the repository:

git clone https://github.com/AleksandraCheidze/LMS.git  
cd LMS-Backend
git clone https://github.com/AleksandraCheidze/LMS.git

2️⃣ Set up environment variables in application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/lms_db  

spring.datasource.username=your_username  

spring.datasource.password=your_password  

github.api.token=your_github_token

3️⃣ Run the application:

mvn spring-boot:run

The backend will start on http://localhost:8080

📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
