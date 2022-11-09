# Archer-Game-Template 2
A Third Person Shooter demo made with [jMonkeyEngine](https://jmonkeyengine.org/)

The demo contains:

* Physics with [Minie](https://stephengold.github.io/Minie/minie/overview.html)
* Arrows are now physical objects affected by gravity with trajectory prediction
* There are 3 types of arrows:
  - normal (The normal arrows stick to surfaces)
  - explosive
  - acid
* A materialize shader that simulates "materializing" an object in or out.
* Third Person Camera with collision detection
* Dynamic update of camera FOV when aiming
* Vertical rotation of the spine when aiming
* Keyboard/Mouse support
* Joystick support
* Sounds
* Particles Effects
* Post Processing Filters

# Youtube videos
[Demo](https://www.youtube.com/watch?v=US9KNTqL2js&feature=emb_logo)

# Keyboard Commands
(see file [GInputAppState](https://github.com/capdevon/Archer-Game-Template2/blob/main/src/main/java/com/capdevon/input/GInputAppState.java) for all configurations, joystick included)
- WASD: Basic movements
- E: Aiming
- R: Switch arrow type
- LMB: Left Mouse Button to fire
- LSHIFT: Running
- CAMERA: Use the mouse to orient the camera
- KEY_0: Toggle Physics Debug

# Resource Used
- [Mixamo](https://www.mixamo.com/)
- [Blender](https://www.blender.org/download/)

# 
![Screenshot](images/archer-1.jpg)
![Screenshot](images/archer-2.jpg)

# Credits
I acknowledge the following artists and software developers:

* "grizeldi" for creating the 3d scene, bow and arrow models and for improving the lighting effects and graphics of the demo. 
* "stephengold" for his excellent work on updating and maintaining the "[Minie](https://github.com/stephengold/Minie)" physics library and for his useful advice.
* "polincdev" for creating the "[ShaderBlowEx](https://github.com/polincdev/ShaderBlowEx)" library with additional scene filters.
* "jayfella" for creating the "[jme-materialize](https://github.com/capdevon/jme-materialize)" library.

## How to build and run the demo from source

1. Install a 64-bit [Java Development Kit (JDK)][adoptium],
   if you don't already have one.
2. Point the "JAVA_HOME" environment variable to your JDK installation.
   (The path might be something like "C:\Program Files\Java\jre1.8.0_301"
   or "/usr/lib/jvm/java-8-openjdk-amd64" or
   "/Library/Java/JavaVirtualMachines/liberica-jdk-17-full.jdk/Contents/Home" .)
  + using Bash or Zsh: `export JAVA_HOME="` *path to installation* `"`
  + using Windows Command Prompt: `set JAVA_HOME="` *path to installation* `"`
  + using PowerShell: `$env:JAVA_HOME = '` *path to installation* `'`
3. Download and extract the demo source code from GitHub:
  + `git clone https://github.com/capdevon/Archer-Game-Template2.git`
  + `cd Archer-Game-Template2`
4. Run the [Gradle] wrapper:
  + using Bash or PowerShell or Zsh: `./gradlew build`
  + using Windows Command Prompt: `.\gradlew build`

You can run the demo application:
+ using Bash or PowerShell or Zsh: `./gradlew run`
+ using Windows Command Prompt: `.\gradlew run`

You can restore the project to a pristine state:
+ using Bash or PowerShell or Zsh: `./gradlew clean`
+ using Windows Command Prompt: `.\gradlew clean`
