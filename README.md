# GroupFlow App

GroupFlow is an Android application developed using **Clean Architecture** principles and **role-based access control** to serve both **Patients** and **Employees** in a clinical environment. The system enables users to manage appointments, view ultrasound scans, submit reviews, and access clinic information.

Referenced principles include Clean Architecture, which emphasises separation into domain, data, and UI layers (Martin, 2018), and role-based access control using Firebase Auth and Security Rules (Firebase, 2025; TheNewGenCoder, 2023).

This app uses **Firebase Realtime Database (RTDB)** for storing and synchronizing data in real time. RTDB was chosen for its low latency, real-time syncing, and event-driven updates across clients (Firebase, 2025).

---

## 📦 Project Structure (Sprint 7)

```plaintext
com.example.groupflow
│
├── core
│   ├── domain              // Entities & Value Objects
│   │   ├── User.kt
│   │   ├── Appointment.kt
│   │   ├── DoctorInfo.kt
│   │   ├── Employee.kt
│   │   ├── Notification.kt
│   │   ├── Patient.kt
│   │   ├── UltrascanImage.kt
│   │   └── Review.kt
│   ├── service             // Port Interfaces
│   │   ├── AuthenticationService.kt
│   │   ├── AppointmentService.kt
│   │   ├── MyFirebaseMessagingService.kt
│   │   ├── ImagingService.kt
│   │   └── ReviewService.kt
│   └── util                // Constants, Mappers, Exceptions
│
├── data                    // Adapter Implementations (Repositories)
│   ├── auth
│   │   └── FirebaseAuthAdapter.kt
│   ├── appointment
│   │   └── FirebaseAppointmentRepo.kt
│   ├── scan
│   │   └── FirebaseScanRepo.kt
│   ├── review
│   │   └── FirebaseReviewRepo.kt
│   ├── notification
│   │   └── FirebaseNotificationRepo.kt
│   └── AppDatabase.kt      // Singleton for accessing in-memory repos
│
├── models
│   ├── NotificationModel.kt
│   ├── UltrascanModel.kt
│   └── UserModel.kt
│
├── ui                      // Activities and UI Layer
│   ├── auth
│   │   ├── LoginActivity.kt
│   │   ├── Session.kt
│   │   └── RegisterActivity.kt
│   ├── appointments
│   │   ├── AppointmentsActivity.kt
│   │   └── RequestAppointmentActivity.kt
│   ├── ultrascans
│   │   ├── UltrascansActivity.kt
│   │   ├── UltrascanAdapter
│   │   └── UploadUltrascanActivity.kt
│   ├── reviews
│   │   ├── ReviewsActivity.kt
│   │   ├── ReviewAdapter.kt
│   │   └── LeaveReviewActivity.kt
│   ├── profile
│   │   └── UserProfileActivity.kt
│   ├── hubs
│   │   └── EmployeeHubActivity.kt
│   ├── info
│   │   ├── ClinicInfoActivity.kt
│   │   └── DoctorInfoActivity.kt
│   ├── notifications
│   │   ├── NotificationsActivity.kt
│   │   └── NotificationsAdapter.kt
│   └── patients
│   │   ├── PatientAdapter.kt
│   │   └── PatientSelectionActivity.kt
├── GroupFlowApplication.kt   //  base Application class for global initialisation 
└── MainActivity.kt           // Acts as Patient Dashboard
```

---

## 🧪 Functionality Summary

| Feature                    | Patient Access   | Employee Access        |
|----------------------------|------------------|------------------------|
| Login / Register           | ✅              | ✅                     |
| View Appointments          | ✅              | 🔜                     |
| Request Appointment        | ✅              | ❌                     |
| Upload Ultrascans          | ❌              | ✅                     |
| View Ultrascans            | ✅              | 🔜                     |
| Submit & View Reviews      | ✅              | ✅                     |
| Access Clinic Info         | ✅              | ✅                     |
| Receive Notifications      | ✅              | ✅                     |
| Dynamic Dashboards         | ✅ MainActivity | ✅ EmployeeHubActivity |

---

## 🔐 Access Control

| User Role   | Dashboard Entry          | Access Restrictions                      |
|-------------|--------------------------|------------------------------------------|
| Patient     | `MainActivity.kt`        | Cannot upload scans                      |
| Employee    | `EmployeeHubActivity.kt` | Cannot request appointments              |

The access control model follows RBAC, where Firebase Auth and Firestore Security Rules enforce permissions based on user roles (Firebase, 2025; TheNewGenCoder, 2023).

## ⚙️ CI/CD

- ✅ GitHub Actions configured for:
  - Build verification
  - Linting & formatting
  - Testing workflows
- ✅ Branch protection enabled on `main`
- ✅ Modular build scripts for future Gradle improvements

---

# Current Sprint
## ✅ Sprint 7: Secondary Feature Implementation (Deadline: 15th September)
### 🎯 Goals
- Implement **Push Notifications** using Firebase Cloud Messaging (FCM) with role-based targeting (Patients vs Employees).
- Implement **Appointments** functionality:
  - Patients can request appointments.
  - Employees can view and manage appointments.
- Implement **Reviews** functionality:
  - Patients can submit reviews (`LeaveReviewActivity`).
  - Both Patients and Employees can view reviews (`ReviewsActivity` with `ReviewAdapter`).

### ✅ Objectives Achieved

- 🔔 **Notifications (FCM + Cloud Functions)**:
  - Integrated Firebase Cloud Messaging in MyFirebaseMessagingService.
  - Configured `index.js` Cloud Function to send notifications per patient/employee role.
  - Patients receive scan upload notifications; Employees receive appointment requests.

