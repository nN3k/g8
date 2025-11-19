package logicUtility;

public class XOR extends Gate{
    public XOR(String type, boolean negation) {
        super(type, negation);
        this.setInputs(2);
    }

    @Override
    protected void function() {
        int temp = 0;
        for (int i = 0; i < this.getInputs(); i++) {
            if (this.getInput(i)) {
                temp++;
            }
            if (temp != 1) {
                this.setOutput(false);
            } else {
                this.setOutput(true);
            }
        }
    }
}
