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

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.IDataExtraction;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaFiltrationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaPreprocessingData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.managers.SelectionManagerSample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.runnable.PcaInputRunnable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.BatchProcessWizardDialog;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.IPcaInputWizard;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.PcaChromatogramsMSDInputWizard;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.PcaPeaksInputWizard;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.untility.PcaColorGroup;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

public class PCAEditorController {

	@FXML // fx:id="cColor"
	private TableColumn<Sample, String> cColor; // Value injected by FXMLLoader
	@FXML // fx:id="cGroupNames"
	private TableColumn<Sample, String> cGroupNames; // Value injected by FXMLLoader
	@FXML
	private Label cLabelNumblerSelectedSamples;
	@FXML // fx:id="cSelections"
	private TableColumn<Sample, Boolean> cSelections; // Value injected by FXMLLoader
	@FXML // fx:id="cTableSamples"
	private TableView<Sample> cTableSamples; // Value injected by FXMLLoader
	private Consumer<Samples> inputSamples;
	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;
	private Map<String, Color> mapGroupColor = new HashMap<>();
	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;
	private Optional<Samples> samples;

	public PCAEditorController() {
		samples = Optional.empty();
	}

	public Optional<Samples> getSamples() {

		return samples;
	}

	public List<Sample> getSelectedSamples() {

		return new ArrayList<>(cTableSamples.getSelectionModel().getSelectedItems());
	}

	@FXML
	void handeChangeGroupName(TableColumn.CellEditEvent<Sample, String> event) {

		String newGroupName = event.getNewValue();
		String oldGroupName = event.getOldValue();
		Sample sample = event.getRowValue();
		if(newGroupName != null) {
			newGroupName = newGroupName.trim();
			if(!newGroupName.equals(oldGroupName)) {
				if(!newGroupName.isEmpty()) {
					sample.setGroupName(newGroupName);
				} else {
					sample.setGroupName(null);
				}
				updateColorMap();
			}
		} else {
			if(newGroupName != oldGroupName) {
				sample.setGroupName(newGroupName);
				updateColorMap();
			}
		}
		cTableSamples.refresh();
	}

	@FXML
	void handlerDeselectAll(ActionEvent event) {

		if(samples.isPresent()) {
			samples.get().getSampleList().forEach(s -> s.setSelected(false));
		}
	}

	@FXML
	void handlerKeyPress(KeyEvent event) {

		if(event.getCode() == KeyCode.ESCAPE) {
			cTableSamples.getSelectionModel().clearSelection();
			SelectionManagerSample.getInstance().getSelection().clear();
		}
	}

	@FXML
	void handlerLoadPeaks(ActionEvent event) {

		try {
			openWizardPcaInput(new PcaPeaksInputWizard());
		} catch(InvocationTargetException | InterruptedException e) {
		}
	}

	@FXML
	void handlerLoadScans(ActionEvent event) {

		try {
			openWizardPcaInput(new PcaChromatogramsMSDInputWizard());
		} catch(InvocationTargetException | InterruptedException e) {
		}
	}

