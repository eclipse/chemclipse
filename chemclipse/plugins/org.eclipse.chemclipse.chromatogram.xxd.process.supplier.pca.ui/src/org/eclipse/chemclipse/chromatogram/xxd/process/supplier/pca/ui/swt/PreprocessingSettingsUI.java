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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.Activator;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class PreprocessingSettingsUI extends Composite {

	private static final String KEY_IMAGE = "IMAGE";
	private static final String LABEL_NORMALIZE = "Normalize Data:";
	private static final String LABEL_REPLACE = "Replace Data:";
	private static final String LABEL_TRANSFORM = "Transform Data:";
	private static final String LABEL_CENTER = "Center Data:";
	private static final String LABEL_SCALE = "Scale Data:";
	//
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
	private Label labelFormula;
	private Canvas canvasFormula;
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

	public void setInput(PreprocessingSettings preprocessingSettings) {

		this.preprocessingSettings = preprocessingSettings;
		updateWidgets();
	}

	public PreprocessingSettings getPreprocessingSettings() {

		return preprocessingSettings;
	}

	private void createControl() {

		setLayout(new GridLayout(3, false));
		//
		createLabel(this, LABEL_NORMALIZE);
		comboViewerNormalize = createComboViewerNormalize(this);
		createButtonInfoFormula(this, comboViewerNormalize);
		//
		createLabel(this, LABEL_REPLACE);
		comboViewerReplacer = createComboViewerReplace(this);
		createButtonInfoFormula(this, comboViewerReplacer);
		//
		createLabel(this, LABEL_TRANSFORM);
		comboViewerTransformation = createComboViewerTransform(this);
		createButtonInfoFormula(this, comboViewerTransformation);
		//
		createLabel(this, LABEL_CENTER);
		comboViewerCentering = createComboViewerCenter(this);
		createButtonInfoFormula(this, comboViewerCentering);
		//
		createLabel(this, LABEL_SCALE);
		comboViewerScaling = createComboViewerScale(this);
		createButtonInfoFormula(this, comboViewerScaling);
		//
		checkBoxRemove = createCheckBoxRemoveVariables(this);
		checkBoxSelect = createCheckBoxSelectVariables(this);
		//
		labelFormula = createLabelFormula(this);
		canvasFormula = createCanvas(this);
	}

	private Label createLabelFormula(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		composite.setLayoutData(gridData);
		return createLabel(composite, "Formula:");
	}

	private Label createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		label.setLayoutData(gridData);
		return label;
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
				//
				updateFormulaDescription(comboViewer);
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
				//
				updateFormulaDescription(comboViewer);
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
				//
				updateFormulaDescription(comboViewer);
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
				//
				updateFormulaDescription(comboViewer);
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
				//
				updateFormulaDescription(comboViewer);
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
		combo.select(0);
		combo.setToolTipText(tooltip);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return comboViewer;
	}

	private Button createButtonInfoFormula(Composite parent, ComboViewer comboViewer) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Show the formula.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateFormulaDescription(comboViewer);
			}
		});
		//
		return button;
	}

	private Button createCheckBoxRemoveVariables(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setText("Remove variables, which contain less than two values.");
		button.setToolTipText("Remove Variables");
		button.setSelection(preprocessingSettings.isRemoveUselessVariables());
		//
		GridData gridData = new GridData();
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
		button.setSelection(preprocessingSettings.isModifyOnlySelectedVariable());
		//
		GridData gridData = new GridData();
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

	private Canvas createCanvas(Composite parent) {

		Canvas canvas = new Canvas(parent, SWT.BORDER);
		canvas.setToolTipText("Formula");
		canvas.setBackground(Colors.WHITE);
		canvas.setData(KEY_IMAGE, Activator.getDefault().getImage(Activator.ICON_DEVIATION));
		//
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 3;
		canvas.setLayoutData(gridData);
		canvas.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {

				Object object = canvas.getData(KEY_IMAGE);
				if(object instanceof Image) {
					/*
					 * Center the image
					 */
					Image image = (Image)object;
					Rectangle src = image.getBounds();
					Rectangle dest = canvas.getBounds();
					int x = (int)(dest.width / 2.0f - src.width / 2.0f);
					int y = (int)(dest.height / 2.0f - src.height / 2.0f);
					e.gc.drawImage(image, x, y);
				}
			}
		});
		//
		return canvas;
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
			//
			if(centering != null) {
				switch(centering.getCenteringType()) {
					case 1:
						comboViewerCentering.getCombo().select(1);
						comboViewerScaling.setInput(scalingInputMean);
						comboViewerScaling.getCombo().setEnabled(true);
						selectComboItem(comboViewerScaling, scalingInputMean, centering);
						break;
					case 2:
						comboViewerCentering.getCombo().select(2);
						comboViewerScaling.setInput(scalingInputMedian);
						comboViewerScaling.getCombo().setEnabled(true);
						selectComboItem(comboViewerScaling, scalingInputMedian, centering);
						break;
					default:
						comboViewerCentering.getCombo().select(0);
						comboViewerScaling.setInput(scaleInputEmpty);
						comboViewerScaling.getCombo().select(0);
						comboViewerScaling.getCombo().setEnabled(false);
						break;
				}
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

	private void updateFormulaDescription(ComboViewer comboViewer) {

		String text = "--";
		Image image = null;
		Object object = comboViewer.getStructuredSelection().getFirstElement();
		//
		if(object instanceof IPreprocessing) {
			IPreprocessing preprocessing = (IPreprocessing)object;
			text = preprocessing.getDescription();
		}
		//
		if(comboViewer == comboViewerNormalize) {
			if(object instanceof Normalization1Norm) {
				// TODO Formula
			} else if(object instanceof Normalization2Norm) {
				// TODO Formula
			} else if(object instanceof NormalizationInfNorm) {
				// TODO Formula
			}
		} else if(comboViewer == comboViewerReplacer) {
			if(object instanceof MeanValuesReplacer) {
				// TODO Formula
			} else if(object instanceof MedianValuesReplacer) {
				// TODO Formula
			} else if(object instanceof SmallValuesReplacer) {
				// TODO Formula
			}
		} else if(comboViewer == comboViewerTransformation) {
			if(object instanceof TransformationLOG10) {
				image = Activator.getDefault().getImage(Activator.ICON_NORM_TRANS_LOG);
			} else if(object instanceof TransformationPower) {
				image = Activator.getDefault().getImage(Activator.ICON_NORM_TRANS_POWER);
			}
		} else if(comboViewer == comboViewerCentering) {
			if(object instanceof CenteringMean) {
				image = Activator.getDefault().getImage(Activator.ICON_CENTER_MEAN);
			} else if(object instanceof CenteringMedian) {
				image = Activator.getDefault().getImage(Activator.ICON_CENTER_MEDIAN);
			}
		} else if(comboViewer == comboViewerScaling) {
			if(object instanceof ScalingAuto) {
				image = Activator.getDefault().getImage(Activator.ICON_NORM_SCALE_AUTO);
			} else if(object instanceof ScalingLevel) {
				image = Activator.getDefault().getImage(Activator.ICON_NORM_SCALE_LEVEL);
			} else if(object instanceof ScalingPareto) {
				image = Activator.getDefault().getImage(Activator.ICON_NORM_SCALE_PARETO);
			} else if(object instanceof ScalingRange) {
				image = Activator.getDefault().getImage(Activator.ICON_NORM_SCALE_RANGE);
			} else if(object instanceof ScalingVast) {
				image = Activator.getDefault().getImage(Activator.ICON_NORM_SCALE_VAST);
			}
		}
		//
		labelFormula.setText(text);
		canvasFormula.setData(KEY_IMAGE, image);
		canvasFormula.redraw();
	}
}
