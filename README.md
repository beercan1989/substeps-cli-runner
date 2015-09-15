# Substeps Command Line Runner
Project to provide useful features and make it easier to get started with Substeps.
+ Substeps Site: https://substeps.g2g3.digital
+ GitHub Account: https://github.com/G2G3Digital

## Possible up coming features in 0.0.2
+ A single common property file for both configuring Substeps and WebDriver.
+ Means of overriding any property per "environment".
+ Better comments / explanations of what the properties do.
+ JUnit XML report.
+ Glossary Builder, provides a HTML report of all the known step implementations, including any included libraries.
+ RestDriver Substeps, provides the ability to use Substeps to test rest api's.
+ Testing on Linux and Windows.
+ Testing on Mac's, this is less likely due to availability of a Mac to test on.

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
