package com.sleitnick.imagetoico;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import net.sf.image4j.codec.ico.ICOEncoder;



public class ImageToICO extends Application {
	
	private static final int[] ICON_SIZES = {
		16, 32, 48, 64, 96, 128, 192,
		256, 512, -1
	};
	
	private Stage primaryStage;
	private Label fileChosen;
	private Button convertButton;
	private File selectedFile = null;
	private FileChooser icoSave;
	
	private CheckBox[] checkboxSizes = new CheckBox[ICON_SIZES.length];
	
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
	
	private void setSelectedFile(File file, boolean allowNull) {
		if (file == null && !allowNull) {
			return;
		}
		selectedFile = file;
		if (file == null) {
			fileChosen.setText("");
		} else {
			fileChosen.setText(selectedFile.getAbsolutePath());
		}
		for (CheckBox cb : checkboxSizes) {
			cb.setVisible(file != null);
		}
		for (int i = 0; i < checkboxSizes.length; i++) {
			CheckBox cb = checkboxSizes[i];
			int size = ICON_SIZES[i];
			cb.setVisible(file != null);
			if (size == -1 && file != null) {
				Dimension imgSize = getImageFileSize(file);
				cb.setText("Original Size (" + imgSize.width + "x" + imgSize.height + ")");
			}
		}
		convertButton.setVisible(file != null);
	}
	
	private void convertAndSave(File file, Dimension... sizes) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		icoSave.setInitialDirectory(file.getParentFile());
		{
			String initName = file.getName();
			if (initName.contains(".")) {
				initName = initName.substring(0, initName.lastIndexOf("."));
			}
			icoSave.setInitialFileName(initName);
		}
		File saveFile = icoSave.showSaveDialog(primaryStage);
		BufferedImage images[] = resizeImage(image, sizes);
		if (saveFile != null) {
			if (!saveFile.getName().endsWith(".ico")) {
				saveFile = new File(saveFile.getAbsolutePath() + ".ico");
			}
		}
		if (saveFile == null) {
			
		} else {
			try {
				saveFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			List<BufferedImage> imagesList = Arrays.asList(images);
			try {
				FileOutputStream out = new FileOutputStream(saveFile);
				ICOEncoder.write(imagesList, out);
				out.close();
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				if (desktop.isSupported(Desktop.Action.OPEN)) {
					try {
						desktop.open(saveFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private Dimension getImageFileSize(File file) {
		javafx.scene.image.Image img = new javafx.scene.image.Image("file:" + file.getPath());
		Dimension size = new Dimension((int)img.getWidth(), (int)img.getHeight());
		return size;
	}
	
	private Dimension[] getSelectedDimensions() {
		List<Dimension> dimensions = new ArrayList<Dimension>();
		for (int i = 0; i < checkboxSizes.length; i++) {
			CheckBox cb = checkboxSizes[i];
			if (cb.isSelected()) {
				int size = ICON_SIZES[i];
				if (size == -1) {
					if (selectedFile != null && selectedFile.exists()) {
						Dimension s = getImageFileSize(selectedFile);
						dimensions.add(new Dimension(s.width, s.height));
					}
				} else {
					dimensions.add(new Dimension(size, size));
				}
			}
		}
		Dimension[] selectedDimensions = new Dimension[dimensions.size()];
		dimensions.toArray(selectedDimensions);
		return selectedDimensions;
	}
	
	private void createGui() {
		primaryStage.setTitle("Image To ICO");
		primaryStage.getIcons().add(new javafx.scene.image.Image("com/sleitnick/imagetoico/icon.png"));
		primaryStage.setResizable(false);
		
		FileChooser fileChooser = new FileChooser();
		{
			File initDir = new File(System.getProperty("user.home"));
			List<String> dirFiles = Arrays.asList(initDir.list());
			if (dirFiles.contains("Pictures")) {
				initDir = new File(initDir, "Pictures");
			} else if (dirFiles.contains("Documents")) {
				initDir = new File(initDir, "Documents");
			}
			fileChooser.setInitialDirectory(initDir);
			fileChooser.getExtensionFilters().addAll(
					new ExtensionFilter("All Images", "*.png", "*.jpg", "*.jpeg", "*.bmp")
			);
		}
		
		icoSave = new FileChooser();
		icoSave.setInitialDirectory(fileChooser.getInitialDirectory());
		icoSave.getExtensionFilters().addAll(
				new ExtensionFilter("Icon Image", "*.ico")
		);
		
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		
		Text scenetitle = new Text("Image To ICO");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		scenetitle.setId("welcome-text");
		grid.add(scenetitle, 0, 0, 2, 1);
		
		Button chooseImageButton = new Button("Choose Image...");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.CENTER);
		hbBtn.getChildren().add(chooseImageButton);
		grid.add(hbBtn, 0, 2, 2, 2);
		
		fileChosen = new Label("This is a test");
		fileChosen.setAlignment(Pos.CENTER);
		fileChosen.setTextAlignment(TextAlignment.CENTER);
		grid.add(fileChosen, 0, 4, 2, 4);
		
		convertButton = new Button("Convert");
		convertButton.setVisible(false);
		HBox hbBtn2 = new HBox(10);
		hbBtn2.setAlignment(Pos.CENTER);
		hbBtn2.getChildren().add(convertButton);
		grid.add(hbBtn2, 0, 11, 2, 12);
		
		for (int i = 0; i < ICON_SIZES.length; i++) {
			int iconSize = ICON_SIZES[i];
			CheckBox cb = new CheckBox(iconSize == -1 ? "Original Size" : iconSize + "x" + iconSize);
			int column = (i >= ICON_SIZES.length / 2 ? 1 : 0);
			int row = (column == 0 ? (6 + i) : 6 + (i - (ICON_SIZES.length / 2)));
			grid.add(cb, column, row + 2);
			checkboxSizes[i] = cb;
			cb.setVisible(false);
			if (iconSize == -1) {
				cb.setSelected(true);
			}
		}
        
		chooseImageButton.setOnAction((ActionEvent actionEvent) -> {
        	File file = fileChooser.showOpenDialog(primaryStage);
        	setSelectedFile(file, false);
        });
		
		convertButton.setOnAction((ActionEvent actionEvent) -> {
			if (selectedFile != null) {
				convertAndSave(selectedFile, getSelectedDimensions());
			}
		});
        
        Scene loginScene = new Scene(grid, 400, 350);
		
		primaryStage.setScene(loginScene);
		//primaryStage.getIcons().add(new javafx.scene.image.Image("file:imgs/icon.png"));
        loginScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        
        setSelectedFile(null, true);
        
		primaryStage.show();
	}
	
	public ImageToICO() {
		
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
