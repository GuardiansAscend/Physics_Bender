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
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
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
        
        //loading the player and attaching physics
        Spatial playerModel = assetManager.loadModel("Models/newOutGate.j3o");
        
        localRootNode.attachChild(playerModel);
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
        
//       DirectionalLight sun = new DirectionalLight();
//        DirectionalLight sun2 = new DirectionalLight();
//        sun.setDirection(new Vector3f(-1f,1f,-1f).normalizeLocal());
//        sun2.setDirection(new Vector3f(1f,1f,-1f).normalizeLocal());
//        localRootNode.getChild("DirectionalLight");
//        localRootNode.addLight(sun);
//        localRootNode.addLight(sun2);
        
        player = localRootNode.getChild("player");
        BoundingBox boundingBox = (BoundingBox)player.getWorldBound();
        float radius = boundingBox.getXExtent();
        float height = boundingBox.getYExtent();
        
        CapsuleCollisionShape playerShape = new CapsuleCollisionShape(radius, (height + 3.2f));
        
        playerControl = new CharacterControl(playerShape, 1.0f);
        playerControl.setUp(new Vector3f(0f,1f,0f));
        player.addControl(playerControl);
        
        bulletAppState.getPhysicsSpace().add(playerControl);
        
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0f,9.8f,0f));
        
        
        
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
        
        //load terrain
        Node rootScene = (Node) scene;
        TerrainLodControl terrainControl = rootScene.getControl(TerrainLodControl.class);
        if(terrainControl != null)
            terrainControl.setCamera(camera);
        Spatial terrain = rootScene.getChild("terrain-mainWorld");
        terrain.addControl(new RigidBodyControl(0));
        bulletAppState.getPhysicsSpace().add(terrain);
        
        
//        Spatial classicGate = assetManager.loadModel("Models/outsideDoor.obj");
//        Spatial boat = assetManager.loadModel("Models/Boat/boat.j3o");
//        Box b = new Box(4, 0.0001f, 4);
//        Geometry geom = new Geometry("Box", b);
//        
//        Material boatMat = assetManager.loadMaterial("Materials/BlueBoat.j3m");
        
//        Material gateMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        gateMat.setTexture("LightMap", gateText);
//        gateMat.setColor("Color", ColorRGBA.Blue);
//        classicGate.setMaterial(gateMat);
//        
//        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        Texture grass = assetManager.loadTexture("Textures/Dark-Green-Grass-Texture.jpg");
//        geom.getMesh().scaleTextureCoordinates(new Vector2f(25,25));
//        grass.setWrap(Texture.WrapMode.Repeat);
//        mat.setTexture("LightMap", grass);
//        
//        geom.setMaterial(mat);
//        boat.setMaterial(boatMat);
//        geom.setLocalTranslation(0, 0, 0);
//        classicGate.setLocalTranslation(0,0,0);
//        classicGate.scale(0.0005f);
//        localRootNode.attachChild(geom);
//        localRootNode.attachChild(classicGate);
//        localRootNode.attachChild(boat);
//        localRootNode.attachChild(geomm);
        localRootNode.attachChild(scene);
        
    }
    @Override
    public void cleanup(){
        rootNode.detachChild(localRootNode);
        super.cleanup();
    }
    @Override
    public void update(float tpf){
        Vector3f camDir = camera.getDirection().clone();
        Vector3f camLeft = camera.getLeft().clone();
        camDir.y = 0;
        camLeft.y = 0;
        
        camDir.normalizeLocal();
        camLeft.normalizeLocal();
        
        playerWalkDirr.set(new Vector3f(0f,0f,0f));
        
        if (left) playerWalkDirr.addLocal(camLeft);
        if (right) playerWalkDirr.addLocal(camLeft.negate());
        if (up) playerWalkDirr.addLocal(camDir);
        if (down) playerWalkDirr.addLocal(camDir.negate());
        
        if(player != null) {
            playerWalkDirr.multLocal(5.0f).multLocal(tpf);
            playerControl.setWalkDirection(playerWalkDirr);
        }
        
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
