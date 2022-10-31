package client.component.tabs.dashboardTab.subComp.singleContest;

import client.component.tabs.dashboardTab.subComp.contestList.ContestListController;
import engine.serverLogic.Battlefield;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class SingleContestController {
    @FXML
    private GridPane node;
    @FXML
    private Label battlefieldLbl;

    @FXML
    private Label uBoatLbl;

    @FXML
    private Label difficultyLbl;

    @FXML
    private Label alliesLbl;

    @FXML
    private Label statusLbl;

    @FXML
    private RadioButton contestRB;

    private ContestListController parentController;

    private boolean focus = false;

    private SimpleIntegerProperty alliesCurr;

    private SimpleIntegerProperty alliesReq;

    @FXML
    void PickContestAction(MouseEvent event) {
        if(!focus){
            parentController.changeFocus(this);
            setFocus();
        }
    }

    @FXML
    void contestRBAction() {
        if(!focus){
            focus = true;
            parentController.changeFocus(this);
        }
        else contestRB.setSelected(true);
    }


    public void setSingleContestController(Battlefield battlefield, ContestListController parentController) {
        battlefieldLbl.setText(battlefield.getName());
        uBoatLbl.setText(battlefield.getUBoat());
        difficultyLbl.setText(battlefield.getDifficulty());
        alliesCurr.set(battlefield.getAlliesCurr());
        alliesReq.set(battlefield.getAlliesCount());
        alliesLbl.setText(String.format("%d/%d", battlefield.getAlliesCurr(), battlefield.getAlliesCount()));
        statusLbl.setText(battlefield.getStatus());
        this.parentController = parentController;
    }

    public SingleContestController() {
        this.alliesCurr = new SimpleIntegerProperty();
        this.alliesReq = new SimpleIntegerProperty();
    }

    @FXML
    private void initialize(){
        contestRB.disableProperty().bind(Bindings.equal(alliesCurr, alliesReq));
    }

    public void setFocus(){
        focus = !focus;
        if(focus){
            contestRB.setSelected(true);
        }
        else{
            contestRB.setSelected(false);
        }
    }

    public void disableRadioButton(){
        contestRB.setVisible(false);
    }

    public String getBattlefield() {
        return battlefieldLbl.getText();
    }

    public String getUBoat(){
        return uBoatLbl.getText();
    }
}
