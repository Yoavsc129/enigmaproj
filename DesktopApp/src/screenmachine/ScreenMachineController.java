package screenmachine;


import engine.Engine;
import engine.MachineDetails;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class ScreenMachineController {



    private final static String FILE_NAME_SUFFIX = ".xml";
    @FXML
    public Button encryptBtn;
    @FXML
    public Button bruteBtn;

    @FXML
    private Button loadFileBtn;

    @FXML
    private Button machineBtn;

    @FXML
    private Text filename;

    @FXML
    private Text fileError;

    @FXML
    private Button randomBtn;

    @FXML
    private Button setBtn;

    @FXML
    private TextField rotorTF;

    @FXML
    private TextField positionTF;

    @FXML
    private TextField plugsTF;

    @FXML
    private ComboBox<?> reflectorCB;

    @FXML
    private Text positionCurr;

    @FXML
    private Text plugsCurr;

    @FXML
    private Text rotorCurr;

    @FXML
    private Text reflectorCurr;

    @FXML
    private Text usedTotalT;

    @FXML
    private Text msgT;

    @FXML
    private Text totalReflectorT;

    @FXML
    private Text initialText;

    private Engine engine;



    @FXML
    void LoadFileBtnActionListener(ActionEvent event) {
        Window window = ((Node) (event.getSource())).getScene().getWindow();
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(window);
        fileError.setText("");
        if(file != null)
        {
            if(!file.getName().endsWith(FILE_NAME_SUFFIX))
                fileError.setText("File name must end with .fxml");
            else{
                try{
                    engine.createMachineFromFile(file.getAbsolutePath());
                    filename.setText(file.getName());
                    updateMachineDetails();
                }catch (Exception e){
                    fileError.setText(e.getMessage());
                }
            }
        }

    }

    private void updateMachineDetails() {
        MachineDetails details = engine.getDetails();
        usedTotalT.setText(String.format("%d/%d", details.getUsedRotors(), details.getTotalRotors()));
        totalReflectorT.setText(String.valueOf(details.getTotalReflectors()));
        msgT.setText(String.valueOf(details.getMessages()));
    }

    @FXML
    void RandomBtnActionListener(ActionEvent event) {

    }

    @FXML
    void SetBtnActionListener(ActionEvent event) {

    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }
}
