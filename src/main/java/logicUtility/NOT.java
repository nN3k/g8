package logicUtility;

public class NOT extends Gate{
    protected NOT(String type) {
        super(type, true);
        this.setInputs(1);
    }

    @Override
    protected void function() {
        if(this.getInput(0)) {
            this.setOutput(true);
        } else {
            this.setOutput(false);
        }
    }
}
