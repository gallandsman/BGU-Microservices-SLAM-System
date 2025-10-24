package bgu.spl.mics.application.messages.events;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;
import java.util.List;

public class TrackedObjectsEvent implements Event<Boolean> {
   private List<TrackedObject> trackedObjects;

   public TrackedObjectsEvent(List<TrackedObject> trackedObjects) {
       this.trackedObjects = trackedObjects;
   }

   public List<TrackedObject> getTrackedObjects() {
       return trackedObjects;
   }

   public String toString(){
       return "TrackedObjectsEvent";
   }
}
