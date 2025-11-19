package logicUtility;

public class OR extends Gate{
    protected OR(String type, boolean negation) {
        super(type, negation);
        this.setInputs(2);
    }

    @Override
    protected void function() {
        this.setOutput(false);
        for(int i = 0; i < this.getInputs(); i++) {
            if (this.getInput(i)) {
                this.setOutput(true);
                return;
            }
        }
    }
}
