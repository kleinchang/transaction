### Prerequisite ###

This project was compiled with Android Gradle plugin 3.0.0, which requires Gradle version 4.1 or higher, and Android Studio 3.0 as minimum.


### Testing ###

By setting different build variants, we can choose mock (MockTransactionData) or prod (NetworkTransactionData) as data source.
Espresso test is under app/src/androidTestProd/ or app/src/androidMock/, depending on the data sources we choose. 
This project is architected using MVVM pattern. Testing of ViewModel and Data (Repository) are under app/src/test/.


### Who do I talk to? ###

Please also feel free to contact kleinchang@gmail.com for technical discussion. Would be more than happy to help clarify any technical issue such as project compilation, functionality, testing, etc.