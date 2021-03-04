package view;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class DirectionViewController {
    @FXML
    private Text cmdText;
    @FXML
    private Text fileGrid;
    @FXML
    private GridPane processGrid;
    @FXML
    private GridPane cmdGrid;
    @FXML
    private RadioButton processDir;
    @FXML
    private RadioButton fileDir;
    @FXML
    private RadioButton cmdDir;

    private MainAppController mainApp;
    public  void setMainApp(MainAppController mainApp){
        this.mainApp=mainApp;
    }

    @FXML
    private void changeAction(){
        if(processDir.isSelected()){
            processGrid.setVisible(true);
            fileGrid.setVisible(false);
            cmdGrid.setVisible(false);
            cmdText.setVisible(false);
        }else if(fileDir.isSelected()){
            processGrid.setVisible(false);
            fileGrid.setVisible(true);
            cmdGrid.setVisible(false);
            cmdText.setVisible(false);
        }else{
            processGrid.setVisible(false);
            fileGrid.setVisible(false);
            cmdGrid.setVisible(true);
            cmdText.setVisible(true);
        }
    }
}
