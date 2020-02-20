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
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Side Character
 */
public class MainWorld extends AbstractAppState {

    private final Node rootNode;
    
    private Node localRootNode = new Node("Level 1");
    private final AssetManager assetManager;
    private final InputManager inputManager;
    
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
        
        
        Box b = new Box(1.00000001f, 1, 1.2f);
        Box BOKS = new Box(10, 0.5f, 1);
        Geometry geom = new Geometry("Box", b);
        Geometry geomm = new Geometry("Boxy", BOKS);

        Material mat = assetManager.loadMaterial("Materials/BlueBoat.j3m");
        
        Material mate = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        geom.setMaterial(mat);
        mate.setColor("Color", ColorRGBA.Yellow);
        geomm.setMaterial(mate);
        localRootNode.attachChild(geom);
        localRootNode.attachChild(geomm);
    }
    @Override
    public void cleanup(){
        rootNode.detachChild(localRootNode);
        super.cleanup();
    }
    private float angleThing = 1;
    @Override
    public void update(float tpf){
        Spatial geom = localRootNode.getChild("Box");
        Spatial geomm = localRootNode.getChild("Boxy");
        if(geom != null){
            Geometry geomme = new Geometry("Boxy", new Box(10, 0.5f, 1));
            Material mate = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mate.setColor("Color", ColorRGBA.Yellow);
            geomme.setMaterial(mate);
//            double angleThing = 0;
            angleThing+= 0.001f;
            float scale = 0.01f + (float)Math.cos(angleThing);
//            geomm = geomme.clone().scale(angleThing);
            geomm.scale(angleThing);
            Vector3f mov = new Vector3f(0,-0.005f,0);
            
            geom.move(mov);
//            geomm.scale(scale);
        }
    }
}
