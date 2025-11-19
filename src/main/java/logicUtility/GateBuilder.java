package logicUtility;

/*
* returns new Gate object based of given type for gate
*/

public class GateBuilder {
    public Gate buildGate(String type){
        if(type.equalsIgnoreCase("AND")){
            return new AND("AND", false);

        } else if (type.equalsIgnoreCase("NAND")) {
            return new AND("NAND", true);

        } else if (type.equalsIgnoreCase("OR")) {
            return new OR("OR", false);

        } else if (type.equalsIgnoreCase("NOR")) {
            return new OR("NOR", true);

        } else if (type.equalsIgnoreCase("XOR")) {
            return new XOR("XOR", false);

        } else if (type.equalsIgnoreCase("XNOR")) {
            return new XOR("XNOR", true);

        } else if (type.equalsIgnoreCase("NOT")) {
            return new NOT("NOT");

        } else if (type.equalsIgnoreCase("POWER") || type.equalsIgnoreCase("NPOWER")) {
            return new RedstoneBlock();
        } else if (type.equalsIgnoreCase("OUTPUT") || type.equalsIgnoreCase("!OUTPUT")) {
            return new OUTPUT();
        }
        else {
            throw new IllegalArgumentException("Invalid gate type: " + type);
        }
    }
}
