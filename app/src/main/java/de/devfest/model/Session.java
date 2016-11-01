package de.devfest.model;

import org.threeten.bp.ZonedDateTime;

import java.util.List;

import de.devfest.data.DataUtils;

public final class Session {
    public final String id;
    public final String title;
    public final String description;
    public final String language;
    // likely to be changed
    public final Stage stage;
    public final ZonedDateTime startTime;
    public final ZonedDateTime endTime;
    public final List<Speaker> speakers;
    public final Track track;
    public final boolean isScheduable;

    private Session(Builder builder) {
        id = builder.id;
        title = builder.title;
        description = builder.description;
        language = builder.language;
        stage = builder.stage;
        startTime = builder.startTime;
        endTime = builder.endTime;
        speakers = builder.speakers;
        track = builder.track;
        isScheduable = builder.isScheduable;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Session)) return false;
        Session other = (Session) obj;
        return id.equals(other.id)
                && startTime.equals(other.startTime)
                && endTime.equals(other.endTime)
                && title.equals(other.title)
                && description.equals(other.description)
                && language.equals(other.language)
                && DataUtils.equals(speakers, other.speakers)
                && track.equals(other.track)
                && isScheduable == other.isScheduable;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private String title;
        private String description;
        private String language;
        private Stage stage;
        private ZonedDateTime startTime;
        private ZonedDateTime endTime;
        private List<Speaker> speakers;
        private Track track;
        private boolean isScheduable;

        private Builder() {
        }

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Builder title(String val) {
            title = val;
            return this;
        }

        public Builder description(String val) {
            description = val;
            return this;
        }

        public Builder language(String val) {
            language = val;
            return this;
        }

        public Builder stage(Stage val) {
            stage = val;
            return this;
        }

        public Builder startTime(ZonedDateTime val) {
            startTime = val;
            return this;
        }

        public Builder endTime(ZonedDateTime val) {
            endTime = val;
            return this;
        }

        public Builder speaker(List<Speaker> val) {
            speakers = val;
            return this;
        }

        public Builder track(Track val) {
            track = val;
            return this;
        }

        public Builder isScheduable(boolean val) {
            isScheduable = val;
            return this;
        }

        public Session build() {
            return new Session(this);
        }
    }
}
