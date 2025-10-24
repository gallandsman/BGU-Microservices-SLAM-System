import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.services.TimeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.events.*;
import static org.junit.jupiter.api.Assertions.*;


public class MessageBuslmplTest {
    private MessageBusImpl messageBus;
    private MicroService microService1;
    private MicroService microService2;
    private MicroService microService3;
        @BeforeEach
        public void setUp() {
            messageBus = MessageBusImpl.getInstance();
            microService1 = new MicroService("MicroService1") {
                @Override
                protected void initialize() {}
            };
            microService2 = new MicroService("MicroService2") {
                @Override
                protected void initialize() {}
            };
            microService3 = new MicroService("MicroService3") {
                @Override
                protected void initialize() {}
            };
        }


        public void register(){
            messageBus.register(microService1);
            messageBus.register(microService2);
            messageBus.register(microService3);
        }
        public void unregister() {
        messageBus.unregister(microService1);
        messageBus.unregister(microService2);
        messageBus.unregister(microService3);
    }

    /**
     * send broadcast should add broadcast to all services subscribed queue
     * @ pre: broadcast's class is in the messages map
     * @ post: broadcast was sent to al subscribers queue and only theirs
     */

    @Test
    public void testSendBroadcast() {
        register();
            TickBroadcast broadcast = new TickBroadcast( 1);
            messageBus.subscribeBroadcast(TickBroadcast.class, microService1);
            messageBus.subscribeBroadcast(TickBroadcast.class, microService2);
            messageBus.sendBroadcast(broadcast);
            assertTrue(messageBus.getServices().get(microService1).contains(broadcast));
            assertTrue(messageBus.getServices().get(microService2).contains(broadcast));
            assertFalse(messageBus.getServices().get(microService3).contains(broadcast));

            //reset
        messageBus.getServices().get(microService1).poll();
        messageBus.getServices().get(microService1).poll();
            unregister();

        }

    /**
     * send event should add the event to the queue of thק first service in the subscribed queue
     * @ pre: event's class is in the messages map
     * @ post: event was sent to one microservice(the first) queue and only theirs
     * a future is returned. the future is saved in the futures map with this event.
     */

        @Test
        public void testSendEvent() {
            register();
            Event<Boolean> event = new DetectObjectsEvent(new StampedDetectedObjects());
            messageBus.subscribeEvent(DetectObjectsEvent.class, microService1);
            messageBus.subscribeEvent(DetectObjectsEvent.class, microService2);
            Future<Boolean> future = messageBus.sendEvent(event);
            assertNotNull(future);
            assertTrue(messageBus.getServices().get(microService1).contains(event));
            assertFalse(messageBus.getServices().get(microService2).contains(event));
            assertNotNull(messageBus.getFutureMap().get(event));
            assertEquals(future, messageBus.getFutureMap().get(event));

            // clear
            messageBus.getServices().get(microService1).poll();
            messageBus.getFutureMap().remove(event);
            unregister();

        }

    /**
     * register should add the microservice queue from to the services mao
     * @ pre: microservice is not in the map
     * @ post: services map size increases by one
     * microservice is now a key in the map with an empty queue
     */
        @Test
        public void testRegister() {
            MicroService newService = new TimeService(1,1);
            int sizeBeforeRegister= messageBus.getServices().size();
            assertDoesNotThrow(() -> messageBus.register(newService));
            assertEquals(sizeBeforeRegister+1, messageBus.getServices().size());
            assertNotNull(messageBus.getServices().get(newService));
            assertEquals(0, messageBus.getServices().get(newService).size());

        }
    }

