# CRIF High Marks SDK - Customer Details Retrieval

The CRIF High Marks SDK is a powerful tool that allows Android developers to retrieve customer details in the background for verification purposes. This SDK enables seamless integration with the CRIF High Marks API, providing access to essential customer information to ensure accurate and reliable verification processes.

## Features

- **Background Customer Details Retrieval**: The SDK facilitates the retrieval of customer details in the background without interrupting the user experience.
- **Integration with CRIF High Marks API**: The SDK seamlessly integrates with the CRIF High Marks API, providing access to a comprehensive set of customer data.
- **Secure and Efficient**: The SDK ensures the secure transmission of customer data and optimizes the retrieval process for efficiency.
- **Customizable**: The SDK offers flexibility to tailor the customer details retrieval process to specific application requirements.
- **Error Handling**: The SDK provides robust error handling mechanisms to handle exceptions and maintain application stability.
- **Detailed Documentation**: The SDK comes with detailed documentation, including installation instructions, usage guidelines, and code examples.

## Requirements

- Android API Level XX and above
- CRIF High Marks API credentials (API key, secret key, etc.)
- Internet permission in the Android manifest file

## Installation

To install the CRIF High Marks SDK in your Android application, follow these steps:

1. Download the SDK package.
2. Extract the SDK package into your project directory.
3. Open your Android project in Android Studio.
4. In the Project view, navigate to the directory where you extracted the SDK package.
5. Right-click on your application module and select "New Module".
6. Select "Import Gradle Project" and follow the on-screen instructions to import the SDK module.
7. Add the SDK module as a dependency in your app's `build.gradle` file:

   ```groovy
   implementation project(':crifhighmarkssdk')
8.Sync your project with Gradle files.

## Usage
To retrieve customer details using the CRIF High Marks SDK, follow these steps:

Initialize the SDK with your CRIF High Marks API credentials:

CRIFHighMarksSDK.initialize(context, apiKey, secretKey);

Use the provided methods to retrieve customer details based on your verification requirements:

CRIFHighMarksSDK.retrieveCustomerDetails(customerId, new CustomerDetailsCallback() {
    @Override
    public void onSuccess(CustomerDetails customerDetails) {
        // Handle successful retrieval of customer details
    }

    @Override
    public void onFailure(String error) {
        // Handle retrieval failure and display appropriate error message
    }
});

Handle the retrieved customer details in the onSuccess callback method and any errors in the onFailure callback method.

For detailed usage instructions, please refer to the SDK Documentation.

## License
The CRIF High Marks SDK is released under the MIT License.

Please note that in the code snippet, replace `XX` with the appropriate Android API level required for the SDK, and ensure that the paths to the SDK module and documentation files are accurate.
