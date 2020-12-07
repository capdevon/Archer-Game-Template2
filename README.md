# Archer-Game-Template 2
A Third Person Shooter demo made with jMonkeyEngine v3.3.2-stable.

The demo contains:

* Physics with [Minie](https://stephengold.github.io/Minie/minie/overview.html)
* Arrows are now physical objects affected by gravity with trajectory prediction
* There are 3 types of arrows:
  - normal (The normal arrows stick to surfaces)
  - explosive
  - acid
* Animations (with gltf2 animations, file .blend included): 
    * "Idle", "Running", "Running_2", "Aim_Idle", "Aim_Overdraw", "Aim_Recoil", "Draw_Arrow", "Water_Idle", "Water_Moving", "Swimming"
* Third Person Camera with collision detection
* Dynamic update of camera FOV when aiming
* Keyboard/Mouse and Joystick support

# Keyboard Commands:
(see file [GInputAppState](https://github.com/capdevon/Archer-Game-Template2/blob/main/src/main/java/com/capdevon/input/GInputAppState.java) for all configurations, joystick included)
- WASD: Basic movements
- E: Aiming
- R: Switch arrow type
- LMB: Left Mouse Button to fire
- SPACE: Hold down the key while moving to sprinting
- CAMERA: Use the mouse to orient the camera
- 0 (zero): toggle Physics Debug

# Resource Used:

- Code
    - [jMonkeyEngine](https://jmonkeyengine.org/)
    - [Minie](https://stephengold.github.io/Minie/minie/overview.html)
    
- Assets
    - [Mixamo](https://www.mixamo.com/)
    - [Blender](https://www.blender.org/download/)
