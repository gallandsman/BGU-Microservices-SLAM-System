import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CameraTest {
    static Camera camera = new Camera(1, 2, "camera1");


    public static void setTest(){
        camera.initializeDetectedObjects(new ArrayList<>());
        camera.setTimeIndex();
        DetectedObject [] time1 = {new DetectedObject("table_1", "table"), new DetectedObject("table_2", "table") };
        DetectedObject [] time2 = {new DetectedObject("tv_1", "tv")};
        DetectedObject [] time4 = {};
        DetectedObject [] time5 = {new DetectedObject("desk_1", "desk")};

        StampedDetectedObjects objects1 = new StampedDetectedObjects(1, time1);
        StampedDetectedObjects objects2 = new StampedDetectedObjects(2, time2);
        StampedDetectedObjects objects4 = new StampedDetectedObjects(4, time4);
        StampedDetectedObjects objects5 = new StampedDetectedObjects(5, time5);

        camera.getDetectedObjects().add(objects1);
        camera.getDetectedObjects().add(objects2);
        camera.getDetectedObjects().add(objects4);
        camera.getDetectedObjects().add(objects5);
    }
    /**
     * sends a list of detected objects that the camera has detected at time t (if the frequency has passed). detects an a error if exists
     * @ pre: no conditions
     * @ post: The camera detects the required objects at the time + frequency and will create stamp detected object list
               The camera will not create a stamp detected object list if it has no objects to detect in the time + frequency
               The camera will process the last block of Detected objects. Forward them as stamp Detected objects list and change its status to down.
     */
    @Test
    public void testSendingDetectedObjects(){
        setTest();
        camera.handleTick(1);
        camera.handleTick(2);
        StampedDetectedObjects o1 = camera.handleTick(3);
        StampedDetectedObjects o2 = camera.handleTick(4);

        assertEquals(2, o1.getDetectedObjects().length); // check amount of objects
        assertEquals("table_2", (o1.getDetectedObjects())[1].getId()); // check if correct object
        assertEquals((o2.getDetectedObjects())[0].getId(), (camera.getLastFrame().getDetectedObjects()[0].getId())); // check update of lastframe
    }

    @Test
    public void testNoDetectedObjects(){
        setTest();
        StampedDetectedObjects o1 = camera.handleTick(1);
        for (int i=2; i<6;i++)
            camera.handleTick(i);
        StampedDetectedObjects o4= camera.handleTick(6);

        assertNull(o1);
        assertNull(o4);
    }

    @Test
    public void testFinishReading(){
        setTest();
        for (int i=1; i<7;i++)
            camera.handleTick(i);
        StampedDetectedObjects o5= camera.handleTick(7);

        assertSame(STATUS.DOWN, camera.getStatus()); // check camera status
        assertEquals(1,o5.getDetectedObjects().length); // check that last frame is update correctly
        assertEquals("desk_1", o5.getDetectedObjects()[0].getId()); // check that last frame is update correctly
    }

    /**
     * the method checks if there is an ERROR in the camera at time t
     * @ pre: no conditions
     * @ post:The camera will detect the error if exists at the current time (without the frequency)
     * returns true and changes the camera's status to error
     */
    @Test
    public void testIfError1(){
        camera.initializeDetectedObjects(new ArrayList<>());
        camera.setTimeIndex();
        DetectedObject [] time1 = {new DetectedObject("tv_1", "tv"), new DetectedObject("ERROR", "camera broken") };
        DetectedObject [] time2 = {};
        StampedDetectedObjects objects1 = new StampedDetectedObjects(1, time1);
        StampedDetectedObjects objects2 = new StampedDetectedObjects(2, time2);
        camera.getDetectedObjects().add(objects1);
        camera.getDetectedObjects().add(objects2);

        assertTrue(camera.checkIfError(1));
        assertEquals("camera broken", camera.getErrorDescription());
    }
}

