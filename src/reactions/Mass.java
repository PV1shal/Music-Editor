package reactions;

import music.I;

import java.awt.*;

public abstract class Mass extends Reaction.List implements I.Show{
    public Layer layer;

    public Mass(String layerName){
        // getting the thing attaching to the string (fetching the thing by name)
        this.layer = Layer.byName.get(layerName); // this could fail
        if (layer != null){
            layer.add(this);
        }else{
            System.out.println("Bad Layer Name" + layerName);
        }
    }
    public void deleteMass(){
        clearAll();
        layer.remove(this);
    }

    // the note head themselves can overwrite this show routine.
    public void show (Graphics g){}


}
