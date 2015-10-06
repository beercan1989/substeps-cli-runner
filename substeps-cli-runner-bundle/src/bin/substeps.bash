#!/usr/bin/env bash

# Enable running script from any location
cd "$( dirname "${BASH_SOURCE[0]}" )"

PATH="$(pwd)/drivers:${PATH}"

# Just run it now, adding properties to the classpath
java "${@}" -classpath "properties/:lib/*" "uk.co.baconi.substeps.cli.SubstepsCommandLineRunner"
