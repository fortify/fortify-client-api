#!/bin/bash

projectName=fortify-client-api
scanOpts="-scan-module -Dcom.fortify.sca.UseSynchronousSerialization=true -Dcom.fortify.sca.ThreadCount=1 -Dcom.fortify.sca.DefaultAnalyzers=dataflow"

set -x

# Remove any old FPR file
rm -f ${projectName}.fpr

# Clean our build model
sourceanalyzer -b ${projectName} -clean

# Translate using SCA Gradle integration
# The -Pfortify option informs our build script that 
# we're running a Fortify translation, allowing the
# build script to take appropriate measures if necessary
sourceanalyzer -b ${projectName} gradle clean build -Pfortify

# Scan the project as a module (SCA 19.2+ feature)
sourceanalyzer -b ${projectName} -f ${projectName}.fpr ${scanOpts}
