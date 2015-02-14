package com.sleitnick.imagetoico;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import net.sf.image4j.codec.ico.ICOEncoder;



public class ImageToICO extends Application {
	
	private Stage primaryStage;
	private Label testLabel;
	
	/**
	 * Cast an {@link Image} to a {@link BufferedImage}
	 * @param image {@link Image} to cast
	 * @return {@link BufferedImage} result
	 */
	private BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage)image;
		}
		BufferedImage bImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bImage.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bImage;
	}
	
	/**
	 * Resize a given {@link BufferedImage} to the specified width and height
	 * @param image {@link BufferedImage} to resize
	 * @param width Width of resized image
	 * @param height Height of resized image
	 * @return Resized image
	 */
	private BufferedImage resizeImage(BufferedImage image, int width, int height) {
		int hints = Image.SCALE_SMOOTH;
		BufferedImage resizedImage = toBufferedImage(image.getScaledInstance(width, height, hints));
		return resizedImage;
	}
	
	/**
	 * Resize a given {@link BufferedImage} to a series of specified widths and heights
	 * @param image {@link BufferedImage} to resize
	 * @param sizes Tuple of {@link Dimension}s to resize the image to
	 * @return {@link BufferedImage}[] array of resized images
	 */
	private BufferedImage[] resizeImage(BufferedImage image, Dimension... sizes) {
		BufferedImage[] images = new BufferedImage[sizes.length];
		for (int i = 0; i < sizes.length; i++) {
			Dimension size = sizes[i];
			BufferedImage resizedImage = resizeImage(image, size.width, size.height);
			images[i] = resizedImage;
		}
		return images;
	}
	
	public ImageToICO() {
		/*
		BufferedImage image = null;
		BufferedImage[] images;
		try {
			image = ImageIO.read(new File("imgs/addfolder.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		images = resizeImage(image, new Dimension(32, 32), new Dimension(64, 64));
		File file = new File("imgs/test.ico");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Writing ICO image...");
		try {
			//ICOEncoder.write(image, file);
			List<BufferedImage> imagesList = new ArrayList<BufferedImage>();
			for (BufferedImage img : images) {
				imagesList.add(img);
			}
			ICOEncoder.write(imagesList, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("ICO image written");
		Desktop desktop = Desktop.getDesktop();
		if (desktop.isSupported(Desktop.Action.OPEN)) {
			try {
				desktop.open(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		*/
	}
	
	private void createGui() {
		primaryStage.setTitle("Image To ICO");
		primaryStage.setResizable(false);
		
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		
		Text scenetitle = new Text("Image To ICO");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		scenetitle.setId("welcome-text");
		grid.add(scenetitle, 0, 0, 2, 1);
		
		Label userName = new Label("User Name:");
		grid.add(userName, 0, 1);

		TextField userTextField = new TextField();
		grid.add(userTextField, 1, 1);

		Label pw = new Label("Password:");
		grid.add(pw, 0, 2);

		PasswordField pwBox = new PasswordField();
		grid.add(pwBox, 1, 2);
		
		Button btn = new Button("Sign in");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		grid.add(hbBtn, 1, 4);
		
		final Text actiontarget = new Text();
		actiontarget.setFill(Color.FIREBRICK);
		actiontarget.setId("actiontarget");
        grid.add(actiontarget, 1, 6);
        
        btn.setOnAction((ActionEvent actionEvent) -> {
            actiontarget.setFill(Color.FIREBRICK);
            actiontarget.setText("Sign in button pressed");
        });
        
        Scene loginScene = new Scene(grid, 400, 300);
		
		primaryStage.setScene(loginScene);
		primaryStage.getIcons().add(new javafx.scene.image.Image("file:imgs/icon.png"));
        loginScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		createGui();
	}
	
}
