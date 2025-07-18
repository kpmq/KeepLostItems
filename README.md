# KeepLostItems

A PaperMC 1.21.8 Kotlin plugin that locks death-dropped items to their owners and preserves experience on death.

## Features

- Preserve experience (XP) on death  
- Lock dropped items so only the original player can pick them up  
- `/keeplostitems toggle [on|off]` to enable/disable  
- `/keeplostitems lang <English|Korean|Japanese>` to change language  
- Tab-complete support  

## Installation

1. Build the plugin JAR:  
   ```bash
   mvn clean package
