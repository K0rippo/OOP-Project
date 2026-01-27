OOP Project

### Core Managers
* **GameMaster:** The central singleton that coordinates all other managers.
* **EntityManager:** Handles the lifecycle (creation, updates, removal) of all game entities.
* **SceneManager:** Manages different game states (screens) and transitions.
* **CollisionManager:** Detects and resolves overlaps between colliders.
* **MovementManager:** Calculates physics and position updates for entities.
* **IOManager:** Handles user input (keyboard/mouse) and audio playback.

### Game Objects
* **Entity:** The base class for all objects in the game world.
* **Scene:** Represents a specific level or screen (e.g., Menu, GameLevel).
* **Collider:** Defines the physical bounds for collision detection.
* **InputAction:** Enumeration defining all possible player actions (Jump, Move, etc.).
