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
2. Copy the generated JAR (KeepLostItems-1.0.0.jar) into your server’s ```plugins/``` folder.
3. Restart your server.

Commands
   Command	Description
      /keeplostitems toggle [on|off]	Enable or disable the plugin
      /keeplostitems lang <lang>	Change language (English/Korean/Japanese)

Configuration
   Defaults are in config.yml. You can toggle the plugin or change language in-game; no manual config edits required.

Releases
   Download JARs from the Releases page.

Contributing
1. Fork this repository

2. Create a branch (git checkout -b my-feature)

3. Commit your changes (git commit -m "Add feature")

4. Push to your fork (git push origin my-feature)

5. Open a Pull Request

License
   This project is licensed under the MIT License. See LICENSE for details.
