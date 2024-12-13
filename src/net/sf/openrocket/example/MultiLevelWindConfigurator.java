package net.sf.openrocket.example;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import net.sf.openrocket.document.Simulation;
import net.sf.openrocket.gui.SpinnerEditor;
import net.sf.openrocket.gui.adaptors.DoubleModel;
import net.sf.openrocket.gui.components.BasicSlider;
import net.sf.openrocket.gui.components.UnitSelector;
import net.sf.openrocket.gui.print.visitor.Dimension;
import net.sf.openrocket.plugin.Plugin;
import net.sf.openrocket.simulation.extension.AbstractSwingSimulationExtensionConfigurator;
import net.sf.openrocket.unit.UnitGroup;

/**
 * The Swing configuration dialog for the extension.
 * 
 * The abstract implementation provides a ready JPanel using MigLayout
 * to which you can build the dialog.
 */
@Plugin
public class MultiLevelWindConfigurator extends AbstractSwingSimulationExtensionConfigurator<MultiLevelWind> {

	public MultiLevelWindConfigurator() {
		super(MultiLevelWind.class);
	}

	private File SelectedFile;
	private double degToRad = Math.PI / 180;
	private double mToFt = 0.3048;
	private double msToKt = 0.51444445;

	@Override
	protected JComponent getConfigurationComponent(MultiLevelWind extension, Simulation simulation, JPanel panel) {
		extension.setGlobalWindDirection(simulation.getOptions().getWindDirection());
		extension.setGlobalWindSpeed(simulation.getOptions().getWindSpeedAverage());
		createWindow(panel, extension, simulation);
		return panel;
	}

	private void createWindow(JPanel panel, MultiLevelWind extension, Simulation simulation) {
		JFrame frame = new JFrame("MultiLevelWinds File Selection");
		createUI(extension, simulation, panel, frame);
	}

