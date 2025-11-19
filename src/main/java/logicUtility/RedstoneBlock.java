package logicUtility;

public class RedstoneBlock extends Gate{
    protected RedstoneBlock() {
        super("POWER", false);
        this.setInputs(0);
        this.setOutput(false);
    }

    public void typeSwap() {
        if (this.getType().equalsIgnoreCase("POWER")) {
            this.setType("NPOWER");
        } else if (this.getType().equalsIgnoreCase("NPOWER")) {
            this.setType("POWER");
        }
    }

    @Override
    public void flipOutput(){
        this.setOutput(!this.getRawOutput());
        this.typeSwap();
    }
    

    @Override
    protected void function() {

    }
}
