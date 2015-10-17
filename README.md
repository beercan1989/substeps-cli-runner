# Substeps Command Line Runner [![Build Status](https://travis-ci.org/beercan1989/substeps-cli-runner.svg?branch=master)](https://travis-ci.org/beercan1989/substeps-cli-runner)
Project to provide useful features and make it easier to get started with Substeps.
+ Substeps Site: https://substeps.g2g3.digital
+ GitHub Account: https://github.com/G2G3Digital

## Basic Requirements
+ An urge to test things using an automation tool
+ Java 8

## Possible up coming features in 0.0.3
+ PhantomJS integration via GhostDriver
+ Bug fixes.
+ Feature requests.
+ Testing on Mac's, this is less likely due to availability of a Mac to test on.

## Features in 0.0.2
+ A single common property file for both configuring Substeps and WebDriver.
    + application.conf is now the property file to rule them all, did a minor hack to alter how the webdriver properties are loaded.
+ Means of overriding any property per "environment".
    + Run the command with -Denvironment=qa to make it look for qa.conf first before application.conf
+ Better comments / explanations of what the properties do.
    + Lots of comments in the properties file to help explain what they do.
+ JUnit XML report.
    + Generates JUnit XML reports similar to maven surefire outputs. 
+ Glossary Builder.
    + Provides a HTML report of all the known step implementations, including any included libraries.
+ RestDriver Substeps.
    + Provides the ability to use Substeps to test rest api's.
    + Currently not released into Maven, but can be submoduled from GitHub.
+ Testing on Linux and Windows.
    + Basic testing on Windows 10 has been performed.
    + Extensive testing on Ubuntu has been performed.

## Features in 0.0.1
+ Linux based testing, so Mac and Windows code is currently untested, sorry.
+ Command line runner for Substeps.
+ Property file to configure Substeps.
+ Working example feature and substep files, with accompanying properties to facilitate.
+ Comments / explanations of what the properties do and their defaults.
+ HTML feature report generation.
+ Common logging with easy to control config file.
+ Bundled command line runner, which contains:
    + Required libraries to run Substeps
        + Substeps
        + Substeps Command Line Runner
        + WebDriver Substeps
    + Property files
    + OS architecture specific driver implementations
        + Chrome Driver for Linux, Windows, and Mac
        + IE Driver for Windows only
        + Firefox will work on all three, but you will be restricted to a certain version by the Selenium version pulled in by WebDriver Substeps.

## Building Project
```bash
# Checkout code
git clone https://github.com/beercan1989/substeps-cli-runner.git

# Setup the submodules, currently only substeps-restdriver 
git submodule init
git submodule update

# Build the project
mvn clean package
```