	@FXML
	void handlerSelectAll(ActionEvent event) {

		if(samples.isPresent()) {
			samples.get().getSampleList().forEach(s -> s.setSelected(true));
		}
	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {

		assert cTableSamples != null : "fx:id=\"cTableSamples\" was not injected: check your FXML file 'PCAEditor.fxml'.";
		assert cSelections != null : "fx:id=\"cSelections\" was not injected: check your FXML file 'PCAEditor.fxml'.";
		assert cColor != null : "fx:id=\"cColor\" was not injected: check your FXML file 'PCAEditor.fxml'.";
		cSelections.setCellValueFactory(new Callback<CellDataFeatures<Sample, Boolean>, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(CellDataFeatures<Sample, Boolean> param) {

				Sample sample = param.getValue();
				SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(sample.isSelected());
				// Note: singleCol.setOnEditCommit(): Not work for
				// CheckBoxTableCell.
				// When "Single?" column change.cell
				booleanProp.addListener(new ChangeListener<Boolean>() {

					@Override
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

						if(newValue != null) {
							sample.setSelected(newValue);
						}
					}
				});
				return booleanProp;
			}
		});
		cSelections.setCellFactory(new Callback<TableColumn<Sample, Boolean>, //
				TableCell<Sample, Boolean>>() {

			@Override
			public TableCell<Sample, Boolean> call(TableColumn<Sample, Boolean> p) {

				CheckBoxTableCell<Sample, Boolean> cell = new CheckBoxTableCell<Sample, Boolean>();
				cell.setAlignment(Pos.CENTER);
				return cell;
			}
		});
		cColor.setCellFactory(param -> {
			final TableCell<Sample, String> cell = new TableCell<Sample, String>() {

				final Rectangle r = new Rectangle(20, 20);

				@Override
				public void updateItem(String item, boolean empty) {

					super.updateItem(item, empty);
					if(empty) {
						setGraphic(null);
						setText(null);
					} else {
						Color c = mapGroupColor.get(item);
						if(c != null) {
							r.setFill(c);
							setGraphic(r);
							setText(null);
						}
					}
				}
			};
			cell.setAlignment(Pos.CENTER);
			return cell;
		});
		cTableSamples.setRowFactory(new Callback<TableView<Sample>, TableRow<Sample>>() {

			@Override
			public TableRow<Sample> call(TableView<Sample> tableView2) {

				final TableRow<Sample> row = new TableRow<>();
				row.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {

						event.getTarget();
						if(event.getClickCount() == 1) {
							final int index = row.getIndex();
							if(index < 0 || index >= cTableSamples.getItems().size()) {
								cTableSamples.getSelectionModel().clearSelection();
								SelectionManagerSample.getInstance().getSelection().clear();
								event.consume();
							}
						}
					}
				});
				return row;
			}
		});
		cTableSamples.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Sample>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends Sample> c) {

				List<? extends Sample> samples = c.getList();
				if(!samples.isEmpty()) {
					Sample s = samples.get(0);
					if(!SelectionManagerSample.getInstance().getSelection().contains(s)) {
						SelectionManagerSample.getInstance().getSelection().setAll(s);
					}
				}
			}
		});
		updateNumerSeletedSamples();
	}

	private int openWizardPcaInput(IPcaInputWizard wizard) throws InvocationTargetException, InterruptedException {

		BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(Display.getDefault().getActiveShell(), wizard);
		int status = wizardDialog.open();
		if(status == Window.OK) {
			SelectionManagerSamples.getInstance().getElements().remove(samples);
			SelectionManagerSamples.getInstance().getSelection().clear();
			PcaFiltrationData pcaFiltrationData = wizard.getPcaFiltrationData();
			PcaPreprocessingData pcaPreprocessingData = wizard.getPcaPreprocessingData();
			IDataExtraction pcaExtractionData = wizard.getPcaExtractionData();
			/*
			 * Run the process.
			 */
			PcaInputRunnable runnable = new PcaInputRunnable(pcaExtractionData, pcaFiltrationData, pcaPreprocessingData);
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			/*
			 * Calculate the results and show the score plot page.
			 */
			monitor.run(true, true, runnable);
			this.samples = Optional.of(runnable.getSamples());
			SelectionManagerSamples.getInstance().getElements().add(samples.get());
			SelectionManagerSamples.getInstance().getSelection().add(samples.get());
			updateColorMap();
			updateNumerSeletedSamples();
			inputSamples.accept(samples.get());
			cTableSamples.setItems(FXCollections.observableList(samples.get().getSampleList()));
		}
		return status;
	}

	public void seletedSample(Sample sample) {

		if(!cTableSamples.getSelectionModel().getSelectedItems().contains(sample)) {
			cTableSamples.getSelectionModel().select(sample);
			cTableSamples.scrollTo(sample);
		}
	}

	public void setSamplesConsumer(Consumer<Samples> consumer) {

		this.inputSamples = consumer;
	}

	private void updateColorMap() {

		mapGroupColor = PcaColorGroup.getColorJavaFx(samples.get().getSampleList().stream().map(s -> s.getGroupName()).collect(Collectors.toSet()));
	}

	private void updateNumerSeletedSamples() {

		long selected = 0;
		int totalSamples = 0;
		if(samples.isPresent()) {
			selected = samples.get().getSampleList().stream().filter(s -> s.isSelected()).count();
			totalSamples = samples.get().getSampleList().size();
		}
		cLabelNumblerSelectedSamples.setText("Selected: " + selected + " from " + totalSamples + " samples");
	}
}
