/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author Mastadon
 */
public class ArkOptimalWallet extends Application {

    private AnchorPane homeview;
    private Stage mainStage;

    private FXMLHomeViewController homeViewController;

    @Override
    public void start(Stage stage) throws Exception {

        HostServicesProvider.getInstance().init(getHostServices());
        //Parent root = FXMLLoader.load(getClass().getResource("FXMLArkOptimalWalletMainView.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLHomeView.fxml"));
        homeview = fxmlLoader.load();
        homeViewController = (FXMLHomeViewController) fxmlLoader.getController();
        homeViewController.setAppController(this);
        homeViewController.setHomeview(homeview);

        Scene scene = new Scene(homeview);
        stage.setScene(scene);
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/resources/ArkOptimalWallet.png")));
        stage.show();
        this.mainStage = stage;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public FXMLHomeViewController getHomeViewController() {
        return homeViewController;
    }

    public void setHomeViewController(FXMLHomeViewController homeViewController) {
        this.homeViewController = homeViewController;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public void view(Parent parent) {
        this.mainStage.getScene().setRoot(parent);
        this.mainStage.show();

    }

}