- 📅 **Appointments Module**:
  - Added `AppointmentsActivity` to display upcoming appointments.
  - Added `RequestAppointmentActivity` where patients can submit appointment requests.
  - Appointment data stored and synced via Firebase Realtime Database (RTDB).

- 📝 **Reviews Module**:
  - Added `LeaveReviewActivity` for patients to write reviews.
  - Implemented `ReviewsActivity` + `ReviewAdapter` to fetch and display reviews dynamically from RTDB.
  - Employees can read feedback to improve services.

---

# Previous Sprints
## Sprint 4: Project Structure and Initial Implementation
**Duration:** 26th June – 8th July  
**Goal:**  
Set up the core infrastructure of the application, initialize the GitHub repository and CI/CD pipeline, and scaffold all necessary classes and layout files to support modular development of GroupFlow (Martin, 2018).

### ✅ Objectives Achieved
- 📁 Defined and implemented a **Clean Architecture structure**:
    - Separation into **domain**, **data**, and **UI** layers.
    - Defined entities, repositories, services, and UI screens per role.
- ⚙️ CI/CD Pipeline:
    - Initialized GitHub repository.
    - GitHub Actions configured for basic linting and build checks (to be expanded).
- 🧱 Initial Implementation:
    - Designed layouts for all core activities (login, register, dashboard, etc.).
    - Created mock repositories using in-memory data classes.
    - Implemented navigation between screens based on user role.

---

## Sprint 5: Core Feature Implementation ( Deadline: 25th August)

**Goal:**
- Replace in-memory stubs with **Firebase Firestore / Firebase Auth** services
- Implement service logic in `core.service` to connect data and UI
- Build ViewModels to support state-based UI updates
- Add **role-based dashboard behavior**:
  - Patients → `MainActivity`
  - Employees → `EmployeeHubActivity`

### ✅ Objectives Achieved
- 🔐 Authentication: Implemented FirebaseAuthAdapter with email/password registration, login, logout, and session persistence.
- 👥 Role-Based Access: Patients → MainActivity; Employees → EmployeeHubActivity.

- 🗄 Firebase Repositories:
  - `FirebaseAppointmentRepo`
  - `FirebaseScanRepo`
  - `FirebaseReviewRepo`
  - `FirebaseNotificationRepo`

- 🕒 Date Handling: Added Converters for LocalDateTime ↔ Long storage.

- 🌐 Lifecycle + Coroutines:
  - Used `lifecycleScope.launch {}` in Activities to safely call Firebase suspend functions (Google, 2025).
  - Ensured data fetching and updates respect Activity lifecycle.
- 💾 SessionManager:
  - Stores `userId`, `role`, and persists across app restarts.
  - Enforces access restrictions before navigating to Activities.
---

## 🔜 Sprint 6: Integration and Testing (Deadline: 18th August)

### 🎯 Goals
- Full **Firebase integration** (Auth, Realtime Database, Storage)
- Connect all screens to real data
- Comprehensive **UI & unit testing**
- Finalize bottom navigation and toolbar behavior across roles

---

## 💡 Summaries
- Sprint 4 established a clean, modular structure with mock repos.
- Sprint 5 integrated Firebase, role-based navigation, and lifecycle-aware coroutines.
- Sprint 6 will finalize integration, strengthen testing, and deliver production readiness.
- Sprint 7 extended the system with Appointment and review funtionalities, with real-time notifications via FCM.

---

🧱 *Built using Android, Kotlin, Material 3, and Clean Architecture.*

---

## 👥 Contributors

- **Scrum-Master & Lead Front-End Developer:** *[Javier Pena Gonzalez]*
- **Lead Back-End Developer (Database & Infrastructure):** *[Fungho Baloyi / GitHub Handle]*
- **Architecture & Design Lead:** *[Lefa Matunda]*
- **Lead Back-End Developer (API & Integration):** *[Karabo Latakgomo]*
- **Requirements, Testing & QA:** *[Sandile Ndukula]*

---

## 📚 References

- Firebase. 2025. *Role-based access with Firebase Auth and Firestore*. Available at: [https://firebase.google.com/docs/firestore/solutions/role-based-access](https://firebase.google.com/docs/firestore/solutions/role-based-access) (Accessed: 4 August 2025).  
- Firebase. 2025. Cloud Functions for Firebase. Available at: [https://firebase.google.com/docs/functions] (https://firebase.google.com/docs/functions) (Accessed: 5 September 2025).
- Firebase. 2025. Firebase Cloud Messaging. Available at: [https://firebase.google.com/docs/cloud-messaging] (https://firebase.google.com/docs/cloud-messaging) (Accessed: 5 September 2025).
- Google. 2025. *Kotlin coroutines on Android*. Available at: [https://developer.android.com/kotlin/coroutines](https://developer.android.com/kotlin/coroutines) (Accessed: 4 August 2025).  
- Martin, R.C. 2018. *Clean architecture: A craftsman’s guide to software structure and design*. Upper Saddle River, NJ: Prentice Hall.  
- TheNewGenCoder. 2023. *Firebase authentication with role-based access control (RBAC)*. Medium. Available at: [https://medium.com/@theNewGenCoder/firebase-authentication-with-role-based-access-control-rbac-e2eee803283b](https://medium.com/@theNewGenCoder/firebase-authentication-with-role-based-access-control-rbac-e2eee803283b) (Accessed: 4 August 2025). 

---

_© 2025 GroupFlow Team — Built with passion and purpose._
