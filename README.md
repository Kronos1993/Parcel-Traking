# Parcel Tracking App (ZipCargo Tracker)

![ZipCargo Tracker Logo](app_logo.png)

The Parcel Tracking App (ZipCargo Tracker) is an Android application that allows users to track their packages once they arrive at the ZipCargo box in Miami. The app follows the Clean Architecture principles and utilizes MVVM design pattern for a modular, maintainable, and testable codebase. It uses Retrofit for API communication, Dependency Injection for managing dependencies, and consists of several modules: app, data, core, resources, logger, and domain.

## Features

- Track package status once it arrives at ZipCargo box in Miami.
- User-friendly interface for easy navigation and interaction.
- Real-time updates and notifications on package status changes.
- Seamless integration with ZipCargo's tracking API.

## Modules

The project is structured into several modules to ensure a clean and modular codebase:

1. **app**: The main module that contains the UI layer, including Activities, Fragments, ViewModels, and UI-related components.

2. **data**: This module handles data access and communication with the remote server using Retrofit. It contains repositories, data sources, and entity mappers.

3. **core**: The core module contains shared utility classes, common constants, and base classes that are used across different modules.

4. **resources**: This module includes resources such as strings, colors, dimensions, and drawables to maintain a centralized resource management.

5. **logger**: The logger module provides logging functionalities for the entire application.

6. **domain**: The domain module holds the business logic and use cases of the application. It defines the interfaces for repositories and use cases.

## Tech Stack

- **Android Architecture Components**: Utilizes ViewModel and LiveData for the MVVM architecture.
- **Retrofit**: For handling API communication with the ZipCargo tracking service.
- **Dagger (or Hilt)**: Dependency Injection for managing dependencies in a clean and modular way.
- **Gson**: For JSON parsing and serialization.
- **Coroutines**: For handling asynchronous tasks in a clean and efficient manner.
- **Room**: For local data storage and caching (if needed).

## Prerequisites

- Android Studio (latest version)
- Gradle (latest version)
- Kotlin (latest stable version)

## Setup

1. Clone the repository to your local machine.

```bash
git clone https://github.com/Kronos1993/Parcel-Traking.git
```

2. Open the project in Android Studio.

3. Build the project to download the required dependencies.

4. Update the API endpoint in the `data` module's Retrofit service with the ZipCargo tracking API URL.

5. Run the app on either an emulator or a physical device to start tracking your packages!

## Contributions

Contributions to the ZipCargo Tracker app are welcome. If you find any issues or have suggestions for improvements, feel free to create a pull request or open an issue.

## License

This app is open-source.

## Contact

For any inquiries or support, please contact the project maintainers at [contact@zipcargotracker.com](mailto:contact@zipcargotracker.com).

---

Thank you for using ZipCargo Tracker! We hope you find it helpful in tracking your packages seamlessly. If you have any questions or need further assistance, don't hesitate to contact us. Happy package tracking!
