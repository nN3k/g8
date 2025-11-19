package main;

import logicUtility.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import logicUtility.Wire;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.*;

public class AppController implements Initializable {

    private File file;
    private String fileTpe = ".g8";

    private boolean placeGateFlag = false;
    private boolean moveGateFlag = false;
    private boolean moveCanvasFlag = true;

    private Gate selectedGate;
    private static ArrayList<ImageView> iViews = new ArrayList<>();
    GateBuilder gateBuilder = new GateBuilder();

    private double oldX;
    private double oldY;

    private double canvasX;
    private double canvasY;

    private GraphicsContext gc;

    @FXML
    private Pane mainPane;

    @FXML
    protected Canvas canvas;
    @FXML
    private Label gateID;

    @FXML
    private Label showNumOfInputs;
    @FXML
    private Label showInputs;
    @FXML
    private TextField inputsField;
    @FXML
    private TextField inputField;
    @FXML
    private CheckBox powerBox;
    @FXML
    private Button moveButton;
    @FXML
    private Button deleteButton;



    private void fileExplorer(String mode) throws IOException {
        assert mode.equalsIgnoreCase("LOAD") || mode.equalsIgnoreCase("SAVE");

        FileDialog dialog;
        if (mode.equalsIgnoreCase("SAVE")) {
            dialog = new FileDialog((Frame)null, "Save File");
            dialog.setMode(FileDialog.SAVE);

            dialog.setVisible(true);
            String dir = dialog.getDirectory();
            String file = dialog.getFile();
            dialog.dispose();

            File saveFile = new File(dir+file+fileTpe);
            BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));

            writer.write("");
            for (int i = 0; i < Gate.getGates().size(); i++) {
                Gate gate = Gate.getGates().get(i);
                writer.write("GATE");

                //TODO: is deleted
                writer.append(" " +gate.getType());
                writer.append(" " +gate.getX());
                writer.append(" " +gate.getY());
                writer.append(" " +gate.getInputs());
                writer.newLine();

            }
            for (Wire wire : Wire.wires) {
                writer.write("WIRE");
                writer.append(" " +wire.getSourceGate().getId());
                writer.append(" " +wire.getTargetGate().getId());
                writer.append(" " +wire.getInputIndex());
//                writer.append(" " + Arrays.deepToString(wire.getPos()));
                writer.newLine();
            }

