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
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

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
    
    public MainWorld(SimpleApplication app){
        rootNode = app.getRootNode();
        assetManager = app.getAssetManager();
        inputManager = app.getInputManager();
        inputManager.addMapping("pause", new KeyTrigger(KeyInput.KEY_P));
//        inputManager.addListener(listener, "pause");
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        rootNode.attachChild(localRootNode);
        //loading the physics controller
        bulletAppState = new BulletAppState();
        bulletAppState.setDebugEnabled(true);
        stateManager.attach(bulletAppState);
        
        //loading the scene
        
        Spatial scene = assetManager.loadModel("Scenes/mainWorld.j3o");
        localRootNode.attachChild(scene);
        //loading the floor and attaching physics
        
        Spatial floor = localRootNode.getChild("floor");
        bulletAppState.getPhysicsSpace().add(floor.getControl(RigidBodyControl.class));
        
        //loading the player and attaching physics
        
        player = localRootNode.getChild("player");
        BoundingBox boundingBox = (BoundingBox)player.getWorldBound();
        float radius = boundingBox.getXExtent();
        float height = boundingBox.getYExtent();
        
        CapsuleCollisionShape playerShape = new CapsuleCollisionShape(radius, height);
        
        playerControl = new CharacterControl(playerShape, 1.0f);
        player.addControl(playerControl);
        
        bulletAppState.getPhysicsSpace().add(playerControl);
        
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
            
        
    }
}
