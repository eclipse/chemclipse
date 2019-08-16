/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts.controllers;

import java.util.List;
import java.util.function.Supplier;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaPreprocessingData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.AbstractCentering;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.CenteringMean;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.CenteringMedian;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ICentering;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.IDataModificator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.INormalization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.IPreprocessing;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.IScaling;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ITransformation;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.MeanValuesReplacer;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.MedianValuesReplacer;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.Normalization1Norm;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.Normalization2Norm;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.NormalizationInfNorm;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ScalingAuto;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ScalingPareto;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ScalingRange;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ScalingVast;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.SmallValuesReplacer;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.TransformationLOG10;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.TransformationPower;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaSettingsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISampleVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISamplesVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class PreprocessingController {

	private final String TRANSFORMATION_LOG10 = "Log Transformation 10log(x)";
	private final String TRANSFORMATION_POWER = "Power Transformation";
	private final String CENTERING_MEAN = "Mean Centering";
	private final String CENTERING_MEDIAN = "Median Centering";
	private final String NORMALIZATION_1_NORM = "Normalization 1-norm";
	private final String NORMALIZATION_2_NORM = "Normalization 2-norm";
	private final String NORMALIZATION_INF_NORM = "Normalization inf-norm";
	private final String SCALING_AUTO = "Autoscaling";
	private final String SCALING_LEVEL = "Range Scaling";
	private final String SCALING_PARETO = "Pareto Scaling";
	private final String SCALING_RANGE = "Vast Scaling";
	private final String SCALING_VAST = "Level Scaling";
	@FXML // fx:id="bTransformData"
	private CheckBox bTransformData; // Value injected by FXMLLoader
	@FXML // fx:id="cNormalizeData"
	private ComboBox<ComboItem<INormalization>> cNormalizeData; // Value injected by FXMLLoader
	@FXML // fx:id="cReplaceEmptyValue"
	private ComboBox<ComboItem<IDataModificator>> cReplaceEmptyValue; // Value injected by FXMLLoader
	@FXML // fx:id="bNormalizeData"
	private CheckBox bNormalizeData; // Value injected by FXMLLoader
	@FXML // fx:id="cTransformData"
	private ComboBox<ComboItem<ITransformation>> cTransformData; // Value injected by FXMLLoader
	@FXML // fx:id="bCenterData"
	private CheckBox bCenterData; // Value injected by FXMLLoader
	@FXML // fx:id="cCenterData"
	private ComboBox<ComboItem<ICentering>> cCenterData; // Value injected by FXMLLoader
	@FXML // fx:id="bScaleData"
	private CheckBox bScaleData; // Value injected by FXMLLoader
	@FXML // fx:id="cScaleData"
	private ComboBox<ComboItem<IScaling>> cScaleData; // Value injected by FXMLLoader
	@FXML // fx:id="bRemoveVariables"
	private CheckBox bRemoveVariables; // Value injected by FXMLLoader
	@FXML
	private CheckBox bUseOnlySelectedVariables;
	//
	private int centeringType;
	private PcaPreprocessingData preprecessing;
	private volatile boolean isSetPreprocessing;
	private IPcaSettingsVisualization pcaSettings;
	private ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization> samples;

	private class ComboItem<Preprocessing extends IPreprocessing> {

		private String name;
		private String pathPicture;
		private Supplier<Preprocessing> preprecessing;

		public ComboItem(String name, String pathPicture, Supplier<Preprocessing> preprecessing) {

			super();
			this.name = name;
			this.pathPicture = pathPicture;
			this.preprecessing = preprecessing;
			isSetPreprocessing = false;
		}
	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {

		assert bTransformData != null : "fx:id=\"bTransformData\" was not injected: check your FXML file 'Preprecessing.fxml'.";
		assert cNormalizeData != null : "fx:id=\"cNormalizeData\" was not injected: check your FXML file 'Preprecessing.fxml'.";
		assert cReplaceEmptyValue != null : "fx:id=\"cReplaceEmptyValue\" was not injected: check your FXML file 'Preprecessing.fxml'.";
		assert bNormalizeData != null : "fx:id=\"bNormalizeData\" was not injected: check your FXML file 'Preprecessing.fxml'.";
		assert cTransformData != null : "fx:id=\"cTransformData\" was not injected: check your FXML file 'Preprecessing.fxml'.";
		assert bCenterData != null : "fx:id=\"bCenterData\" was not injected: check your FXML file 'Preprecessing.fxml'.";
		assert cCenterData != null : "fx:id=\"cCenterData\" was not injected: check your FXML file 'Preprecessing.fxml'.";
		assert bScaleData != null : "fx:id=\"bScaleData\" was not injected: check your FXML file 'Preprecessing.fxml'.";
		assert cScaleData != null : "fx:id=\"cScaleData\" was not injected: check your FXML file 'Preprecessing.fxml'.";
		assert bRemoveVariables != null : "fx:id=\"bRemoveVariables\" was not injected: check your FXML file 'Preprecessing.fxml'.";
		/*
		 * normalization
		 */
		cNormalizeData.getItems().add(new ComboItem<>(NORMALIZATION_2_NORM, "", () -> new Normalization2Norm()));
		cNormalizeData.getItems().add(new ComboItem<>(NORMALIZATION_1_NORM, "", () -> new Normalization1Norm()));
		cNormalizeData.getItems().add(new ComboItem<>(NORMALIZATION_INF_NORM, "", () -> new NormalizationInfNorm()));
		cNormalizeData.valueProperty().addListener(new ChangeListener<ComboItem<INormalization>>() {

			@Override
			public void changed(ObservableValue<? extends ComboItem<INormalization>> observable, ComboItem<INormalization> oldValue, ComboItem<INormalization> newValue) {

				dataPreprocessing();
			}
		});
		cNormalizeData.setCellFactory(new Callback<ListView<ComboItem<INormalization>>, ListCell<ComboItem<INormalization>>>() {

			@Override
			public ListCell<ComboItem<INormalization>> call(ListView<ComboItem<INormalization>> p) {

				return new ListCell<ComboItem<INormalization>>() {

					@Override
					protected void updateItem(ComboItem<INormalization> item, boolean empty) {

						super.updateItem(item, empty);
						if(item == null) {
							return;
						}
						setText(item.name);
					}
				};
			}
		});
		cNormalizeData.setConverter(new StringConverter<ComboItem<INormalization>>() {

			@Override
			public String toString(ComboItem<INormalization> item) {

				if(item != null) {
					return item.name;
				}
				return "";
			}

			@Override
			public ComboItem<INormalization> fromString(String string) {

				return null;
			}
		});
		bNormalizeData.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				cNormalizeData.setDisable(!newValue);
				dataPreprocessing();
			}
		});
		/*
		 * scaling
		 */
		cScaleData.getItems().add(new ComboItem<>(SCALING_AUTO, "", () -> new ScalingAuto(centeringType)));
		cScaleData.getItems().add(new ComboItem<>(SCALING_RANGE, "", () -> new ScalingRange(centeringType)));
		cScaleData.getItems().add(new ComboItem<>(SCALING_PARETO, "", () -> new ScalingPareto(centeringType)));
		cScaleData.getItems().add(new ComboItem<>(SCALING_VAST, "", () -> new ScalingVast(centeringType)));
		cScaleData.getItems().add(new ComboItem<>(SCALING_LEVEL, "", () -> new ScalingRange(centeringType)));
		cScaleData.valueProperty().addListener(new ChangeListener<ComboItem<IScaling>>() {

			@Override
			public void changed(ObservableValue<? extends ComboItem<IScaling>> observable, ComboItem<IScaling> oldValue, ComboItem<IScaling> newValue) {

				dataPreprocessing();
			}
		});
		cScaleData.setCellFactory(new Callback<ListView<ComboItem<IScaling>>, ListCell<ComboItem<IScaling>>>() {

			@Override
			public ListCell<ComboItem<IScaling>> call(ListView<ComboItem<IScaling>> p) {

				return new ListCell<ComboItem<IScaling>>() {

					@Override
					protected void updateItem(ComboItem<IScaling> item, boolean empty) {

						super.updateItem(item, empty);
						if(item == null) {
							return;
						}
						setText(item.name);
					}
				};
			}
		});
		cScaleData.setConverter(new StringConverter<ComboItem<IScaling>>() {

			@Override
			public String toString(ComboItem<IScaling> item) {

				if(item != null) {
					return item.name;
				}
				return "";
			}

			@Override
			public ComboItem<IScaling> fromString(String string) {

				return null;
			}
		});
		bScaleData.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				cScaleData.setDisable(!newValue);
				bCenterData.setDisable(newValue);
				bCenterData.selectedProperty().set(newValue);
				dataPreprocessing();
			}
		});
		/*
		 * centering
		 */
		cCenterData.getItems().add(new ComboItem<>(CENTERING_MEAN, "", () -> new CenteringMean()));
		cCenterData.getItems().add(new ComboItem<>(CENTERING_MEDIAN, "", () -> new CenteringMedian()));
		cCenterData.valueProperty().addListener(new ChangeListener<ComboItem<ICentering>>() {

			@Override
			public void changed(ObservableValue<? extends ComboItem<ICentering>> observable, ComboItem<ICentering> oldValue, ComboItem<ICentering> newValue) {

				dataPreprocessing();
			}
		});
		cCenterData.setCellFactory(new Callback<ListView<ComboItem<ICentering>>, ListCell<ComboItem<ICentering>>>() {

			@Override
			public ListCell<ComboItem<ICentering>> call(ListView<ComboItem<ICentering>> p) {

				return new ListCell<ComboItem<ICentering>>() {

					@Override
					protected void updateItem(ComboItem<ICentering> item, boolean empty) {

						super.updateItem(item, empty);
						if(item == null) {
							return;
						}
						setText(item.name);
					}
				};
			}
		});
		cCenterData.setConverter(new StringConverter<ComboItem<ICentering>>() {

			@Override
			public String toString(ComboItem<ICentering> item) {

				if(item != null) {
					return item.name;
				}
				return "";
			}

			@Override
			public ComboItem<ICentering> fromString(String string) {

				return null;
			}
		});
		bCenterData.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				cCenterData.setDisable(!newValue);
				dataPreprocessing();
			}
		});
		/*
		 * transformation
		 */
		cTransformData.getItems().add(new ComboItem<>(TRANSFORMATION_POWER, "", () -> new TransformationPower()));
		cTransformData.getItems().add(new ComboItem<>(TRANSFORMATION_LOG10, "", () -> new TransformationLOG10()));
		cTransformData.valueProperty().addListener(new ChangeListener<ComboItem<ITransformation>>() {

			@Override
			public void changed(ObservableValue<? extends ComboItem<ITransformation>> observable, ComboItem<ITransformation> oldValue, ComboItem<ITransformation> newValue) {

				dataPreprocessing();
			}
		});
		cTransformData.setCellFactory(new Callback<ListView<ComboItem<ITransformation>>, ListCell<ComboItem<ITransformation>>>() {

			@Override
			public ListCell<ComboItem<ITransformation>> call(ListView<ComboItem<ITransformation>> p) {

				return new ListCell<ComboItem<ITransformation>>() {

					@Override
					protected void updateItem(ComboItem<ITransformation> item, boolean empty) {

						super.updateItem(item, empty);
						if(item == null) {
							return;
						}
						setText(item.name);
					}
				};
			}
		});
		cTransformData.setConverter(new StringConverter<ComboItem<ITransformation>>() {

			@Override
			public String toString(ComboItem<ITransformation> item) {

				if(item != null) {
					return item.name;
				}
				return "";
			}

			@Override
			public ComboItem<ITransformation> fromString(String string) {

				return null;
			}
		});
		bTransformData.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				cTransformData.setDisable(!newValue);
				dataPreprocessing();
			}
		});
		cReplaceEmptyValue.getItems().add(new ComboItem<>("Mean", "", () -> new MeanValuesReplacer()));
		cReplaceEmptyValue.getItems().add(new ComboItem<>("Median", "", () -> new MedianValuesReplacer()));
		cReplaceEmptyValue.getItems().add(new ComboItem<>("Small Random", "", () -> new SmallValuesReplacer()));
		cReplaceEmptyValue.valueProperty().addListener(new ChangeListener<ComboItem<IDataModificator>>() {

			@Override
			public void changed(ObservableValue<? extends ComboItem<IDataModificator>> observable, ComboItem<IDataModificator> oldValue, ComboItem<IDataModificator> newValue) {

				dataPreprocessing();
			}
		});
		cReplaceEmptyValue.setCellFactory(new Callback<ListView<ComboItem<IDataModificator>>, ListCell<ComboItem<IDataModificator>>>() {

			@Override
			public ListCell<ComboItem<IDataModificator>> call(ListView<ComboItem<IDataModificator>> p) {

				return new ListCell<ComboItem<IDataModificator>>() {

					@Override
					protected void updateItem(ComboItem<IDataModificator> item, boolean empty) {

						super.updateItem(item, empty);
						if(item == null) {
							return;
						}
						setText(item.name);
					}
				};
			}
		});
		cReplaceEmptyValue.setConverter(new StringConverter<ComboItem<IDataModificator>>() {

			@Override
			public String toString(ComboItem<IDataModificator> item) {

				if(item != null) {
					return item.name;
				}
				return "";
			}

			@Override
			public ComboItem<IDataModificator> fromString(String string) {

				return null;
			}
		});
		bRemoveVariables.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				dataPreprocessing();
			}
		});
		bUseOnlySelectedVariables.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				dataPreprocessing();
			}
		});
		reset();
	}

	public void setUpdate(PcaPreprocessingData preprecessing, IPcaSettingsVisualization pcaSettings) {

		setUpdate(preprecessing, null, pcaSettings);
	}

	public void setUpdate(PcaPreprocessingData preprecessing, ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization> samples, IPcaSettingsVisualization pcaSettings) {

		reset();
		isSetPreprocessing = true;
		this.preprecessing = preprecessing;
		this.pcaSettings = pcaSettings;
		this.samples = samples;
		//
		bUseOnlySelectedVariables.setSelected(preprecessing.isModifyOnlySelectedVariable());
		INormalization normalization = preprecessing.getNormalization();
		if(normalization != null) {
			List<ComboItem<INormalization>> items = cNormalizeData.getItems();
			int index = getIndexSelection(items, normalization);
			if(index != -1) {
				cNormalizeData.getSelectionModel().select(index);
				bNormalizeData.setSelected(true);
			}
		}
		ITransformation transformation = preprecessing.getTransformation();
		if(transformation != null) {
			List<ComboItem<ITransformation>> items = cTransformData.getItems();
			int index = getIndexSelection(items, transformation);
			if(index != -1) {
				cTransformData.getSelectionModel().select(index);
				bTransformData.setSelected(true);
			}
		}
		ICentering centering = preprecessing.getCenteringScaling();
		if(centering instanceof IScaling) {
			IScaling scaling = (IScaling)centering;
			List<ComboItem<IScaling>> items = cScaleData.getItems();
			int index = getIndexSelection(items, scaling);
			if(index != -1) {
				cScaleData.getSelectionModel().select(index);
				bScaleData.setSelected(true);
				bCenterData.setSelected(true);
				bCenterData.setDisable(true);
				switch(scaling.getCenteringType()) {
					case AbstractCentering.CENTERING_MEAN:
						cCenterData.getSelectionModel().select(0);
						break;
					case AbstractCentering.CENTERING_MEADIAN:
						cCenterData.getSelectionModel().select(1);
						break;
					default:
						break;
				}
			}
		} else {
			if(centering != null) {
				List<ComboItem<ICentering>> items = cCenterData.getItems();
				int index = getIndexSelection(items, centering);
				if(index != -1) {
					cCenterData.getSelectionModel().select(index);
					bCenterData.setSelected(true);
				}
			}
		}
		IDataModificator replaceEmptyValue = preprecessing.getReplaceEmptyValues();
		List<ComboItem<IDataModificator>> itemsReplaces = cReplaceEmptyValue.getItems();
		int index = getIndexSelection(itemsReplaces, replaceEmptyValue);
		if(index != -1) {
			cReplaceEmptyValue.getSelectionModel().select(index);
		}
		bRemoveVariables.selectedProperty().set(pcaSettings.isRemoveUselessVariables());
		isSetPreprocessing = false;
	}

	public void dataPreprocessing() {

		if(preprecessing == null || isSetPreprocessing) {
			return;
		}
		preprecessing.setModifyOnlySelectedVariable(bUseOnlySelectedVariables.isSelected());
		preprecessing.setRemoveUselessVariables(bRemoveVariables.isSelected());
		if(bNormalizeData.isSelected()) {
			INormalization normalization = cNormalizeData.getSelectionModel().getSelectedItem().preprecessing.get();
			INormalization oldNormalization = preprecessing.getNormalization();
			if(oldNormalization == null || !normalization.getClass().getName().equals(oldNormalization.getClass().getName())) {
				preprecessing.setNormalization(normalization);
			}
		} else {
			preprecessing.setNormalization(null);
		}
		IDataModificator replacer = cReplaceEmptyValue.getSelectionModel().getSelectedItem().preprecessing.get();
		if(!replacer.getClass().getName().equals(preprecessing.getReplaceEmptyValues().getClass().getName())) {
			preprecessing.setReplaceEmptyValues(replacer);
		}
		if(bTransformData.isSelected()) {
			ITransformation transformation = cTransformData.getSelectionModel().getSelectedItem().preprecessing.get();
			ITransformation transformationOld = preprecessing.getTransformation();
			if(transformationOld == null || !transformation.getClass().getName().equals(transformationOld.getClass().getName())) {
				preprecessing.setTransformation(transformation);
			}
		} else {
			preprecessing.setTransformation(null);
		}
		if(bScaleData.isSelected()) {
			int centeringTypeOld = centeringType;
			centeringType = cCenterData.getSelectionModel().getSelectedItem().preprecessing.get().getCenteringType();
			IScaling scaling = cScaleData.getSelectionModel().getSelectedItem().preprecessing.get();
			ICentering centeringScelingOld = preprecessing.getCenteringScaling();
			if(centeringScelingOld == null || centeringTypeOld != centeringType || !scaling.getClass().getName().equals(centeringScelingOld.getClass().getName())) {
				preprecessing.setCenteringScaling(scaling);
			}
		} else {
			if(bCenterData.isSelected()) {
				ICentering centering = cCenterData.getSelectionModel().getSelectedItem().preprecessing.get();
				centeringType = centering.getCenteringType();
				ICentering centeringScelingOld = preprecessing.getCenteringScaling();
				if(centeringScelingOld == null || !centering.getClass().getName().equals(centeringScelingOld.getClass().getName())) {
					preprecessing.setCenteringScaling(centering);
				}
			} else {
				preprecessing.setCenteringScaling(null);
			}
		}
		if(pcaSettings != null) {
			pcaSettings.setRemoveUselessVariables(bRemoveVariables.isSelected());
		}
	}

	public void reset() {

		this.preprecessing = null;
		cNormalizeData.getSelectionModel().select(0);
		cCenterData.getSelectionModel().select(0);
		cScaleData.getSelectionModel().select(0);
		cReplaceEmptyValue.getSelectionModel().select(0);
		cTransformData.getSelectionModel().select(0);
		bNormalizeData.selectedProperty().set(false);
		bCenterData.selectedProperty().set(false);
		bNormalizeData.selectedProperty().set(false);
		bRemoveVariables.selectedProperty().set(false);
		bScaleData.selectedProperty().set(false);
		bTransformData.selectedProperty().set(false);
	}

	private <Preprocessing extends IPreprocessing> int getIndexSelection(List<ComboItem<Preprocessing>> items, Preprocessing preprocess) {

		int i = 0;
		for(; i < items.size(); i++) {
			if(items.get(i).preprecessing.get().getClass().getName().equals(preprocess.getClass().getName())) {
				return i;
			}
		}
		return -1;
	}
}
