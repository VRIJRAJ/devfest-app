package de.devfest.data.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import de.devfest.data.EventManager;
import de.devfest.model.EventPart;
import rx.Observable;
import rx.Single;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;


public class FirebaseEventManager implements EventManager {

    private static final String FIREBASE_CHILD_DETAILS = "eventdetail";

    private final DatabaseReference reference;

    FirebaseEventManager(FirebaseDatabase database) {
        this.reference = database.getReference(FIREBASE_CHILD_DETAILS);
    }

    @Override
    public Observable<EventPart> getEventParts() {
        return Observable.create(new Observable.OnSubscribe<EventPart>() {
            @Override
            public void call(Subscriber<? super EventPart> subscriber) {
                ValueEventListener listener = new EventPartExtractor(subscriber, false);
                subscriber.add(Subscriptions.create(() -> reference.removeEventListener(listener)));
                reference.addValueEventListener(listener);
            }
        });
    }

    @Override
    public Single<String> getEventName() {
        throw new RuntimeException("not yet implemented");
    }

    private static class EventPartExtractor extends FirebaseExtractor<EventPart> {

        EventPartExtractor(Subscriber<? super EventPart> subscriber, boolean single) {
            super(subscriber, single);
        }

        @Override
        protected EventPart convert(DataSnapshot snapshot) {
            FirebaseEventPart part = snapshot.getValue(FirebaseEventPart.class);
            ZonedDateTime startTime = ZonedDateTime
                    .ofInstant(Instant.ofEpochSecond(part.startTime), ZoneId.of("UTC"));
            ZonedDateTime endTime = ZonedDateTime
                    .ofInstant(Instant.ofEpochSecond(part.endTime), ZoneId.of("UTC"));

            return EventPart.newBuilder()
                    .id(snapshot.getKey())
                    .name(part.name)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build();
        }
    }
}
