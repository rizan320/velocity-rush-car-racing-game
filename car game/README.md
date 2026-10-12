# Velocity Rush — Java Racing Game

A complete, high-performance 2D arcade car racing game built from scratch using **Java Swing**, **Java2D**, and strict **Object-Oriented Programming (OOP)** principles.

Developed as a college Java mini project. Runs 100% locally on Windows without requiring paid APIs, external game engines, or internet connection.

---

## 🏎️ Key Features

- **12 Selectable Sports Cars**: Complete interactive 3D showroom turntable featuring Hypercars, V8 Muscle Coupes, and Luxury Grand Tourers (GT).
- **Flagship Apex Flame V10**: Upgraded red flagship hypercar with aggressive aerodynamic splitter, gold performance rims, and top speed specs.
- **3-Lane Daytime Highway**: High-fidelity asphalt road with tire-wear lanes, red-and-white rumble curbs, gravel shoulders, galvanized guardrails with amber cat-eye reflectors, and shaded roadside trees.
- **Auto-Forward Cruising & Ultra-Smooth Steering**: The car automatically cruises forward; the player uses calm, precision steering to weave between tight gaps.
- **Instant Blast Explosions**: Universal collision physics—hitting any vehicle or road barrier causes an instant fireball blast with expanding shockwaves, flying shrapnel, and screen shake!
- **Traffic Pack Congestion**: AI traffic vehicles follow safe distance rules to cruise in dense packs across all 3 lanes, creating intense highway congestion without destroying each other.
- **10-Second 2X Speed Boost**: Rare glowing cyan N2O canisters launch your car past 400+ km/h with supersonic warp streaks and double overtake bonuses.
- **Synthesized Audio Engine**: Real-time non-blocking synthesized explosion rumbles, rising speed whooshes, and UI button clicks using standard `javax.sound.sampled`.
- **Local Save/Load Persistence**: Saves High Score, Best Distance, Total Overtakes, and Credits locally in `saves/records.dat`.
- **Mission & Achievement System**: Real-time evaluation of distance, overtake, and score challenges.

---

## 🕹️ Controls

| Action | Primary Key | Alternate Key |
| :--- | :--- | :--- |
| **Steer Left** | **`<`** (Comma `,`) | **A** or **Left Arrow** |
| **Steer Right** | **`>`** (Period `.`) | **D** or **Right Arrow** |
| **Emergency Brake** | **S** | **Down Arrow** |
| **Nitro Thrust** | **Spacebar** | — |
| **Pause Game** | **Escape** | Click `[ || PAUSE ]` |
| **Restart Race** | **R** | Click `[ RESTART ]` |
| **Menu Navigation** | **Mouse Click** | Click `[ < ]`, `[ > ]`, or `[ NEW GAME ]` |

---

## 🚀 How to Run the Game

### Method 1: One-Click Desktop Launcher (No VS Code Needed)
Simply double-click **`run_game.bat`** in your project folder! It will automatically compile and launch the game on Windows.

### Method 2: Create a Standalone Executable JAR
1. Double-click **`package_jar.bat`** in your project folder.
2. It compiles the source files and creates **`VelocityRush.jar`**.
3. You can now double-click **`VelocityRush.jar`** anywhere on your PC to play!

### Method 3: In VS Code
1. Open the project folder in VS Code.
2. Open `src/com/velocityrush/Main.java`.
3. Click the clickable **`▶ Run`** button above `main()` or press **Ctrl + F5**.

---

## 🏛️ Object-Oriented Programming (OOP) Architecture

Velocity Rush is built strictly around genuine Object-Oriented design:

```
src/
└── com/
    └── velocityrush/
        ├── Main.java                 # Entry point (EDT thread-safe bootstrap)
        ├── assets/                   # AssetLoader & ProceduralCarRenderer
        ├── audio/                    # SoundManager (javax.sound.sampled synthesizer)
        ├── core/                     # GameEngine, GamePanel, GameWorld, GameState
        ├── entities/                 # Vehicle (abstract), PlayerCar, OpponentCar, RoadObstacle, Collectible, Explosion, Particle, Track
        ├── garage/                   # CarProfile & GarageManager
        ├── input/                    # InputHandler (KeyListener with edge detection)
        ├── missions/                 # Mission (abstract), DistanceMission, OvertakeMission, ScoreMission, MissionManager
        ├── storage/                  # SaveManager (File I/O with Exception Handling)
        └── ui/                       # UIButton (interactive clickable component)
```

### OOP Concept Mapping Table (For College Viva)

| OOP Concept | Implementing Classes | Contribution to Game Architecture |
| :--- | :--- | :--- |
| **Classes & Objects** | `PlayerCar`, `OpponentCar`, `Track`, `GarageManager` | Models real-world entities with encapsulated state and behaviors. |
| **Encapsulation** | `Vehicle.java`, `CarProfile.java`, `SaveManager.java` | Keeps critical fields private (`speed`, `health`, `credits`, `boostTimer`). Enforces validations (e.g. speed clamping, non-negative credits). |
| **Abstraction** | `Vehicle` (abstract class), `Mission` (abstract class) | Defines common contracts (`update()`, `render()`, `evaluate()`) without exposing internal physics or AI algorithms. |
| **Inheritance** | `PlayerCar extends Vehicle`, `OpponentCar extends Vehicle`, `DistanceMission extends Mission` | Reuses spatial coordinates, collision bounds, and rendering logic while specializing player input vs. AI pack driving. |
| **Polymorphism** | `Vehicle.update()`, `Vehicle.triggerBlast()`, `Mission.evaluate()` | `GameWorld` manages lists of vehicles and missions uniformly. Player handles keyboard input while Opponents execute traffic following AI through the same method signature. |
| **Composition** | `GameWorld` has-a `Track`, `PlayerCar`, `List<OpponentCar>`, `List<Explosion>`, `SoundManager` | Builds complex worlds by assembling modular, decoupled components. |
| **Collections** | `ArrayList<OpponentCar>`, `ArrayList<Explosion>`, `ArrayList<Mission>` | Manages dynamic traffic packs, active fireball shockwaves, and mission objectives at runtime. |
| **Exception Handling** | `SaveManager.java`, `AssetLoader.java` | Uses try-with-resources and safe fallback handling to prevent game crashes if save files or images are missing or corrupt. |

---

## 🎓 Student Viva Questions & Challenges

### 1. Encapsulation Challenge
* **Question**: How is encapsulation applied to vehicle speed?
* **Answer**: `Vehicle.java` makes `speed` private. The mutator `setSpeed(double s)` validates and clamps the value between `0.0` and `maxSpeed`, preventing negative speeds or infinite acceleration.

### 2. Polymorphism Challenge
* **Question**: How do `PlayerCar` and `OpponentCar` demonstrate runtime polymorphism?
* **Answer**: Both inherit from abstract `Vehicle`. In `GameWorld.java`, entities are treated as `Vehicle`. When `update()` or `triggerBlast()` is called, Java dynamically invokes the overridden subclass behavior (Player reads keyboard input; Opponent executes traffic-following AI).

### 3. File Handling & Exception Handling Challenge
* **Question**: What happens if `saves/records.dat` is deleted or corrupted?
* **Answer**: `SaveManager.java` uses `try-with-resources` with `BufferedReader`. If the file is missing or contains invalid text, the `IOException` and `NumberFormatException` are caught gracefully, and the game automatically initializes default records without crashing.

---

## 📜 Credits & License
- Developed as an educational Java Swing project.
- Top-down vehicle art templates sliced from user-provided arcade sprites.
- Built with Java SE standard libraries.
