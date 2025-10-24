import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.objects.TrackedObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FusionSlamTest {
    private static FusionSlam fusionSlam;

    public  void before() {
        fusionSlam = FusionSlam.getInstance();
        fusionSlam.setStatus(STATUS.UP);
    }

    public void tearDown() {
        fusionSlam.resetTests();

    }

    /**
     * The handle tick method in fusion slam should create landmarks from pose data and tracked objects in correlated times
     * @ pre: none
     * @ post: all tracked objects in tracked object list with time t which already is in the pose data are transformed to landmarks
     * if it's the first time the object is tracked landmark's size will increase by one (for each object as this)
     * the object will be in the landmarks with accurate coordinates
     * if the object was already in the landmarks the coordinates will change according to the new tracking data.
     */

    @Test
    public void testTransformTrackedObjectsToLandmarks() {
        // test calculations
       before();

        CloudPoint[] cloudPoints= new CloudPoint[]{
                new CloudPoint(1.0, 2.0),
                new CloudPoint(-1.2, 0.0),
                new CloudPoint(7.8, -4.3)
        };
        List<TrackedObject> trackedObjects = new ArrayList<>();
        trackedObjects.add( new TrackedObject("object1", 1, "description", cloudPoints));
        Pose p= new Pose(1, (float) 3.05, (float) 0.0755, (float)-92);
        fusionSlam.handleNewPose(p);
        fusionSlam.handleTrackedObjects(trackedObjects);
        fusionSlam.handleTick();
        List<LandMark> landmarks = fusionSlam.getLandMarks();


        assertNotNull(landmarks);
        assertEquals(1, landmarks.size());
        assertEquals("object1", landmarks.get(0).getId());
        assertEquals(3, landmarks.get(0).getCoordinates().size());
        assertEquals(5.013882109651975  , landmarks.get(0).getCoordinates().get(0).getX());
        assertEquals( -0.9936898238811671 , landmarks.get(0).getCoordinates().get(0).getY());
        assertEquals(3.0918793483592855 , landmarks.get(0).getCoordinates().get(1).getX());
        assertEquals(1.2747689889658456  , landmarks.get(0).getCoordinates().get(1).getY());
        assertEquals(-1.5195966781453345  , landmarks.get(0).getCoordinates().get(2).getX());
        assertEquals(-7.569680618385262 , landmarks.get(0).getCoordinates().get(2).getY());

        tearDown();

    }
    @ Test
    public void testPosesCoordinateObjects() {
        before();
        CloudPoint[] cloudPoints1= new CloudPoint[]{
                new CloudPoint(1.0, 2.0),
                new CloudPoint(-1.2, 0.0)
        };
        CloudPoint[] cloudPoints2= new CloudPoint[]{
                new CloudPoint (4.0, 6.0)
        };
        List<TrackedObject> trackedObjects = new ArrayList<>();
        trackedObjects.add( new TrackedObject("object1", 1, "description", cloudPoints1));
        trackedObjects.add(new TrackedObject("object2", 3,"description2", cloudPoints2));
        fusionSlam.handleNewPose(new Pose(1, (float) 1, (float) 1, (float)-1));
        fusionSlam.handleNewPose(new Pose(2, (float) 2, (float) 2, (float)-2));
        fusionSlam.handleNewPose(new Pose(3, (float) 3, (float) 3, (float)-3));
        fusionSlam.handleTrackedObjects(trackedObjects);
        fusionSlam.handleTick();
        List<LandMark> landmarks = fusionSlam.getLandMarks();


        assertNotNull(landmarks);
        assertEquals(2, landmarks.size());
        assertEquals("object1", landmarks.get(0).getId());
        assertEquals("object1", landmarks.get(0).getId());
        assertEquals(2, landmarks.get(0).getCoordinates().size());
        assertEquals("object2", landmarks.get(1).getId());
        assertEquals(1, landmarks.get(1).getCoordinates().size());
        assertEquals(0, fusionSlam.getLastTrackedObjects().size());

        tearDown();

    }
    @Test
    public void testNoPoseObject(){
        before();
        CloudPoint[] cloudPoints1= new CloudPoint[]{
                new CloudPoint(1.0, 2.0)
        };
        List<TrackedObject> trackedObjects = new ArrayList<>();
        trackedObjects.add( new TrackedObject("object1", 4, "description", cloudPoints1));
        fusionSlam.handleNewPose(new Pose(1, (float) 1, (float) 1, (float)-1));
        fusionSlam.handleNewPose(new Pose(2, (float) 2, (float) 2, (float)-2));
        fusionSlam.handleNewPose(new Pose(3, (float) 3, (float) 3, (float)-3));
        fusionSlam.handleTrackedObjects(trackedObjects);
        fusionSlam.handleTick();
        List<LandMark> landmarks = fusionSlam.getLandMarks();


        assertNotNull(landmarks);
        assertEquals(0, landmarks.size());
         tearDown();
     }
    @Test
    public void testSameIdObjects(){
       before();
            List<CloudPoint> cloudPoints1= new ArrayList<>();
             cloudPoints1.add(new CloudPoint(1.0, 2.0));

        fusionSlam.addlandmark(new LandMark("object1","description", cloudPoints1));
        CloudPoint[] cloudPoints2= new CloudPoint[]{
                new CloudPoint(2.0, 2.0),
                new CloudPoint(1.0, 4.0)
        };
        List<TrackedObject> trackedObjects = new ArrayList<>();
        trackedObjects.add(new TrackedObject("object1", 1, "description", cloudPoints2));
        fusionSlam.handleNewPose(new Pose(1, (float) 1, (float) 1, (float)45));
        fusionSlam.handleTrackedObjects(trackedObjects);
        fusionSlam.handleTick();
        List<LandMark> landmarks = fusionSlam.getLandMarks();

        assertEquals(1, landmarks.size());
        assertEquals(2, landmarks.get(0).getCoordinates().size());
        assertEquals(1 , landmarks.get(0).getCoordinates().get(0).getX());
        assertEquals(2.914213562373095  , landmarks.get(0).getCoordinates().get(0).getY());
        assertEquals(-1.1213203435596424  , landmarks.get(0).getCoordinates().get(1).getX());
        assertEquals(4.535533905932738  , landmarks.get(0).getCoordinates().get(1).getY());
    tearDown();
    }

    }

