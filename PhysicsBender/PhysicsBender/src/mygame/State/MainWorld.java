/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.State;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.Terrain;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

/**
 *
 * @author Side Character
 */
public class MainWorld extends AbstractAppState {

    private final Node rootNode;
    
    private Node localRootNode = new Node("Level 1");
    private final AssetManager assetManager;
    private final InputManager inputManager;
    private BulletAppState bulletAppState;
    private Spatial player;
    private CharacterControl playerControl;
    private final FlyByCamera flyByCamera;
    private final Camera camera;
    private ChaseCamera chaseCam;
    
    private final Vector3f playerWalkDirr = Vector3f.ZERO;
    
    private boolean left= false; 
    private boolean right= false; 
    private boolean up= false; 
    private boolean down= false; 
    private boolean space= false; 
    
    public MainWorld(SimpleApplication app){
        rootNode = app.getRootNode();
        assetManager = app.getAssetManager();
        inputManager = app.getInputManager();
        flyByCamera = app.getFlyByCamera();
        camera = app.getCamera();
        
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        rootNode.attachChild(localRootNode);
        //loading the physics controller
        bulletAppState = new BulletAppState();
        bulletAppState.setDebugEnabled(false);
        stateManager.attach(bulletAppState);
        
        //loading the scene
        
        Spatial scene = assetManager.loadModel("Scenes/mainWorld.j3o");
        localRootNode.attachChild(scene);   
        
        //loading skyBox
        Texture west = app.getAssetManager().loadTexture("Textures/Sky/westView.jpg");
        Texture east = app.getAssetManager().loadTexture("Textures/Sky/eastView.jpg");
        Texture front = app.getAssetManager().loadTexture("Textures/Sky/frontbackView.jpg");
        Texture back = app.getAssetManager().loadTexture("Textures/Sky/newbackView.jpg");
        Texture upIMG = app.getAssetManager().loadTexture("Textures/Sky/topView.jpg");
        Texture downIMG = app.getAssetManager().loadTexture("Textures/Sky/bottomView.jpg");
        localRootNode.attachChild(SkyFactory.createSky(app.getAssetManager(), west, east, front, back, upIMG, downIMG));
        
        //loading the Doors
        //Door 1
        Spatial doorOneModel = assetManager.loadModel("Models/newOutGate.j3o");
        doorOneModel.setLocalTranslation(15.5f, 7f, 55);
        doorOneModel.rotate(0,((float) -(5*Math.PI/7.2)), 0);
        localRootNode.attachChild(doorOneModel);
        
        // Scrapped Adding a rigid body to it, it makes the app run slow 
//        BoundingBox doorOneBB = (BoundingBox)doorOneModel.getWorldBound();
//        BoxCollisionShape shape = new BoxCollisionShape(new Vector3f(doorOneBB.getXExtent(),doorOneBB.getZExtent(),doorOneBB.getYExtent()));
//        RigidBodyControl doorOneRigidBody = new RigidBodyControl(shape, 1);
//        doorOneModel.addControl(doorOneRigidBody);
//        bulletAppState.getPhysicsSpace().add(doorOneRigidBody);

        Geometry OGgeom0 = (Geometry) localRootNode.getChild("newOutGate-geom-0");
        Geometry OGgeom1 = (Geometry) localRootNode.getChild("newOutGate-geom-1");
        Geometry OGgeom2 = (Geometry) localRootNode.getChild("newOutGate-geom-2");
        Geometry OGgeom3 = (Geometry) localRootNode.getChild("newOutGate-geom-3");
        Material OGmat0 = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        Material OGmat1 = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        Material OGmat2 = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        Material OGmat3 = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        OGmat0.setTexture("LightMap", assetManager.loadTexture("Textures/outGate/Boole.1Surface_Color.jpg"));
        OGmat1.setTexture("LightMap", assetManager.loadTexture("Textures/outGate/Null.1Surface_Color.jpg"));
        OGmat2.setTexture("LightMap", assetManager.loadTexture("Textures/outGate/Symmetry.1Surface_Color.jpg"));
        OGmat3.setTexture("LightMap", assetManager.loadTexture("Textures/outGate/Symmetry.2Surface_Color.jpg"));
        OGgeom0.setMaterial(OGmat0);
        OGgeom1.setMaterial(OGmat1);
        OGgeom2.setMaterial(OGmat2);
        OGgeom3.setMaterial(OGmat3);
        
        //Door 2
        Spatial doorTwoModel = assetManager.loadModel("Models/outsideDoor.j3o");
        doorTwoModel.rotate(0, ((float) -(5*Math.PI/7.2)), 0);
        doorTwoModel.scale(0.0025f);
        doorTwoModel.setLocalTranslation(-19, 7, 55);
        localRootNode.attachChild(doorTwoModel);
//        Geometry D2G0 = (Geometry) doorTwoModel;
//        Material D2M0 = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
//        D2G0.setMaterial(assetManager.loadMaterial("Models/"));
//        D2M0.setColor("Color", ColorRGBA.Pink);
//        D2G0.setMaterial(D2M0);

        //Door 3
        Spatial teleGateModel = assetManager.loadModel("Models/TeleportationGate_V2/14023_Teleportation_Gate_v2.j3o");
        teleGateModel.rotate(-(float) (Math.PI/2),(float) Math.PI/2, 0);
        teleGateModel.scale(0.03f);
        teleGateModel.setLocalTranslation(-56.5f, 8, 31.5f);
        localRootNode.attachChild(teleGateModel);
        Geometry teleGate = (Geometry) localRootNode.getChild("14023_Teleportation_Gate_v2_l1-geom-0");
        Geometry teleGate1 = (Geometry) localRootNode.getChild("14023_Teleportation_Gate_v2_l1-geom-1");
        Geometry teleGate2 = (Geometry) localRootNode.getChild("14023_Teleportation_Gate_v2_l1-geom-2");
        
        Material teleGateMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        Material teleGateMat1 = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        teleGateMat.setTexture("LightMap", assetManager.loadTexture("Textures/TeleGateMat/14023_TeleportationGate_V2_diffuse.jpg"));
        teleGateMat1.setTexture("LightMap", assetManager.loadTexture("Textures/TeleGateMat/14023_TeleportationGate_V2_diffuse2.jpg"));
        
        teleGate.setMaterial(teleGateMat1);
        teleGate1.setMaterial(teleGateMat1);
        teleGate2.setMaterial(teleGateMat);
        
        //KNOB
        Spatial knob = assetManager.loadModel("Models/10905_door knob_v1_L3.j3o");
        knob.setLocalTranslation(8, 10, 10);
        localRootNode.attachChild(knob);
        Geometry knobb = (Geometry) knob;
        Material knobMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        knobMat.setTexture("LightMap", assetManager.loadTexture("Textures/knob.png"));
        knob.setMaterial(knobMat);
        
        //loading player and attaching controls to it
        player = localRootNode.getChild("player");
        BoundingBox boundingBox = (BoundingBox)player.getWorldBound();
        BoxCollisionShape playerShape = new BoxCollisionShape(new Vector3f(boundingBox.getXExtent(),boundingBox.getYExtent()*2-0.31f,boundingBox.getZExtent()));
        playerControl = new CharacterControl(playerShape, 1.0f);
        playerControl.setUp(new Vector3f(0f,1f,0f));
        player.addControl(playerControl);
        
        bulletAppState.getPhysicsSpace().add(playerControl);
        
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0f,-9.8f,0f));
        
        
        
        // setup the input
        inputManager.addMapping("pause", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "pause");
        inputManager.addListener(actionListener, "up");
        inputManager.addListener(actionListener, "down");
        inputManager.addListener(actionListener, "left");
        inputManager.addListener(actionListener, "right");
        inputManager.addListener(actionListener, "jump");
        
        //disable flyByCamera
        flyByCamera.setEnabled(false);
        
        //setup chaseCam
        chaseCam = new ChaseCamera(camera, player, inputManager);
        chaseCam.setUpVector(new Vector3f(0,1,0));
        
        //load terrain
        Node rootScene = (Node) scene;
        TerrainLodControl terrainControl = rootScene.getControl(TerrainLodControl.class);
        if(terrainControl != null)
            terrainControl.setCamera(camera);
        Spatial terrain = rootScene.getChild("terrain-mainWorld");
        terrain.addControl(new RigidBodyControl(0));
        bulletAppState.getPhysicsSpace().add(terrain);
        
        System.out.println(rootScene.getChildren());
        
        localRootNode.attachChild(scene);
        
    }
    @Override
    public void cleanup(){
        rootNode.detachChild(localRootNode);
        super.cleanup();
    }
    @Override
    public void update(float tpf){
        //updating the camera and fetching its directions
        Vector3f camDir = camera.getDirection().clone();
        Vector3f camLeft = camera.getLeft().clone();
        camDir.y = 0;
        camLeft.y = 0;
        
        camDir.normalizeLocal();
        camLeft.normalizeLocal();
        //updating the player position
        playerWalkDirr.set(new Vector3f(0f,0f,0f));
        
        if (left) playerWalkDirr.addLocal(camLeft);
        if (right) playerWalkDirr.addLocal(camLeft.negate());
        if (up) playerWalkDirr.addLocal(camDir);
        if (down) playerWalkDirr.addLocal(camDir.negate());
        
        if(player != null) {
            playerWalkDirr.multLocal(30.0f).multLocal(tpf);
            playerControl.setWalkDirection(playerWalkDirr);
        }
        //checking if the player is at a door and making him swap world
    }
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf){
            if(name.equals("Pause")&& !keyPressed) {
                setEnabled(!isEnabled());
            }else if(name.equals("up")){
                up = keyPressed;
            }else if(name.equals("down")){
                down = keyPressed;
            }else if(name.equals("left")){
                left = keyPressed;
            }else if(name.equals("right")){
                right = keyPressed;
            }else if(name.equals("jump")){
                playerControl.jump(new Vector3f(0f,20f,0f));
            }
        }
    };
}
