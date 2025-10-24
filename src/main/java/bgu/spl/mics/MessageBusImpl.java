package bgu.spl.mics;
import bgu.spl.mics.application.messages.events.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	private ConcurrentHashMap<Class<? extends Message>, BlockingQueue<MicroService>> messageSubscribers;
	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> services;
	private ConcurrentHashMap<Event<?>, Future<?>> futureMap;
	private List <Class<? extends Message>> messages = Arrays.asList(TickBroadcast.class, CrashedBroadcast.class, TerminatedBroadcast.class, DetectObjectsEvent.class, TrackedObjectsEvent.class, PoseEvent.class, LastFrameEvent.class);


    private MessageBusImpl() {
		messageSubscribers = new ConcurrentHashMap<>();
		for (Class<? extends Message> messageClass: messages){
			messageSubscribers.put(messageClass, new LinkedBlockingQueue<MicroService>());
		}
		services = new ConcurrentHashMap<>();
		futureMap = new ConcurrentHashMap<>();
	}

	private static class MessageBusImplHolder {
		private static final MessageBusImpl instance = new MessageBusImpl();
		}

	public static MessageBusImpl getInstance() {
			return MessageBusImplHolder.instance;
		}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		messageSubscribers.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		messageSubscribers.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> future = (Future<T>) futureMap.get(e);
		future.resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
	BlockingQueue <MicroService> servicesToSend = messageSubscribers.get(b.getClass());
	synchronized (servicesToSend) {
		for (MicroService service : servicesToSend) {
			services.get(service).add(b);
			}
		servicesToSend.notifyAll();
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
			BlockingQueue<MicroService> eventServices = messageSubscribers.get(e.getClass());
			Future<T> future = new Future<>();
			synchronized (eventServices) {
				if (eventServices.isEmpty()) {
					return null;
				}
				futureMap.put(e, future);
				MicroService s = eventServices.poll();

				services.get(s).add(e);
				eventServices.add(s);
				eventServices.notifyAll();
			}
		return future;
	}

	@Override
	public void register(MicroService m) {
		services.put(m, new LinkedBlockingQueue<Message>());
	}

	@Override
	public void unregister(MicroService m) {
		for (BlockingQueue<MicroService> servicesSubscribers : messageSubscribers.values()) {
			synchronized (servicesSubscribers) {
				servicesSubscribers.remove(m);
				servicesSubscribers.notifyAll();
			}
		}
			services.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		try {
			Message m1 = services.get(m).take();
			return m1;
		} catch (InterruptedException e) {
			throw new InterruptedException();
			}
	}

	// TEST GETTERS
	public ConcurrentHashMap<MicroService, BlockingQueue<Message>> getServices() {
		return services;
	}
	public ConcurrentHashMap<Event<?>, Future<?>> getFutureMap() {
		return futureMap;
	}
}
