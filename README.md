# Kidney Stone Risk Detection App
![covid-kidneys](https://github.com/user-attachments/assets/2bb7e954-b822-45e1-a8e7-833b47832e24)

## Overview
This project aims to develop a mobile application for analyzing the contents of supermarket products to identify minerals or components that may increase the risk of kidney stones. Users can scan product barcodes using a barcode reader to determine the risk level.

## Key Features
- **Barcode Scanning**: Allows users to scan product barcodes using the camera.
- **OpenFoodFacts API Integration**: Retrieves product content information using barcode data.
- **Kidney Stone Risk Analysis**: Identifies components in the product that could increase kidney stone risk.
- **User-Friendly Interface**: Provides a clean and modern design for optimal user experience.
- **Information Screen**: Displays results after scanning a barcode while keeping the camera running in the background for further scans.

## Technologies Used
### Mobile Application
- **Programming Language**: Kotlin
- **Development Environment**: Android Studio
- **Libraries**:
  - **ZXing**: For barcode scanning.
  - **Retrofit**: For making requests to the OpenFoodFacts API.
  - **Gson**: For JSON data parsing.
  - **Glide**: For loading product images from the API.
  - **ViewModel and LiveData**: For MVVM architecture support.
  - **Room**: For local database (optional, to store previously scanned product information).

### API
- **OpenFoodFacts**: Provides data on product barcodes and contents.
  - [OpenFoodFacts API Documentation](https://world.openfoodfacts.org/data)

## Application Architecture
### Layered Architecture
- **Data**: Handles data retrieval from the API and local database.
  - `ApiService.kt`: Communicates with the OpenFoodFacts API.
  - `ProductRepository.kt`: Abstracts data sources.
- **Domain**: Business logic and use cases.
  - Example: `AnalyzeProductUseCase.kt`
- **UI**: Contains screens and components presented to the user.
  - Barcode scanning screen
  - Result information screen

### Key Considerations
1. **User Data Privacy**:
   - No user data is stored on servers.
   - All analyses are performed on the user's device.

2. **Minimal Dependencies**:
   - No Firebase or complex/paid infrastructure is used, relying solely on APIs and local solutions.

3. **Performance**:
   - Ensures smooth app performance even when the camera is active.
   - Avoids unnecessary API calls.

4. **Error Handling**:
   - Provides accurate error messages for API failures.
   - Displays user-friendly notifications when a barcode cannot be recognized.

5. **Accessibility**:
   - Designed to adhere to Android accessibility standards.

## Installation
### Requirements
- Android Studio Arctic Fox or later
- Minimum Android SDK 23 (Marshmallow)

### Installation Steps
1. Clone the project:
   ```bash
   git clone https://github.com/baranselklnc/KidneyStone.git
   ```

2. Open the project in Android Studio.

3. Add the required API keys to the `local.properties` file:
   ```
   OPENAI_API_KEY=your_openai_api_key_here
   ```

4. Build and run the application.

## Usage
1. Launch the application.
2. Accept the camera permission request.
3. Scan a barcode.
4. View the risk analysis result screen.
5. Tap the information screen to scan a new barcode.

## Roadmap
- **Planned Features**:
  - Product history and favorites.
  - Personalized dietary recommendations for users.
  - Multi-language support.

## Contributing
To contribute, please create a **pull request**. You can ask questions under the [issues](https://github.com/baranselklnc/KidneyStone/issues) section.

## License
This project is licensed under the [MIT License](LICENSE).

