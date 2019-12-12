import functions.Function;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;

public class Main extends Application {


    NumberTextField A;
    NumberTextField B;
    NumberTextField C;
    NumberTextField D;
    NumberTextField alphaField;
    NumberTextField betaField;
    NumberTextField gammaField;
    NumberTextField deltaField;
    NumberTextField fiField;
    NumberTextField N;
    LineChart<Number, Number> chart;
    HBox root;

    NumberTextField step;

    Function dfx = new Function() {
        @Override
        public double getValueAt(double x, double y) {
            double a = alphaField.getNumber();
            double b = betaField.getNumber();
            double g = gammaField.getNumber();
            double step = Main.this.step.getNumber();

            return -(a * x * x + b * y * y + g) * step + x;
        }
    };

    Function dfy = new Function() {
        @Override
        public double getValueAt(double x, double y) {
            double d = deltaField.getNumber();
            double f = fiField.getNumber();
            double step = Main.this.step.getNumber();

            return -(d * x + f) * step + y;
        }
    };

    Function fx = new Function() {
        @Override
        public double getValueAt(double x, double y) {
            double a = alphaField.getNumber();
            double b = betaField.getNumber();
            double g = gammaField.getNumber();
            double step = Main.this.step.getNumber();

            return (a * x * x + b * y * y + g) * step + x;
        }
    };

    Function fy = new Function() {
        @Override
        public double getValueAt(double x, double y) {
            double d = deltaField.getNumber();
            double f = fiField.getNumber();
            double step = Main.this.step.getNumber();

            return (d * x + f) * step + y;
        }
    };


    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setScene(initScene());
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Scene initScene() {
        root = new HBox();

        VBox leftPane = new VBox();
        leftPane.setMinWidth(300);
        leftPane.setPadding(new Insets(10, 10, 10, 10));
        leftPane.setSpacing(10);
        Label wParams = new Label("Window parameters");

        GridPane wGrid = new GridPane();
        wGrid.setHgap(10);
        wGrid.setVgap(10);
        Label left = new Label("Left:");
        A = new NumberTextField(-10);
        Label top = new Label("Top:");
        B = new NumberTextField(10);
        Label right = new Label("Right:");
        C = new NumberTextField(10);
        Label bottom = new Label("Bottom:");
        D = new NumberTextField(-10);
        wGrid.add(left, 0, 0);
        wGrid.add(A, 1, 0);
        wGrid.add(right, 0, 1);
        wGrid.add(C, 1, 1);
        wGrid.add(bottom, 0, 2);
        wGrid.add(D, 1, 2);
        wGrid.add(top, 0, 3);
        wGrid.add(B, 1, 3);

        Label fParams = new Label("Function parameters");

        GridPane fGrid = new GridPane();
        fGrid.setHgap(10);
        fGrid.setVgap(10);
        Label alphaL = new Label("Alpha:");
        alphaField = new NumberTextField(1);
        Label betaL = new Label("Beta:");
        betaField = new NumberTextField(2);
        Label gammaL = new Label("Gamma:");
        gammaField = new NumberTextField(3);
        Label deltaL = new Label("Delta:");
        deltaField = new NumberTextField(4);
        Label fiL = new Label("Fi:");
        fiField = new NumberTextField(5);
        Label NL = new Label("N:");
        N = new NumberTextField(10000);
        fGrid.add(alphaL, 0, 0);
        fGrid.add(alphaField, 1, 0);
        fGrid.add(betaL, 0, 1);
        fGrid.add(betaField, 1, 1);
        fGrid.add(gammaL, 0, 2);
        fGrid.add(gammaField, 1, 2);
        fGrid.add(deltaL, 0, 3);
        fGrid.add(deltaField, 1, 3);
        fGrid.add(fiL, 0, 4);
        fGrid.add(fiField, 1, 4);
        fGrid.add(NL, 0, 5);
        fGrid.add(N, 1, 5);

        GridPane res = new GridPane();
        step = new NumberTextField(0.001);
        res.add(new Label("Step = "), 0, 0);
        res.add(step, 1, 0);


        Scene scene = new Scene(root, 1300, 700);

        Button commit = new Button("Clean");
        commit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                initPlot();
            }
        });

        Label name = new Label("ИВТ-42БО Бондарь Алеся.");
        name.setAlignment(Pos.BOTTOM_LEFT);

        leftPane.getChildren().addAll(wParams, wGrid, fParams, fGrid, res, commit, name);

        root.getChildren().add(leftPane);

        initPlot();

        return scene;
    }

    public void initPlot() {

        if (root.getChildren().contains(chart)) {
            root.getChildren().remove(chart);
        }

        chart = new LineChart<Number, Number>(new NumberAxis(A.getNumber(), C.getNumber(), (C.getNumber() - A.getNumber()) / 10), new NumberAxis(D.getNumber(), B.getNumber(), (B.getNumber() - D.getNumber()) / 10));
        chart.setMinWidth(1000);
        chart.setCreateSymbols(false);
        //chart.getStylesheets().add(Main.class.getResource("res/ch.css").toExternalForm());
        root.getChildren().add(chart);

        chart.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = chart.getXAxis().getValueForDisplay(event.getX()).doubleValue();
                double y = chart.getYAxis().getValueForDisplay(event.getY()).doubleValue();
                drawFunction(x, y);
            }
        });
    }

    public void drawFunction(double x, double y) {
        double step = this.step.getNumber();
        ArrayList<Pair<Double, Double>> data = new ArrayList<>();
        data.add(new Pair<>(x, y));
        double xn = x;
        double yn = y;

        for (int n = 1; n < N.getNumber()/2; n++) {
            double xt = dfx.getValueAt(xn, yn);
            double yt = dfy.getValueAt(xn, yn);
            xn = xt;
            yn = yt;
            if (Double.isInfinite(xn) || Double.isInfinite(yn)){
                break;
            }
            data.add(new Pair<>(xn, yn));
        }

        xn = x;
        yn = y;

        for (int n = 1; n < N.getNumber()/2; n++) {
            double xt = fx.getValueAt(xn, yn);
            double yt = fy.getValueAt(xn, yn);
            xn = xt;
            yn = yt;
            if (Double.isInfinite(xn) || Double.isInfinite(yn)){
                break;
            }
            data.add(new Pair<>(xn, yn));
        }

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int i = 0; i < data.size(); i++) {
            Number xf = data.get(i).getKey();
            Number yf = data.get(i).getValue();
            series.getData().add(new XYChart.Data<>(xf, yf));
        }
        chart.getData().add(series);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
