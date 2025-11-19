package logicUtility;

public class AND extends Gate{
    protected AND(String type, boolean negation) {
        super(type, negation);
        this.setInputs(2);
    }


    @Override
    protected void function() {
        for(int i = 0; i < this.getInputs(); i++) {
            if (!this.getInput(i)) {
                this.setOutput(false);
                return;
            }
        }
        this.setOutput(true);
    }


}
