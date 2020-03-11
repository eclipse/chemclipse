/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.swt;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PreprocessingSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.CenteringMean;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.CenteringMedian;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ICentering;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.INormalization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.IPreprocessing;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.IReplacer;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ITransformation;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.MeanValuesReplacer;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.MedianValuesReplacer;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.Normalization1Norm;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.Normalization2Norm;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.NormalizationInfNorm;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ScalingAuto;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ScalingLevel;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ScalingPareto;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ScalingRange;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ScalingVast;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.SmallValuesReplacer;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.TransformationLOG10;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.TransformationPower;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class PreprocessingSettingsUI extends Composite {

	private PreprocessingSettings preprocessingSettings = new PreprocessingSettings();
	//
	private ComboViewer comboViewerNormalize;
	private ComboViewer comboViewerReplacer;
	private ComboViewer comboViewerTransformation;
	private ComboViewer comboViewerCentering;
	private ComboViewer comboViewerScaling;
	private Button checkBoxRemove;
	private Button checkBoxSelect;
	//
	private Object[] normalizeInput = new Object[]{"--", new Normalization1Norm(), new Normalization2Norm(), new NormalizationInfNorm()};
	private Object[] replacerInput = new Object[]{new MeanValuesReplacer(), new MedianValuesReplacer(), new SmallValuesReplacer()};
	private Object[] transformationInput = new Object[]{"--", new TransformationLOG10(), new TransformationPower()};
	private Object[] centeringInput = new Object[]{"--", new CenteringMean(), new CenteringMedian()};
	private Object[] scaleInputEmpty = new Object[]{"--"};
	private Object[] scalingInputMean = createScaleElements(ICentering.MEAN);
	private Object[] scalingInputMedian = createScaleElements(ICentering.MEDIAN);

	public PreprocessingSettingsUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	public void update(PreprocessingSettings preprocessingSettings) {

		this.preprocessingSettings = preprocessingSettings;
		updateWidgets();
	}

	public PreprocessingSettings getPreprocessingSettings() {

		return preprocessingSettings;
	}

	private void createControl() {

		setLayout(new GridLayout(3, false));
		//
		createLabel(this, 2, "Normalize Data:");
		comboViewerNormalize = createComboViewerNormalize(this);
		//
		createLabel(this, 2, "Replace Data:");
		comboViewerReplacer = createComboViewerReplace(this);
		//
		createLabel(this, 2, "Transform Data:");
		comboViewerTransformation = createComboViewerTransform(this);
		//
		createLabel(this, 1, "Center & Scale Data:");
		comboViewerCentering = createComboViewerCenter(this);
		comboViewerScaling = createComboViewerScale(this);
		//
		checkBoxRemove = createCheckBoxRemoveVariables(this);
		checkBoxSelect = createCheckBoxSelectVariables(this);
	}

	private void createLabel(Composite parent, int horizontalSpan, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = horizontalSpan;
		label.setLayoutData(gridData);
	}

	private ComboViewer createComboViewerNormalize(Composite parent) {

		ComboViewer comboViewer = createComboViewer(parent, normalizeInput, "Normalization");
		Combo combo = comboViewer.getCombo();
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof INormalization) {
					preprocessingSettings.setNormalization((INormalization)object);
				} else {
					preprocessingSettings.setNormalization(null);
				}
			}
		});
		//
		return comboViewer;
	}

	private ComboViewer createComboViewerReplace(Composite parent) {

		ComboViewer comboViewer = createComboViewer(parent, replacerInput, "Replacement");
		Combo combo = comboViewer.getCombo();
		combo.select(0);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof IReplacer) {
					preprocessingSettings.setReplacer((IReplacer)object);
				} else {
					preprocessingSettings.setReplacer(null);
				}
			}
		});
		//
		return comboViewer;
	}

	private ComboViewer createComboViewerTransform(Composite parent) {

		ComboViewer comboViewer = createComboViewer(parent, transformationInput, "Transformation");
		Combo combo = comboViewer.getCombo();
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof ITransformation) {
					preprocessingSettings.setTransformation((ITransformation)object);
				} else {
					preprocessingSettings.setTransformation(null);
				}
			}
		});
		//
		return comboViewer;
	}

	private ComboViewer createComboViewerCenter(Composite parent) {

		ComboViewer comboViewer = createComboViewer(parent, centeringInput, "Centering");
		Combo combo = comboViewer.getCombo();
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(comboViewerScaling != null) {
					Object object = comboViewer.getStructuredSelection().getFirstElement();
					preprocessingSettings.setCentering(null);
					Combo combo = comboViewerScaling.getCombo();
					//
					if(object instanceof CenteringMean) {
						comboViewerScaling.setInput(scalingInputMean);
						combo.setEnabled(true);
					} else if(object instanceof CenteringMedian) {
						comboViewerScaling.setInput(scalingInputMedian);
						combo.setEnabled(true);
					} else {
						comboViewerScaling.setInput(scaleInputEmpty);
						combo.setEnabled(false);
					}
				}
			}
		});
		//
		return comboViewer;
	}

	private ComboViewer createComboViewerScale(Composite parent) {

		ComboViewer comboViewer = createComboViewer(parent, scaleInputEmpty, "Scaling");
		Combo combo = comboViewer.getCombo();
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof ICentering) {
					preprocessingSettings.setCentering((ICentering)object);
				} else {
					preprocessingSettings.setCentering(null);
				}
			}
		});
		//
		return comboViewer;
	}

	private ComboViewer createComboViewer(Composite parent, Object[] input, String tooltip) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setInput(input);
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IPreprocessing) {
					return ((IPreprocessing)element).getName();
				} else if(element instanceof String) {
					return element.toString();
				}
				return null;
			}
		});
		//
		Combo combo = comboViewer.getCombo();
		combo.setToolTipText(tooltip);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return comboViewer;
	}

	private Button createCheckBoxRemoveVariables(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setText("Remove variables, which contain less than two values.");
		button.setToolTipText("Remove Variables");
		//
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		button.setLayoutData(gridData);
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				preprocessingSettings.setRemoveUselessVariables(button.getSelection());
			}
		});
		//
		return button;
	}

	private Button createCheckBoxSelectVariables(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setText("Use only selected variables for data processing.");
		button.setToolTipText("Selected Variables");
		//
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		button.setLayoutData(gridData);
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				preprocessingSettings.setModifyOnlySelectedVariable(button.getSelection());
			}
		});
		//
		return button;
	}

	private Object[] createScaleElements(int centeringType) {

		return new Object[]{"--", new ScalingAuto(centeringType), new ScalingLevel(centeringType), new ScalingPareto(centeringType), new ScalingRange(centeringType), new ScalingVast(centeringType)};
	}

	private void updateWidgets() {

		if(preprocessingSettings != null) {
			selectComboItem(comboViewerNormalize, normalizeInput, preprocessingSettings.getNormalization());
			selectComboItem(comboViewerReplacer, replacerInput, preprocessingSettings.getReplacer());
			selectComboItem(comboViewerTransformation, transformationInput, preprocessingSettings.getTransformation());
			//
			ICentering centering = preprocessingSettings.getCentering();
			if(centering != null) {
				switch(centering.getCenteringType()) {
					case 1: // Mean
						selectComboItem(comboViewerScaling, scalingInputMean, centering);
						break;
					case 2: // Median
						selectComboItem(comboViewerScaling, scalingInputMedian, centering);
						break;
					default:
						comboViewerCentering.getCombo().select(0);
						break;
				}
			} else {
				comboViewerCentering.getCombo().select(0);
			}
			//
			checkBoxRemove.setSelection(preprocessingSettings.isRemoveUselessVariables());
			checkBoxSelect.setSelection(preprocessingSettings.isModifyOnlySelectedVariable());
		}
	}

	private void selectComboItem(ComboViewer comboViewer, Object[] input, IPreprocessing selectedPreprocessing) {

		Combo combo = comboViewer.getCombo();
		combo.select(0);
		//
		if(selectedPreprocessing != null) {
			for(int i = 0; i < input.length; i++) {
				Object object = input[i];
				if(object instanceof IPreprocessing) {
					IPreprocessing preprocessing = (IPreprocessing)object;
					if(preprocessing.getName().equals(selectedPreprocessing.getName())) {
						combo.select(i);
						return;
					}
				}
			}
		}
	}
}
