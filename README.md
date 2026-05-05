# BGU-Microservices-SLAM-System

## 📘 Overview
* Java-based Microservices framework for a vacuum robot's Perception and Mapping (SLAM) systemת built with a microservices architecture.
* Each service models a real-world SLAM component (camera, LiDAR, GPS/IMU, and fusion engine), communicating asynchronously through a custom MessageBus implementation.
* The project demonstrates advanced concepts in concurrency, event-driven design, and thread-safe communication between distributed components.

## 🧩 Main Components
* **Message Framework (`bgu.spl.mics`)**
  * **Classes:**  
    MessageBus, MessageBusImpl, Event, Broadcast, Future, Callback, Message, MicroService  
  * **Purpose:**  
    Core infrastructure handling message passing, subscription management, and asynchronous event delivery between microservices.

* **Application Layer (`bgu.spl.mics.application`)**
  * **Messages / Events:**  
    Domain events such as PoseEvent, DetectObjectsEvent, TrackedObjectsEvent, TickBroadcast, and others.  
  * **Objects:**  
    Core SLAM data models - Camera, LiDarDataBase, FusionSlam, Pose, LandMark, DetectedObject, and related containers.  
  * **Services:**  
    Microservices that execute SLAM tasks - CameraService, LiDarService, PoseService, FusionSlamService, and TimeService.

* **Testing (`src/test/java`)**
  * **Purpose:**  
    Unit tests verifying message bus functionality and SLAM data fusion logic.  
  * **Tests:**  
    MessageBusImplTest, CameraTest, FusionSlamTest.

 ## 🚀 How to Run
The system uses configuration files to initialize the simulation.
1. Open the project in **VS Code**.
2. Go to **Run and Debug** (Ctrl+Shift+D).
3. Choose an option from the dropdown:
   * **Run Example 1:** Standard successful simulation.
   * **Run Example 2:** Complex environment with more sensors.
   * **Run Example 3:** **Error Simulation** - simulates sensor/data conflicts to test system robustness.
4. Press **F5** to start.

## 📂 Input & Output
* **Input:** Located in `input/`. Each example folder contains specific sensor data (Camera, LiDAR, Pose).
* **Output:** A file named `output_file.json` is generated in the root directory, containing:
  * **System Statistics:** Runtime and message counts.
  * **SLAM Map:** Calculated global positions of all detected Landmarks.

