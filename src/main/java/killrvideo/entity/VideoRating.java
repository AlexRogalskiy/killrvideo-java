package killrvideo.entity;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

import killrvideo.ratings.RatingsServiceOuterClass.GetRatingResponse;
import killrvideo.utils.TypeConverter;

/**
 * Pojo representing DTO for table 'video_ratings'.
 *
 * @author DataStax evangelist team.
 */
public class VideoRating implements Serializable {

    /** Serial. */
    private static final long serialVersionUID = -8874199914791405808L;

    private UUID videoid;

    private Long ratingCounter;

    private Long ratingTotal;

    /**
     * Mapping to generated GPRC beans.
     */
    public GetRatingResponse toRatingResponse() {
        return GetRatingResponse.newBuilder()
                .setVideoId(TypeConverter.uuidToUuid(videoid))
                .setRatingsCount(Optional.ofNullable(ratingCounter).orElse(0L))
                .setRatingsTotal(Optional.ofNullable(ratingTotal).orElse(0L))
                .build();
    }

    /**
     * Getter for attribute 'videoid'.
     *
     * @return
     *       current value of 'videoid'
     */
    public UUID getVideoid() {
        return videoid;
    }

    /**
     * Setter for attribute 'videoid'.
     * @param videoid
     * 		new value for 'videoid '
     */
    public void setVideoid(UUID videoid) {
        this.videoid = videoid;
    }

    /**
     * Getter for attribute 'ratingCounter'.
     *
     * @return
     *       current value of 'ratingCounter'
     */
    public Long getRatingCounter() {
        return ratingCounter;
    }

    /**
     * Setter for attribute 'ratingCounter'.
     * @param ratingCounter
     * 		new value for 'ratingCounter '
     */
    public void setRatingCounter(Long ratingCounter) {
        this.ratingCounter = ratingCounter;
    }

    /**
     * Getter for attribute 'ratingTotal'.
     *
     * @return
     *       current value of 'ratingTotal'
     */
    public Long getRatingTotal() {
        return ratingTotal;
    }

    /**
     * Setter for attribute 'ratingTotal'.
     * @param ratingTotal
     * 		new value for 'ratingTotal '
     */
    public void setRatingTotal(Long ratingTotal) {
        this.ratingTotal = ratingTotal;
    }

}
