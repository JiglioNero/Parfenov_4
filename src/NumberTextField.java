import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;


public class NumberTextField extends TextField {
    public NumberTextField() {
        init();
    }

    public NumberTextField(double value) {
        super(String.valueOf(value));
        init();
    }

    private void init(){
        setMaxWidth(50);
    }

    public double getNumber(){
        String t = getText();
        try{
            return Double.parseDouble(t);
        }catch (Exception e){
            return 0;
        }
    }
}
