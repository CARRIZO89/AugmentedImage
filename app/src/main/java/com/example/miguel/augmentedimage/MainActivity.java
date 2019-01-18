package com.example.miguel.augmentedimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.Collection;

public class MainActivity extends AppCompatActivity implements Scene.OnUpdateListener {

    private CustomArFragment arFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this);
    }

    public void setupDatabase(Config config, Session session){
        Bitmap flashBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.justiceleague2017);
        AugmentedImageDatabase aid = new AugmentedImageDatabase(session);
        aid.addImage("justiceleague2017", flashBitmap);
        config.setAugmentedImageDatabase(aid);
    }

    @Override
    public void onUpdate(FrameTime frameTime) {

        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<AugmentedImage> images = frame.getUpdatedTrackables(AugmentedImage.class);

        for (AugmentedImage image : images ) {
            if (image.getTrackingState() == TrackingState.TRACKING){
                if (image.getName().equals("justiceleague2017")){
                    Anchor anchor = image.createAnchor(image.getCenterPose());

                    createModel(anchor);
                }
            }
        }

    }

    private void createModel(Anchor anchor) {
        ModelRenderable.builder()
                .setSource(this, Uri.parse("model.sfb"))
                .build()
                .thenAccept(modelRenderable -> placeModel(modelRenderable, anchor));
    }

    private void placeModel(ModelRenderable modelRenderable, Anchor anchor) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }
}
