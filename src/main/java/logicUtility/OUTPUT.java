package logicUtility;

import main.AppController;

public class OUTPUT extends Gate{
    protected OUTPUT() {
        super("!OUTPUT", false);
        this.setInputs(1);
    }


    @Override
    protected void function() {
        if (!this.getInput(0)) {
            this.setType("OUTPUT");
            this.setOutput(false);
        } else if (this.getInput(0)) {
            this.setType("!OUTPUT");
            this.setOutput(true);
        }
        AppController.updateGateImage(this);
    }
}
