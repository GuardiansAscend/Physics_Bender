package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import mygame.State.MainWorld;
import mygame.State.GravityWorld;
import mygame.State.WindyHills;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    Boolean first = true;
    String current = "MW";
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    public Main appp;

    @Override
    public void simpleInitApp() {
        appp = this;
        stateManager.attach(new MainWorld(this));
        inputManager.addMapping("MW", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("WH", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("swap", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addListener(actionListener, "MW");
        inputManager.addListener(actionListener, "WH");
        inputManager.addListener(actionListener, "swap");
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    public void Swap(){
//        Main app = new Main();
        stateManager.cleanup();
        
    }
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf){
            if(name.equals("swap")){
                Node root = (Node) appp.rootNode.getChild(0);
                Spatial player = root.getChild("player");
                System.out.println(player.getLocalTranslation());
                //gravity door
                if((player.getLocalTranslation().x < 21 && player.getLocalTranslation().x > 12)&&
                        (player.getLocalTranslation().z < 60 &&player.getLocalTranslation().z > 48)
                        ){
//                    stateManager.detach(new MainWorld(appp));
                    stateManager.cleanup();
                    stateManager.attach(new GravityWorld(appp));
                }else if((player.getLocalTranslation().x < -50 && player.getLocalTranslation().x > -60)&&
                        (player.getLocalTranslation().z < 36 &&player.getLocalTranslation().z > 26)){
//                    stateManager.detach(new MainWorld(appp));
                    stateManager.cleanup();
                    stateManager.attach(new WindyHills(appp));
                }
                
            }else
            if(name.equals("WH")){
                stateManager.detach(new MainWorld(appp));
                stateManager.cleanup();
                stateManager.attach(new WindyHills(appp));
            }else if(name.equals("MW")){
                stateManager.detach(new WindyHills(appp));
                stateManager.cleanup();
                stateManager.attach(new MainWorld(appp));
                }
        }
    };
}
