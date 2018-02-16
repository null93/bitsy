# Bitsy
> Lightweight clipboard manager for MacOS

![MIT License](https://img.shields.io/badge/License-MIT-red.svg?style=for-the-badge)
![Version 1.0.0](https://img.shields.io/badge/Version-1.0.0-red.svg?style=for-the-badge)
![Stability Stable](https://img.shields.io/badge/Stability-Stable-red.svg?style=for-the-badge)

<p align="center" >
	<img src="assets/images/animation_1.gif" width="33%" />
	<img src="assets/images/animation_2.gif" width="33%" />
	<img src="assets/images/animation_3.gif" width="33%" />
</p>

## Building & Running

This project uses Maven as it's build system. If you would like to build and run the application from source, then the following commands will prove useful. Please note that the application version is replaced with **x.x.x** for the sake of maintainability of this _README_ document.

##### Package JAR
This will produce a _jar_ file in the _target_ directory.
```
mvn package
```

##### Bundle APP
This will bundle the project _jar_ into an app that can be found here: _target/bitsy-x.x.x/bitsy.app_
```
mvn package appbundle:bundle
```

##### Run JAR
This will run the produced _jar_ file, please note that you can also run the _app_ by simply clicking it.
```
java -cp target/bitsy-x.x.x.jar io.raffi.bitsy.Application
```
