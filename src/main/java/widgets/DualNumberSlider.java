package widgets;

import edu.wpi.first.shuffleboard.api.widget.SimpleAnnotatedWidget;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class DualNumberSlider extends SimpleAnnotatedWidget{
    @FXML
    protected Pane _thePane;

    @FXML
    protected Label _negNum;
    @FXML
    protected Label _posNum;

    int _neg = 0;
    int _pos = 0;

    public Pane getView() {
        return _thePane;
    }

}

