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
    Core SLAM data models — Camera, LiDarDataBase, FusionSlam, Pose, LandMark, DetectedObject, and related containers.  
  * **Services:**  
    Microservices that execute SLAM tasks — CameraService, LiDarService, PoseService, FusionSlamService, and TimeService.

* **Testing (`src/test/java`)**
  * **Purpose:**  
    Unit tests verifying message bus functionality and SLAM data fusion logic.  
  * **Tests:**  
    MessageBusImplTest, CameraTest, FusionSlamTest.

