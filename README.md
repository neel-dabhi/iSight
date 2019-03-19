# iSight
App that helps people with low vision to understand their surrounding. 

## Objective
This project is built to aid the Visually Impaired People so that they may easily understand, what’s happening around them. Understanding of environment should be independent to sensation of touch and smell.

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites
What things you need to install the software and how to install them

```
Android Studio
Microsoft Azure Cognitive Services API Key
```

### Installing

A step by step series of Instructions that tell you how to get a development env running

```
0) Download this project as a zip
1) Open Android Studio
2) Click File > New > Import Project.
3) Select project directory, and click OK. Your project will open in Android Studio.
```
After that Wait until Gradle build is Finished.
Meanwhile, goto [Microsoft Azure portal](https://azure.microsoft.com/en-in/try/cognitive-services/) and grab your congnative service API Key and End points.

*After Gradle build is Finished*
```
Navigate to iSight>app>src>main>java>com>neelkanthjdabhi>isight and open file ImageDescription.java
Find variables API_KEY_ONE,API_ENDPOINT_TWO 
Replace the value of both the variable with your values.
```

### Run on a real device
```
1) Connect your device to your development machine with a USB cable.
2) Enable USB debugging in the Developer options
3) In Android Studio, click the app module in the Project window and then select Run > Run (or click Run in the toolbar).
4) In the Select Deployment Target window, select your device, and click OK.

Android Studio installs the app on your connected device and starts it.
```

## Built With

* [Microsoft Cognitive Services](https://azure.microsoft.com/en-in/try/cognitive-services/) - API that processes Image.
* [Text Recognition API](https://developers.google.com/vision/android/text-overview) - API that detect text from image.
