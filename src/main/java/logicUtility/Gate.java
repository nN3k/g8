package logicUtility;


/************************************/
/************************************/
/* Explaining comment will be ABOVE */
/************************************/
/************************************/

/*
Input values:
* 0 = false
* 1 = true
* 3 = not connected
*/

import help.*;

import java.util.ArrayList;

public class Gate {

    /*
     list of all gates
    *  gate ID is index of gate in list
    * */
    private static ArrayList<Gate> gates = new ArrayList<>();

    private boolean isDeleted = false;

    private String type;
    private boolean output = false;
    /* all input values */
    private boolean[] input;
    private boolean negation;
    /* number of inputs */
    private int inputs;
    /* wires going FROM this TO other gate */
    private ArrayList<Wire> wires = new ArrayList<>();
    /* wire going TO other gate FROM this */
    private ArrayList<Wire> inputWires = new ArrayList<>();
    private int wIndex = 0;

    /*V gate cords V*/
    private double x;
    private double y;
    /*^ gate cords ^*/

    /*V next ID V*/
    private static int idCount = 0;
    /*V ID of THIS gate V*/
    private final int id;

    protected Gate(String type, boolean negation) {
        this.id = idCount;
        idCount++;
        this.type = type;
        this.negation = negation;
        this.output = false;

        gates.add(this);

        Log.logAdd(type + " gate was created with ID: " + this.id);
    }




    /* Getter */

    /* return gate from list of all gates by ID/index */
    public static Gate getGateByID(int id) {
        return gates.get(id);
    }

    /* return type of gate */
    public String getType() {
        return this.type;
    }

    /*
    * return true output as boolean
    * negation value will be considered
    */
    public boolean getOutput() {
        if (this.negation == true) {
            return !this.output;
        } else {
            return output;
        }
    }
    /* return output without consideration of negation */
    public boolean getRawOutput() {
        return this.output;
    }

    /* return input value of from list of inputs at index */
    public boolean getInput(int index) {
        return this.input[index];
    }

    /* return number of inputs */
    public int getInputs() {
        return this.inputs;
    }

    /* return gate ID */
    public int getId() {
        return this.id;
    }

    /* return ID count*/
    public static int getIdCount() {
        return idCount;
    }

    /* return gate position X */
    public double getX() {
        return x;
    }

    /* return gate position Y */
    public double getY() {
        return y;
    }


    /* return this gate */
    public Gate getGate() {
        return this;
    }

    /* return list of all gates */
    public static ArrayList<Gate> getGates() {
        return gates;
    }

    /* return list of wires from this to other gate */
    public ArrayList<Wire> getWires() {
        return wires;
    }

    /* return list of wires from others to this gate */
    public ArrayList<Wire> getInputWires() {
        return this.inputWires;
    }




    /* Setter */

    /* set output of this gate and send it to update connected gats */
    protected void setOutput(boolean output) {
            this.output = output;
        for (Wire w : this.wires) {
            if (!w.isDisconnected()){
                w.sendOutput();
            }
        }
    }

    /*
    * flip output of this gate
    * does not update connected gates
    */
    public void flipOutput() {
        this.output = !this.output;
    }

    /* set input[index] to value and calculate output (function) */
    public void setInput(int index, boolean value) {
        this.input[index] = value;
        this.function();
    }
    /* set number of inputs
    * calculate new output
    */
    public void setInputs(int inputs) {
        this.input = new boolean[inputs];
        this.inputs = inputs;
        this.function();
    }


    /* set X position */
    public void setX(double x) {
        this.x = x;
    }

    /* set Y position */
    public void setY(double y) {
        this.y = y;
    }

    /* adds a wire to wires list */
    public void addWire(Wire wire) {
        this.wires.add(wire);
        wire.setIndex(wIndex);
        wIndex++;
    }

    /* adds a wire to wireInputs list */
    public void addInputWire(Wire wire) {
        this.inputWires.add(wire);
    }

    /* clears inputWires list */
    public void clearInputWires() {
        for (Wire w : this.inputWires) {
            w.disconnect();
        }
        this.inputWires.clear();
    }
    /* clears wire list */
    public void clearWires() {
        for (Wire w : this.wires) {
            w.disconnect();
        }
        this.wires.clear();
    }

    /* set gate position */
    public void setPos(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

    /* set type of gate */
    protected void setType(String type) {
        this.type = type;
    }



    /*    Methods    */

    /* gate will be permanently out of use but not deleted
    * TODO: delete gate for real and fix then acuring issie with index in list of all gates  */
    public void deleteGate() {
        this.isDeleted = true;
        this.setOutput(false);
        this.clearInputWires();
        this.clearWires();
    }

    /*TODO:
    *  Could implement reconstruction to avoid index error
    *  will cause significant performance loss*/
    public void reconstructGate(String type, boolean negation, int inputs) throws Exception {
        if (this.isDeleted) {
            this.type = type;
            this.negation = negation;
            this.inputs = inputs;
            this.input = new boolean[inputs];

            Log.logAdd("Gate reconstruction: " + type + " gate was created with ID: " + this.id);
        } else {
            Log.logAdd("Tried to reconstruct used gate: " + this.id);
            throw new Exception("Gate" +this.id +" is in use. Cannot reconstruct gate");
        }
    }



    /*V sends output to conected gates V*/
    public void sendOutput() {
        for (int i = 0; i < this.wires.size(); i++) {
            this.wires.get(i).sendOutput();
        }
    }
    public void updateConnections(){
        for (int i = 0; i < this.wires.size(); i++) {
            if (!this.wires.get(i).isDisconnected()) {
                this.wires.get(i).getTargetGate().sendOutput();
            }
        }
    }
    /*^ sends output to connected gates ^*/



    /*
    * Gate Logic
    * Needs Override
    */
    protected void function() {
        boolean isDefault = true;
        assert (isDefault != true) : "Method was not overriden, gate has no function";
    }

    /* completely deletes all gates */
    public static void resetGate(){
        for (Gate g : gates) {
            g = null;
        }
        gates.clear();
        idCount = 0;
    }
}
