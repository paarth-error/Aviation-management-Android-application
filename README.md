# ✈️ AAI Aviation Management App

> A robust, Android-based mobile application developed to streamline aviation operations, automate flight hour tracking, and manage billing statuses.

This project was engineered during an internship at the **Airport Authority of India (AAI)** to provide a mobile-first, deeply integrated solution for aviation management workflows. It bridges the gap between a modern mobile UI and heavy backend data processing.

---

## ✨ Key Features

* **📊 Interactive Dashboard:** A centralized control hub (`DashboardActivity`) providing quick navigation and an overview of current aviation operations.
* **⏱️ Automated Air Hours Tracking:** Calculates and logs flight hours using an integrated Python processing engine (`flight_calculator.py` & `AirHoursActivity`).
* **💳 Billing Status Management:** Generates, reviews, and manages billing statuses for diverse flight records seamlessly (`billing_status.py` & `BillingStatusActivity`).
* **☁️ Data Upload Module:** A secure, dedicated pipeline (`UploadDataActivity`) to ingest and process incoming aviation metrics.
* **🎨 Modern UI/UX:** Features a polished, custom-styled interface complete with gradient overlays, rounded components, and custom vector iconography.

---

## 🛠️ Tech Stack

* **Frontend:** Android (Java & XML)
* **Backend Logic:** Python (Integrated for complex calculations)
* **IDE:** Android Studio
* **Build System:** Gradle

---

## 📁 Architecture & Structure

This project leverages a hybrid architecture, utilizing Java for the Android lifecycle and Python for computational heavy lifting:

* `app/src/main/java/.../aviation/` - Contains the core UI logic, Activity controllers, Adapters, and Data models.
* `app/src/main/python/` - Contains the algorithmic calculation engines (`billing_status.py` and `flight_calculator.py`).
* `app/src/main/res/` - Houses all custom XML layouts, theme definitions, gradient background files, and vector assets.

---

## 🚀 Getting Started

Follow these instructions to run this project locally on your machine:

### Prerequisites
* Android Studio (latest version recommended)
* A physical Android device or an active Android Emulator
* Git installed on your system

### Installation Steps

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/Paarth00/YOUR_REPO_NAME.git](https://github.com/Paarth00/YOUR_REPO_NAME.git)
   2. **Open the project:**
   Launch **Android Studio**, click on `Open`, and select the cloned repository folder.
3. **Sync dependencies:**
   Allow Android Studio to download the necessary Gradle dependencies and sync the project. *(Note: Ensure the Chaquopy plugin or your specific Python-Android integration tool is properly synced in `build.gradle`)*.
4. **Run the application:**
   Select your target device from the top menu and hit the green **Run** (▶) button.

---

## 📸 Screenshots


| Dashboard | Air Hours Tracker | Billing Status |
|:---:|:---:|:---:|
| <img src="link_to_dashboard_image.png" width="220" alt="Dashboard"> | <img src="link_to_air_hours_image.png" width="220" alt="Air Hours"> | <img src="link_to_billing_image.png" width="220" alt="Billing"> |

---

## 👨‍💻 Author

**Paarth Chhikara** * B.Tech Computer Science and Engineering Student
* Software Engineering Intern, Airport Authority of India (AAI)
