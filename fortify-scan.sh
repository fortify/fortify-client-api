#!/bin/bash

# Set scan options
# In this case, we scan the project as a module (SCA 19.2+ feature),
# and provide some extra low-level scan settings
scanOpts="-scan-module"

# Load and execute actual scan script from GitHub
curl -s https://raw.githubusercontent.com/fortify-ps/gradle-helpers/1.0/fortify-scan.sh | bash -s - ${scanOpts}
