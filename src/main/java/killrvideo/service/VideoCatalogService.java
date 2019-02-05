package killrvideo.service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.datastax.dse.driver.api.core.DseSession;
import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilderDsl;
import com.datastax.oss.driver.api.querybuilder.insert.RegularInsert;
import com.datastax.oss.driver.shaded.guava.common.collect.Sets;
import com.google.protobuf.ProtocolStringList;

import io.grpc.Status;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.grpc.stub.StreamObserver;
import killrvideo.dataLayer.VideoAccess;
import killrvideo.entity.Video;
import killrvideo.validation.KillrVideoInputValidator;
import killrvideo.video_catalog.VideoCatalogServiceGrpc.VideoCatalogServiceImplBase;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.GetLatestVideoPreviewsRequest;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.GetLatestVideoPreviewsResponse;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.GetUserVideoPreviewsRequest;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.GetUserVideoPreviewsResponse;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.GetVideoPreviewsRequest;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.GetVideoPreviewsResponse;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.GetVideoRequest;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.GetVideoResponse;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.SubmitYouTubeVideoRequest;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.SubmitYouTubeVideoResponse;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.VideoLocationType;

import java.io.File;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static killrvideo.utils.ExceptionUtils.mergeStackTrace;


@Service
//public class VideoCatalogService extends AbstractVideoCatalogService {
public class VideoCatalogService extends VideoCatalogServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoCatalogService.class);
    VideoAccess videoAccess = new VideoAccess();

    @Inject
    KillrVideoInputValidator validator;

    @PostConstruct
    public void init(){
    }

    @Override
    public void submitYouTubeVideo(SubmitYouTubeVideoRequest request, StreamObserver<SubmitYouTubeVideoResponse> responseObserver) {

        LOGGER.debug("-----Start adding YouTube video -----");
        
        if (!validator.isValid(request, responseObserver)) {
            return;
        }

        try
        {       

            final Date now = new Date();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            final String yyyyMMdd = dateFormat.format(now);
            final String location = request.getYouTubeVideoId();
            final String name = request.getName();
            final String description = request.getDescription();
            final ProtocolStringList tagsList = request.getTagsList();
            final String previewImageLocation = "//img.youtube.com/vi/"+ location + "/hqdefault.jpg";
            final UUID videoId = UUID.fromString(request.getVideoId().getValue());
            final UUID userId = UUID.fromString(request.getUserId().getValue());
        
            Video newVideo = new Video(videoId, userId, name, description, location,
                                    VideoLocationType.YOUTUBE.ordinal(), previewImageLocation, 
                                    Sets.newHashSet(tagsList.iterator()), now);

            videoAccess.addNewVideo(newVideo);   
            
            LOGGER.debug("Added new video: \n" + newVideo);

            responseObserver.onNext(SubmitYouTubeVideoResponse.newBuilder().build());
            responseObserver.onCompleted();

            LOGGER.debug("End submitting youtube video");
        }
        catch(Throwable e)
        {
            e.printStackTrace();
            LOGGER.debug("Error: " + e);
        }
    }

    @Override
    public void getVideo(GetVideoRequest request, StreamObserver<GetVideoResponse> responseObserver) {

    }

    @Override
    public void getVideoPreviews(GetVideoPreviewsRequest request, StreamObserver<GetVideoPreviewsResponse> responseObserver) {

    }

    @Override
    public void getLatestVideoPreviews(GetLatestVideoPreviewsRequest request, StreamObserver<GetLatestVideoPreviewsResponse> responseObserver) {
        LOGGER.debug("-----Start getting latest video preview-----");

        if (!validator.isValid(request, responseObserver)) {
            return;
        }

        final List<VideoCatalogServiceOuterClass.VideoPreview> results = new ArrayList<>();

        try {
            responseObserver.onNext(GetLatestVideoPreviewsResponse
                    .newBuilder()
                    .addAllVideoPreviews(results)
                    .build());
            responseObserver.onCompleted();

        } catch (Throwable throwable) {
            LOGGER.error("Exception when getting latest preview videos : " + mergeStackTrace(throwable));
            responseObserver.onError(Status.INTERNAL.withCause(throwable).asRuntimeException());
        }

        LOGGER.debug("End getting latest video preview");
    }


    @Override
    public void getUserVideoPreviews(GetUserVideoPreviewsRequest request, StreamObserver<GetUserVideoPreviewsResponse> responseObserver) {

    }

}