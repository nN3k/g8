package logicUtility;

import javafx.scene.shape.Line;
import main.AppController;

import java.util.ArrayList;

public class Wire {

    /* list of all wires */
    public static ArrayList<Wire> wires = new ArrayList<>();

    /* wire position */
    private double[] pos1 = new double[2];
    private double[] pos2 = new double[2];

    /* wire comes from this gate */
    private Gate sourceGate;
    /* wire goes to this gate */
    private Gate targetGate;

    
    private int gateID;
    private int inputIndex;
    private int wIndex;
    private Line line;
    private boolean outOfUse = false;

    public Wire(Gate sourceGate, Gate targetGate, int inputIndex) {
        this.inputIndex = inputIndex;


        this.sourceGate = sourceGate;
        this.sourceGate.addWire(this);
        this.pos1[0] = sourceGate.getX()+35;
        this.pos1[1] = sourceGate.getY()-20;


        this.targetGate = targetGate;
        this.targetGate.addInputWire(this);
        this.pos2[0] = targetGate.getX()+5;
        this.pos2[1] = targetGate.getY()-20;

        this.gateID = sourceGate.getId();

        wires.add(this);

        this.sendOutput();
    }


    public double[][] getPos() {
        double[][] temp = new double[2][2];
        temp[0][0] = pos1[0];
        temp[0][1] = pos1[1];
        temp[1][0] = pos2[0];
        temp[1][1] = pos2[1];
        return temp;
    }

    public int getInputIndex() {
        return this.inputIndex;
    }

    public Gate getSourceGate() {
        return this.sourceGate;
    }
    public Gate getTargetGate() {
        return this.targetGate;
    }


    public int getGateID() {
        return this.gateID;
    }


    public void setIndex(int index) {
        this.wIndex = index;
    }

    public void sendOutput() {
        try {
            this.targetGate.setInput(this.inputIndex, this.sourceGate.getOutput());
            AppController.updateGateImage(this.targetGate);
            AppController.updateGateImage(this.sourceGate);
        } catch (Exception e) {
            //Wira disconnected msg
        }
    }

    public void updatePos() {

        this.pos1[0] = sourceGate.getX()+35;
        this.pos1[1] = sourceGate.getY()-20;

        this.pos2[0] = targetGate.getX()+5;
        this.pos2[1] = targetGate.getY()-20;
    }


    public boolean isActive() {
        if (this.sourceGate.getOutput()) return true;
        else return false;
    }

    public boolean isDisconnected() {
        return this.outOfUse;
    }

    public void disconnect(){
        this.outOfUse = true;
        this.sourceGate = null;
        this.targetGate = null;
        wires.remove(this);
    }

    public static void resetWire() {
        for (Wire w : wires) {
            w = null;
        }
        wires.clear();
    }
}