	private void createUI(MultiLevelWind extension, Simulation simulation, final JPanel panel, JFrame frame) {
		// Create a Button for file selection prompt
		JButton button = new JButton("Please Select your file");
		final JLabel label = new JLabel();
		label.setPreferredSize(new java.awt.Dimension(150, 30));

		// Create a CheckboxGroup for data selection
		CheckboxGroup group = new CheckboxGroup();

		// Create checkboxes and associate them with the group
		Font largerFont = new Font("Arial", Font.BOLD, 20);
		Checkbox amCheckbox = new Checkbox("AM", group, true); // Selected by default
		amCheckbox.setFont(largerFont); // Set font to make the checkbox appear larger
		Checkbox pmCheckbox = new Checkbox("PM", group, false);
		pmCheckbox.setFont(largerFont); // Set font to make the checkbox appear larger

		// Create a button to read the selected checkbox
		Button checkButton = new Button("Confirm Selection");

		// Add action listener to the file selection
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Weather Data", new String[] { "json" });
				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.addChoosableFileFilter(filter);
				fileChooser.setFileFilter(filter);
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int option = fileChooser.showOpenDialog(frame);
				if (option == JFileChooser.APPROVE_OPTION) {
					SelectedFile = fileChooser.getSelectedFile();
					label.setText("Selected: " + SelectedFile.getName());
				} else {
					label.setText("Open command canceled");
				}
			}
		});

		// Add action listener to the confirmation button
		checkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Checkbox selected = group.getSelectedCheckbox();
				List<Map<String, Double>> TimeSlot;
				if (SelectedFile != null) {
					if (selected.getLabel() == "AM") {
						TimeSlot = ReadFile(SelectedFile, "AM");
					} else {
						TimeSlot = ReadFile(SelectedFile, "PM");
					}
					if (TimeSlot != null) {
						int i = 1;
						UnitGroup.UNITS_DISTANCE.setDefaultUnit("ft"); // Set distance in feet
						UnitGroup.UNITS_WINDSPEED.setDefaultUnit("kt"); // Set windspeed to knots
						UnitGroup.UNITS_ANGLE.setDefaultUnit("\u00b0"); // Set direction in degrees
						for (Map<String, Double> timeSlotMap : TimeSlot) {
							// Iterate over each key-value pair in the current map
							for (Map.Entry<String, Double> entry : timeSlotMap.entrySet()) {
								switch (entry.getKey()) {
									case "altitude":
										extension.setWindAlt(i, entry.getValue() * mToFt);
										break;
									case "heading":
										extension.setWindDirection(i, entry.getValue() * degToRad);
										break;
									case "wind":
										extension.setWind(i, entry.getValue() * msToKt);
										break;
									case "temperature":
										// Do nothing since this data is not used
										break;
								}
							}
							i++;
						}
						// Redraw panel with new wind data
						panel.removeAll();
						AddWind(extension, simulation, panel, "First Wind", "WindZero");
						AddWind(extension, simulation, panel, "Second Wind", "WindOne");
						AddWind(extension, simulation, panel, "Third Wind", "WindTwo");
						AddWind(extension, simulation, panel, "Fourth Wind", "WindThree");
						AddWind(extension, simulation, panel, "Fifth Wind", "WindFour");
						AddWind(extension, simulation, panel, "Sixth Wind", "WindFive");
						AddWind(extension, simulation, panel, "Seventh Wind", "WindSix");
						AddWind(extension, simulation, panel, "Eighth Wind", "WindSeven");
						AddWind(extension, simulation, panel, "Ninth Wind", "WindEight");
						AddWind(extension, simulation, panel, "Tenth Wind", "WindNine");
						AddWind(extension, simulation, panel, "Eleventh Wind", "WindTen");
						AddWind(extension, simulation, panel, "Twelth Wind", "WindEleven");
						AddWind(extension, simulation, panel, "Thirtheenth Wind", "WindTwelve");
						//Create return to file button eventually
						panel.revalidate(); // Refresh layout
						panel.repaint();
						// Get the parent window to refresh
						Window parentWindow = SwingUtilities.getWindowAncestor(panel);
						if (parentWindow != null) {
							((JDialog) parentWindow).pack(); // Resize the dialog
						}
					} else {
						label.setText("Error: Could not parse JSON.");
					}
				} else {
					label.setText("Error: Please select a file first.");
				}
			}
		});
		panel.add(button);
		panel.add(label, "wrap");
		panel.add(amCheckbox);
		panel.add(pmCheckbox, "wrap");
		panel.add(checkButton, "wrap");
		frame.getContentPane().add(panel, BorderLayout.CENTER);
	}

	private List<Map<String, Double>> ReadFile(File file, String SelectedClass) {
		try {
			FileReader reader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(reader);
	
			try {
				// Parse JSON using Gson
				Gson gson = new Gson();
				List<Map<String, Object>> dataList = gson.fromJson(reader, new TypeToken<List<Map<String, Object>>>() {}.getType());
	
				// Loop through the list to find the selected class (AM or PM)
				for (Map<String, Object> data : dataList) {
					if (data.containsKey(SelectedClass)) {
						// Extract the corresponding list (AM or PM)
						List<Map<String, Double>> content = (List<Map<String, Double>>) data.get(SelectedClass);
						bufferedReader.close(); // Close the reader before returning
						return content;
					}
				}
			} catch (Exception e) {
				// Handle improper file data
				System.err.println("Error reading file: " + e.getMessage());
			}
	
			// Close the reader
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			// Handle the case where the file is not found
			System.err.println("File not found: " + e.getMessage());
		} catch (IOException e) {
			// Handle other IOExceptions
			System.err.println("Error reading file: " + e.getMessage());
		}
		return null;
	}

	private void AddWind(MultiLevelWind extension, Simulation simulation, JPanel panel, String label,
			String configKey) {
		// Wind altitude
		panel.add(new JLabel("Altitude:"));
		DoubleModel altModel = new DoubleModel(extension, configKey + "Alt", UnitGroup.UNITS_DISTANCE,
				UnitGroup.UNITS_DISTANCE.fromString("330ft"));

		JSpinner altSpin = new JSpinner(altModel.getSpinnerModel());
		altSpin.setEditor(new SpinnerEditor(altSpin));
		panel.add(altSpin, "w 75lp!");

		UnitSelector altUnit = new UnitSelector(altModel);
		panel.add(altUnit, "w 25");

		// Wind speed
		panel.add(new JLabel("       Speed:"));
		DoubleModel speedModel = new DoubleModel(extension, configKey, UnitGroup.UNITS_WINDSPEED);

		JSpinner speedSpin = new JSpinner(speedModel.getSpinnerModel());
		speedSpin.setEditor(new SpinnerEditor(speedSpin));
		panel.add(speedSpin, "w 65lp!");

		UnitSelector speedUnit = new UnitSelector(speedModel);
		panel.add(speedUnit, "w 25");

		// Wind direction
		panel.add(new JLabel("       Direction:"));
		DoubleModel dirModel = new DoubleModel(extension, configKey + "Direction", UnitGroup.UNITS_ANGLE,
				UnitGroup.UNITS_ANGLE.fromString("0"));

		JSpinner dirSpin = new JSpinner(dirModel.getSpinnerModel());
		dirSpin.setEditor(new SpinnerEditor(dirSpin));
		panel.add(dirSpin, "w 65lp!");

		BasicSlider dirSlider = new BasicSlider(dirModel.getSliderModel(0, UnitGroup.UNITS_ANGLE.fromString("360")));
		panel.add(dirSlider, "w 200lp, wrap");
	}

}