            writer.close();
            saveFile.setWritable(true);
            saveFile.setReadable(true);

        }

        else if (mode.equalsIgnoreCase("LOAD")) {
            String lineData;

            dialog = new FileDialog((Frame)null, "Select File to Open");
            dialog.setMode(FileDialog.LOAD);
            dialog.setVisible(true);
            String dir = dialog.getDirectory();
            String file = dialog.getFile();

            if (file.endsWith(fileTpe)) {
                dialog.dispose();
            }

            BufferedReader reader = new BufferedReader(new FileReader(dir+file));

            int line = 0;
            while ((lineData = reader.readLine()) != null){
                String[] data = lineData.split(" ");

                if (data[0].equals("GATE")) {
                    placeGateFlag = false;
                    createGate(data[1], Double.parseDouble(data[2]), Double.parseDouble(data[3]));
                }

                else if (data[0].equals("WIRE")) {
                    connectGates(Gate.getGateByID(Integer.parseInt(data[1])), Gate.getGateByID(Integer.parseInt(data[2])), Integer.parseInt(data[3]));
                }
                line++;
            }

            reader.close();
            drawConnections();
        }
    }

    @FXML
    public void saveFile() throws IOException {
        fileExplorer("save");
    }
    @FXML
    public void openFile() throws IOException {

        for(ImageView iV : iViews) {
            mainPane.getChildren().remove(iV);
        }
        iViews.clear();

        selectedGate = null;
        placeGateFlag = false;
        gateID.setVisible(false);
        showNumOfInputs.setVisible(false);
        showInputs.setVisible(false);
        inputsField.setVisible(false);
        inputField.setVisible(false);
        powerBox.setVisible(false);
        moveButton.setVisible(false);

        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        Main.resetAll();
        fileExplorer("load");
    }

    @FXML
    public void deleteGate() {
        iViews.get(selectedGate.getId()).setX(-100);
        iViews.get(selectedGate.getId()).setY(-100);
        selectedGate.deleteGate();
        selectedGate = null;
        updateLabels();
    }

    @FXML
    public void moveGate() {
        moveGateFlag = true;
        oldX = selectedGate.getX();
        oldY = selectedGate.getY();
    }

    @FXML
    public void changeInputs(){
        try {
            selectedGate.setInputs(Integer.parseInt(inputsField.getText()));
            updateLabels(selectedGate);
            selectedGate.sendOutput();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void changeConnections(){
        String[] gateIds = inputField.getText().split(" ");
        selectedGate.clearInputWires();
        for (int i = 0; i < gateIds.length; i++) {
            try {
                connectGates(Gate.getGateByID(Integer.parseInt(gateIds[i])), selectedGate, i);
                updateLabels(selectedGate);

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private void connectGates(Gate sourceGate, Gate targetGate, int inputIndex) {
        Wire wire = new Wire(sourceGate, targetGate, inputIndex);
        sourceGate.sendOutput();
        updateGateImage(targetGate);

    }

    @FXML
    public void onPower(){
        selectedGate.flipOutput();
        selectedGate.updateConnections();
        iViews.get(selectedGate.getId()).setImage(new Image(getClass().getResourceAsStream("images/gates/"+selectedGate.getType()+".png")));
        drawConnections();
    }

    public static void updateGateImage(Gate gate) {
        iViews.get(gate.getId()).setImage(new Image(AppController.class.getResourceAsStream("images/gates/"+gate.getType()+".png")));
    }

    private void drawConnections() {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (int i = 0; i < Wire.wires.size(); i++) {
            Wire wire = Wire.wires.get(i);
            gc = canvas.getGraphicsContext2D();
            if (!wire.isDisconnected()){

                if (wire.isActive()) {
                    gc.setStroke(WHITE);
                    gc.setLineWidth(5.0);
                    gc.strokeLine(wire.getPos()[0][0], wire.getPos()[0][1],
                            wire.getPos()[1][0], wire.getPos()[1][1]);
                }

                gc.setStroke(BLACK);
                gc.setLineWidth(3.0);
                gc.strokeLine(wire.getPos()[0][0], wire.getPos()[0][1],
                        wire.getPos()[1][0], wire.getPos()[1][1]);
            }
        }
    }

    private void updateLabels(){
        gateID.setVisible(false);
        moveButton.setVisible(false);
        deleteButton.setVisible(false);
        powerBox.setVisible(false);
        showNumOfInputs.setVisible(false);
        showInputs.setVisible(false);
        inputsField.setVisible(false);
        inputField.setVisible(false);
    }

    private void updateLabels(Gate gate) {
        drawConnections();
        /*HUD*/
        gateID.setVisible(true);
        moveButton.setVisible(true);
        deleteButton.setVisible(true);
        if (gate.getType().equalsIgnoreCase("POWER") || gate.getType().equalsIgnoreCase("NPOWER")) {
            if (gate.getType().equalsIgnoreCase("NPOWER")) {
                powerBox.setSelected(true);
            } else {
                powerBox.setSelected(false);
            }
            powerBox.setVisible(true);

            showNumOfInputs.setVisible(false);
            showInputs.setVisible(false);
            inputsField.setVisible(false);
            inputField.setVisible(false);
        } else if (gate.getType().equalsIgnoreCase("OUTPUT") || gate.getType().equalsIgnoreCase("!OUTPUT") ||
                   gate.getType().equalsIgnoreCase("NOT")) {
            showInputs.setVisible(true);
            inputField.setVisible(true);

            powerBox.setVisible(false);
            showNumOfInputs.setVisible(false);
            inputsField.setVisible(false);
        } else {
            showNumOfInputs.setVisible(true);
            showInputs.setVisible(true);
            inputsField.setVisible(true);
            inputField.setVisible(true);

            powerBox.setVisible(false);
        }

        /*VALUES*/
        try {
            gateID.setText("Gate ID: " + String.valueOf(gate.getId()));
            showNumOfInputs.setText("Number of Inputs: "+gate.getInputs());
            String temp = "";
            for (Gate g : gate.getGates()) {
                for (Wire w : g.getWires()) {
                    if (w.getTargetGate() == selectedGate) {
                        temp += String.valueOf(w.getGateID() + " ");
                    }
                }
            }

            showInputs.setText("Inputs: "+temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        inputField.setText("");
        inputsField.setText("");
    }


    private void selectGate(ImageView imageView) {
        if (placeGateFlag) {
            for (int i = 0; i < Gate.getGates().size(); i++) {
                if (Gate.getGates().get(i).getX() >= imageView.getX() && Gate.getGates().get(i).getX() <= imageView.getX()+40) {
                    if (Gate.getGates().get(i).getY() >= imageView.getY() && Gate.getGates().get(i).getY() <= imageView.getY()+40) {
                        selectedGate = Gate.getGates().get(i);
                    }
                }
            }
            updateLabels(selectedGate);
        }
    }
    private void selectGate(Gate gate) {
        selectedGate = gate;
        updateLabels(selectedGate);
    }

    private void prepImage(ImageView imageView, double x, double y) {
        imageView.setX(x);
        imageView.setY(y);
        imageView.setFitHeight(Renderer.gateSize);
        imageView.setFitWidth(Renderer.gateSize);
    }

    private double[] refinePos(double x, double y) {
        double newX = x-20.0;
        double newY = y+20.0;
        if (newX < 5.0) {
            newX = 5.0;
        } else if (newX > canvas.getWidth()-30.0) {
            newX = canvas.getWidth()-30.0;
        }
        if (newY < 30.0) {
            newY = 30.0;
        } else if (newY > canvas.getHeight()-30.0) {
            newY = canvas.getHeight()-30.0;
        }
        return new double[]{newX, newY};
    }

    private void createGate(String type, double x, double y){
        if(!placeGateFlag){
            Image gateImage = new Image(getClass().getResourceAsStream("images/gates/"+type+".png"));
            ImageView iV = new ImageView(gateImage);
            prepImage(iV,x,y);
            iV.setOnMouseClicked(mouseEvent -> {
                selectGate(iV);
            });
            iViews.add(iV);
            mainPane.getChildren().add(iV);
            selectedGate = gateBuilder.buildGate(type);
            selectedGate.setPos(x,y);
            selectGate(selectedGate);
            placeGateFlag = true;
        }
    }

    private void updateSelectedGatePos(double x, double y) {
        selectedGate.setPos(x, y);
        iViews.get(selectedGate.getId()).setX(x);
        iViews.get(selectedGate.getId()).setY(y);
        try {
            for (Wire wire : selectedGate.getInputWires()) {
                wire.updatePos();
            }

        } catch (Exception e) {e.printStackTrace();}
        try {
            for (Wire wire : selectedGate.getWires()) {
                wire.updatePos();
            }

        } catch (Exception e) {e.printStackTrace();}
        drawConnections();
    }

    private void moveCanvas(double startX, double startY, double endX, double endY) {
        double x = 0;
        double y = 0;

        if (startX <= endX) {
            x = endX-startX;
        } else if (startX > endX) {
            x = startX-endX;
            x = -x;
        }

        if (startY <= endY) {
            y = endY-startY;
        } else if (startY > endY) {
            y = startY-endY;
            y = -y;
        }

        for (Gate gate : Gate.getGates()) {
            iViews.get(gate.getId()).setX(iViews.get(gate.getId()).getX()+x);
            iViews.get(gate.getId()).setY(iViews.get(gate.getId()).getY()+y);
            gate.setPos(gate.getX()+x, gate.getY()+y);
            try {
                for (Wire wire : gate.getInputWires()) {
                    wire.updatePos();
                }

            } catch (Exception e) {e.printStackTrace();}
            try {
                for (Wire wire : gate.getWires()) {
                    wire.updatePos();
                }

            } catch (Exception e) {e.printStackTrace();}
        }
        drawConnections();
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*All gate selection methods*/
        mainPane.setOnMouseClicked(event -> {
            double x = event.getX();
            double y = event.getY();
            if (y <=27) {
                if (x >= 132 && x < 157) {
                    selectPowerGate();
                } else if (x >= 157 && x < 182) {
                    selectOutputGate();
                } else if (x >= 182 && x < 203) {
                    selectNotGate();
                } else if (x >= 203 && x < 228) {
                    selectAndGate();
                } else if (x >= 228 && x < 253) {
                    selectOrGate();
                } else if (x >= 253 && x < 278) {
                    selectXorGate();
                } else if (x >= 278 && x < 303) {
                    selectNandGate();
                } else if (x >= 303 && x < 327) {
                    selectNorGate();
                } else if (x >= 327 && x < 352) {
                    selectXnorGate();
                }


            } else {

            }
        });

        canvas.setOnMousePressed(event -> {
            double x = event.getX();
            double y = event.getY();
            if (y >= 28) {
                if (event.isShiftDown()) {
                    canvasX = event.getX();
                    canvasY = event.getY();
                }
            }
        });


        canvas.setOnMouseDragged(event -> {
            double x = event.getX();
            double y = event.getY();

            if (moveGateFlag) {

                updateSelectedGatePos(refinePos(x,y)[0], refinePos(x,y)[1]);

            } else {
                if (event.isShiftDown()) {

                    moveCanvas(canvasX,canvasY, event.getX(), event.getY());
                    canvasX = x;
                    canvasY = y;
                }
            }
        });

        canvas.setOnMouseReleased(event -> {
            if (moveGateFlag) {
                if (!isViable(event.getX(),event.getY())) {

                    updateSelectedGatePos(oldX,oldY);
                }
            }


        });
    }

    private boolean isViable(double x, double y) {
        int bounds = 35;
        double newX = refinePos(x, y)[0];
        double newY = refinePos(x, y)[1];
        for (Gate gate : Gate.getGates()) {
            if (gate != selectedGate &&
                    newX < gate.getX() +bounds &&
                    newX > gate.getX() -bounds &&

                    newY < gate.getY() +bounds &&
                    newY > gate.getY()-bounds){

                return false;
            }
        }
        return true;
    }

    public void selectAndGate() {
        placeGateFlag = false;
        canvas.setOnMouseClicked(event -> {
            if (isViable(event.getX(), event.getY())) {
                double x = refinePos(event.getX(), event.getY())[0];
                double y = refinePos(event.getX(), event.getY())[1];
                createGate("AND", x,y);
            }
        });
    }

    public void selectNandGate() {
        placeGateFlag = false;
        canvas.setOnMouseClicked(event -> {
            if (isViable(event.getX(), event.getY())) {
                double x = refinePos(event.getX(), event.getY())[0];
                double y = refinePos(event.getX(), event.getY())[1];
                createGate("NAND", x,y);
            }
        });
    }

    public void selectOrGate() {
        placeGateFlag = false;
        canvas.setOnMouseClicked(event -> {
            if (isViable(event.getX(), event.getY())) {
                double x = refinePos(event.getX(), event.getY())[0];
                double y = refinePos(event.getX(), event.getY())[1];
                createGate("OR", x,y);
            }
        });
    }

    public void selectNorGate() {
        placeGateFlag = false;
        canvas.setOnMouseClicked(event -> {
            if (isViable(event.getX(), event.getY())) {
                double x = refinePos(event.getX(), event.getY())[0];
                double y = refinePos(event.getX(), event.getY())[1];
                createGate("NOR", x,y);
            }
        });
    }

    public void selectXorGate() {
        placeGateFlag = false;
        canvas.setOnMouseClicked(event -> {
            if (isViable(event.getX(), event.getY())) {
                double x = refinePos(event.getX(), event.getY())[0];
                double y = refinePos(event.getX(), event.getY())[1];
                createGate("XOR", x,y);
            }
        });
    }

    public void selectXnorGate() {
        placeGateFlag = false;
        canvas.setOnMouseClicked(event -> {
            if (isViable(event.getX(), event.getY())) {
                double x = refinePos(event.getX(), event.getY())[0];
                double y = refinePos(event.getX(), event.getY())[1];
                createGate("XNOR", x,y);
            }
        });
    }

    public void selectNotGate() {
        placeGateFlag = false;
        canvas.setOnMouseClicked(event -> {
            if (isViable(event.getX(), event.getY())) {
                double x = refinePos(event.getX(), event.getY())[0];
                double y = refinePos(event.getX(), event.getY())[1];
                createGate("NOT", x,y);
            }
        });
    }

    public void selectPowerGate() {
        placeGateFlag = false;
        canvas.setOnMouseClicked(event -> {
            if (isViable(event.getX(), event.getY())) {
                double x = refinePos(event.getX(), event.getY())[0];
                double y = refinePos(event.getX(), event.getY())[1];
                createGate("POWER", x,y);
            }
        });
    }
    @FXML
    public void selectOutputGate() {
        placeGateFlag = false;
        canvas.setOnMouseClicked(event -> {
            if (isViable(event.getX(), event.getY())) {
                double x = refinePos(event.getX(), event.getY())[0];
                double y = refinePos(event.getX(), event.getY())[1];
                createGate("OUTPUT", x,y);
            }
        });
    }
    /*****************************/

}
