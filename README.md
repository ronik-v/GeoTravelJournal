# GeoTravelJournal

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)

## EN
A mobile application for Android featuring Yandex Map integration, custom route navigation, and geolocation-based tracking. The app is connected to a backend that stores route history and includes a mock authentication/registration system (implemented as a demonstration since this is an educational project).

---

## Features

- **Yandex Map Integration:**  
  Display interactive maps using the Yandex Map SDK.

- **Custom Navigation & Route Building:**  
  Build and navigate routes based on geolocation data (sourced from Google). A marker indicates your current location on the map.

- **Backend Connectivity:**  
  Save and view your route history through a connected backend service.

- **Mock Authentication/Registration:**  
  A simplified, non-production authentication system for demonstration purposes.

---

## Technologies Used

- **Android Development:** Java/Kotlin  
- **Map Integration:** Yandex Map SDK  
- **Geolocation:** Google Geolocation API  
- **Backend Communication:** RESTful API (for route history management)  
- **Authentication:** Mock implementation (for educational use)

---

## Installation

1. **Clone the Repository:**  
   ```bash
   git clone https://github.com/your-repo
2. **Open in Android Studio:**  
   Import the project into Android Studio.

3. **Configure API Keys:**  
   - **Set up your Yandex Map SDK API key**  
   - **Configure the Google Geolocation API key. Adjust these keys in the appropriate configuration files.**

4. **Build & Run:**  
   Build the project and run it on an Android device or emulator.

---

## Notes

- This is a study/educational project.
- The authentication/registration feature is a mock-up and not fully functional.

---

## License

This project is licensed under the MIT License.

---

## RU

Учебное мобильное приложение для Android, в котором реализована интеграция карты от Яндекса, костюмная навигация с построением маршрутов на основе геолокационных данных (геолокация предоставляется Google), а также отображение текущего местоположения пользователя с помощью маркера.

#### Функционал

- **Интеграция карты Яндекса:**  
  Отображение интерактивной карты с использованием Yandex Map SDK.
- **Костюмная навигация и построение маршрутов:**  
  Возможность построения маршрутов и навигация с использованием геолокационных данных (полученных от Google). Текущее местоположение пользователя отмечается на карте.
- **Связь с бекендом:**  
  Хранение и отображение истории маршрутов через подключение к серверу.
- **Моковая авторизация/регистрация:**  
  Демонстрационная система авторизации, которая не является полноценной, так как проект носит учебный характер.

#### Используемые технологии

- **Разработка под Android:** Java/Kotlin  
- **Интеграция карт:** Yandex Map SDK  
- **Геолокация:** Google Geolocation API  
- **Взаимодействие с бекендом:** RESTful API для управления историей маршрутов  
- **Авторизация:** Моковая реализация (для демонстрационных целей)

#### Установка

1. **Клонирование репозитория:**  
   ```bash
   git clone https://github.com/your-repo
### Открытие в Android Studio

1. **Импортируйте проект в Android Studio.**  
   Откройте Android Studio и выберите **"Open an Existing Project"**, затем укажите путь к клонированному репозиторию.

### Настройка API ключей

1. **Получите и настройте API ключ для Yandex Map SDK.**  
   Зарегистрируйтесь на [Яндекс.Картах для разработчиков](https://developer.tech.yandex.ru/) и получите API ключ.  
   Добавьте его в `local.properties` или другой конфигурационный файл, используемый в проекте.

2. **Настройте API ключ для Google Geolocation.**  
   Перейдите в [Google Cloud Console](https://console.cloud.google.com/) и получите API ключ для геолокации.  
   Добавьте ключ в нужные файлы конфигурации.

3. **Внесите изменения в соответствующие конфигурационные файлы.**  
   Убедитесь, что API ключи правильно указаны в `AndroidManifest.xml` или `gradle.properties`.

### Сборка и запуск

1. **Соберите проект.**  
   В Android Studio выполните **"Build > Make Project"** или используйте команду в терминале:
   ```bash
   ./gradlew assembleDebug