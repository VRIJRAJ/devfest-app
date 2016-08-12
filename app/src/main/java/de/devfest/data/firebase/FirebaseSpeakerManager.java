package de.devfest.data.firebase;

import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

import de.devfest.data.SpeakerManager;
import de.devfest.model.Speaker;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

public final class FirebaseSpeakerManager implements SpeakerManager {

    private static final String FIREBASE_CHILD_SPEAKER = "speaker";

    private final DatabaseReference reference;

    public FirebaseSpeakerManager() {
        this.reference = FirebaseDatabase.getInstance().getReference(FIREBASE_CHILD_SPEAKER);
    }

    @Override
    public Observable<Speaker> insertOrUpdate(Speaker speaker) {
        String speakerId = (speaker.speakerId == null) ? reference.push().getKey() : speaker.speakerId;
        FirebaseSpeaker firebaseSpeaker = new FirebaseSpeaker(speaker);
        reference.child(speakerId).setValue(firebaseSpeaker);
        return getSpeaker(speakerId);
    }

    @Override
    public Observable<Speaker> getSpeakers() {
        return Observable.create(new Observable.OnSubscribe<Speaker>() {
            @Override
            public void call(Subscriber<? super Speaker> subscriber) {
                ValueEventListener listener = new SpeakerExtractor(subscriber, false);
                subscriber.add(Subscriptions.create(() -> reference.removeEventListener(listener)));
                reference.addValueEventListener(listener);
            }
        });
    }

    @Override
    public Observable<Speaker> getSpeaker(String uid) {
        return Observable.create(new Observable.OnSubscribe<Speaker>() {
            @Override
            public void call(Subscriber<? super Speaker> subscriber) {
                reference.child(uid).addListenerForSingleValueEvent(new SpeakerExtractor(subscriber, true));
            }
        });
    }

    static final class SpeakerExtractor implements ValueEventListener {

        private final Subscriber<? super Speaker> subscriber;
        private final boolean single;

        SpeakerExtractor(Subscriber<? super Speaker> subscriber, boolean single) {
            this.subscriber = subscriber;
            this.single = single;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (!single) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Speaker speaker = convert(data);
                    subscriber.onNext(speaker);
                }
            } else {
                subscriber.onNext(convert(dataSnapshot));
                subscriber.onCompleted();
            }
        }

        private Speaker convert(DataSnapshot data) {
            FirebaseSpeaker speaker = data.getValue(FirebaseSpeaker.class);
            return Speaker.newBuilder()
                    .speakerId(data.getKey())
                    .name(speaker.name)
                    .photoUrl(speaker.photoUrl)
                    .description(speaker.description)
                    .company(speaker.company)
                    .jobTitle(speaker.jobTitle)
                    .twitter(speaker.twitter)
                    .website(speaker.website)
                    .github(speaker.github)
                    .gplus(speaker.gplus)
                    .tags(Arrays.asList(speaker.tags.split(",")))
                    .build();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            subscriber.onError(new FirebaseException(databaseError.getMessage()));
        }
    }
}
