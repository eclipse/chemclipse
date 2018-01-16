/*******************************************************************************
 * Copyright (c) 2018 jan.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * jan - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts.controllers;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.SampleData;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class PCAEditorController {

	@FXML // fx:id="deselectAll"
	private Button deselectAll; // Value injected by FXMLLoader
	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;
	@FXML // fx:id="numberSelectedSamples"
	private Label numberSelectedSamples; // Value injected by FXMLLoader
	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;
	@FXML // fx:id="sampleDescription"
	private TableView<?> sampleDescription; // Value injected by FXMLLoader
	@FXML // fx:id="sampleGroupNam0es"
	private TableColumn<Sample, String> sampleGroupNam0es; // Value injected by FXMLLoader
	@FXML // fx:id="sampleNames"
	private TableColumn<Sample, String> sampleNames; // Value injected by FXMLLoader
	private Optional<? extends ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>>> samples;
	@FXML // fx:id="sampleSelections"
	private TableColumn<Sample, Boolean> sampleSelections; // Value injected by FXMLLoader
	@FXML // fx:id="selectAll"
	private Button selectAll; // Value injected by FXMLLoader
	@FXML // fx:id="selectTableType"
	private ComboBox<String> selectTableType; // Value injected by FXMLLoader
	@FXML // fx:id="tableSamples"
	private TableView<ISample<? extends ISampleData>> tableSamples; // Value injected by FXMLLoader
	private Sample actualSelectedSample;

	public PCAEditorController() {
		samples = Optional.empty();
	}

	public Optional<? extends ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>>> getSamples() {

		return samples;
	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {

		assert selectAll != null : "fx:id=\"selectAll\" was not injected: check your FXML file 'PCAEditor.fxml'.";
		assert deselectAll != null : "fx:id=\"deselectAll\" was not injected: check your FXML file 'PCAEditor.fxml'.";
		assert tableSamples != null : "fx:id=\"tableSamples\" was not injected: check your FXML file 'PCAEditor.fxml'.";
		assert sampleSelections != null : "fx:id=\"sampleSelections\" was not injected: check your FXML file 'PCAEditor.fxml'.";
		assert sampleNames != null : "fx:id=\"sampleNames\" was not injected: check your FXML file 'PCAEditor.fxml'.";
		assert sampleGroupNam0es != null : "fx:id=\"sampleGroupNam0es\" was not injected: check your FXML file 'PCAEditor.fxml'.";
		assert selectTableType != null : "fx:id=\"selectTableType\" was not injected: check your FXML file 'PCAEditor.fxml'.";
		assert sampleDescription != null : "fx:id=\"sampleDescription\" was not injected: check your FXML file 'PCAEditor.fxml'.";
		assert numberSelectedSamples != null : "fx:id=\"numberSelectedSamples\" was not injected: check your FXML file 'PCAEditor.fxml'.";
	}

	public void setSamples(ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>> samples) {

		this.samples = Optional.of(samples);
		tableSamples.setItems(FXCollections.observableArrayList(samples.getSampleList()));
	}

	private void createDataTable() {

		sampleDescription.getColumns().clear();
		TableColumn<SampleData, Double> col1 = new TableColumn<>("Retention Time");
		col1.setCellFactory(c -> new TableCell<SampleData, Double>() {

			@Override
			public void updateIndex(int i) {

				if(samples.isPresent()) {
				}
			};
		});
		TableColumn<SampleData, SampleData> rawData = new TableColumn<>("#");
		rawData.setMinWidth(20);
		rawData.setCellValueFactory(new Callback<CellDataFeatures<SampleData, SampleData>, ObservableValue<SampleData>>() {

			@Override
			public ObservableValue<SampleData> call(CellDataFeatures<SampleData, SampleData> p) {

				return new ReadOnlyObjectWrapper<>(p.getValue());
			}
		});
		rawData.setCellFactory(new Callback<TableColumn<SampleData, SampleData>, TableCell<SampleData, SampleData>>() {

			@Override
			public TableCell<SampleData, SampleData> call(TableColumn<SampleData, SampleData> param) {

				return new TableCell<SampleData, SampleData>() {

					@Override
					protected void updateItem(SampleData item, boolean empty) {

						super.updateItem(item, empty);
						if(this.getTableRow() != null && item != null) {
							//setText(samples.get().getVariables().get(this.getTableRow().getIndex()));
						}
					}
				};
			}
		});
		
		TableColumn<SampleData, SampleData> data = new TableColumn<>("#");
		data.setMinWidth(20);
		data.setCellValueFactory(new Callback<CellDataFeatures<SampleData, SampleData>, ObservableValue<SampleData>>() {

			@Override
			public ObservableValue<SampleData> call(CellDataFeatures<SampleData, SampleData> p) {
				
				return new ReadOnlyObjectWrapper<>(p.getValue());
			}
		});
		data.setCellFactory(new Callback<TableColumn<SampleData, SampleData>, TableCell<SampleData, SampleData>>() {

			@Override
			public TableCell<SampleData, SampleData> call(TableColumn<SampleData, SampleData> param) {

				return new TableCell<SampleData, SampleData>() {

					@Override
					protected void updateItem(SampleData item, boolean empty) {

						super.updateItem(item, empty);
						if(this.getTableRow() != null && item != null) {
							setText(this.getTableRow().getIndex() + "");
						}
					}
				};
			}
		});
	}
}
