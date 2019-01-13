/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaPreprocessingData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.AbstractCentering;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.CenteringMean;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.CenteringMedian;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ICentering;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.INormalization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.IScaling;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ITransformation;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.Normalization1Norm;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.Normalization2Norm;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.NormalizationInfNorm;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ScalingAuto;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ScalingLevel;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ScalingPareto;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ScalingRange;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ScalingVast;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.TransformationLOG10;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.TransformationPower;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISampleVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISamplesVisualization;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class DataPreprocessingSelection {

	//
	private final int CENTERING_MEAN = 0;
	private final int CENTERING_MEDIAN = 1;
	private Button centeringButton;
	private CCombo centeringCombo;
	//
	private final int NORMALIZATION_1_NORM = 0;
	private final int NORMALIZATION_2_NORM = 1;
	private final int NORMALIZATION_INF_NORM = 2;
	private Button normalizationButton;
	private CCombo normalizationCombo;
	private PcaPreprocessingData pcaPreprocessingData;
	//
	private ISamplesVisualization<? extends IVariable, ? extends ISampleVisualization> samples;
	//
	private final int SCALING_AUTO = 0;
	private final int SCALING_LEVEL = 4;
	private final int SCALING_PARETO = 2;
	private final int SCALING_RANGE = 1;
	private final int SCALING_VAST = 3;
	private Button scalingButton;
	private CCombo scalingCombo;
	//
	private final int TRANSFORMATION_LOG10 = 0;
	private final int TRANSFORMATION_POWER = 1;
	private Button transformationButton;
	//
	private CCombo transformationCombo;

	public DataPreprocessingSelection(Composite composite, Object layoutData) {

		this(composite, layoutData, new PcaPreprocessingData());
	}

	public DataPreprocessingSelection(Composite composite, Object layoutData, PcaPreprocessingData pcaPreprocessingData) {

		init(composite, layoutData);
		update(pcaPreprocessingData);
	}

	private void addCentering(Composite parent, GridData layoutData) {

		centeringButton = new Button(parent, SWT.CHECK);
		centeringButton.setText("Centre Data");
		centeringButton.addListener(SWT.Selection, e -> {
			if(centeringButton.getSelection()) {
				centeringCombo.setEnabled(true);
			} else {
				centeringCombo.setEnabled(false);
			}
			updatePreprocessoring();
		});
		centeringButton.setLayoutData(GridDataFactory.copyData(layoutData));
		centeringCombo = new CCombo(parent, SWT.READ_ONLY);
		centeringCombo.add("Mean Centering");
		centeringCombo.add("Median Centering");
		centeringCombo.addListener(SWT.Selection, e -> updatePreprocessoring());
		centeringCombo.setLayoutData(GridDataFactory.copyData(layoutData));
	}

	private void addNormalization(Composite parent, GridData layoutData) {

		normalizationButton = new Button(parent, SWT.CHECK);
		normalizationButton.setText("Normalize Data");
		normalizationButton.addListener(SWT.Selection, e -> {
			normalizationCombo.setEnabled(normalizationButton.getSelection());
			updatePreprocessoring();
		});
		normalizationButton.setLayoutData(GridDataFactory.copyData(layoutData));
		normalizationCombo = new CCombo(parent, SWT.READ_ONLY);
		normalizationCombo.addListener(SWT.Selection, e -> updatePreprocessoring());
		normalizationCombo.add("Normalization 1-norm");
		normalizationCombo.add("Normalization 2-norm");
		normalizationCombo.add("Normalization inf-norm");
		normalizationCombo.setLayoutData(GridDataFactory.copyData(layoutData));
	}

	private void addScaling(Composite parent, GridData layoutData) {

		scalingButton = new Button(parent, SWT.CHECK);
		scalingButton.setText("Scale Data");
		scalingButton.addListener(SWT.Selection, e -> {
			if(scalingButton.getSelection()) {
				centeringButton.setSelection(true);
				centeringButton.setEnabled(false);
				centeringCombo.setEnabled(true);
				scalingCombo.setEnabled(true);
			} else {
				centeringButton.setEnabled(true);
				centeringButton.setSelection(false);
				centeringCombo.setEnabled(false);
				scalingCombo.setEnabled(false);
			}
			updatePreprocessoring();
		});
		scalingButton.setLayoutData(GridDataFactory.copyData(layoutData));
		scalingCombo = new CCombo(parent, SWT.READ_ONLY);
		scalingCombo.addListener(SWT.Selection, e -> updatePreprocessoring());
		scalingCombo.add("Autoscaling");
		scalingCombo.add("Range Scaling");
		scalingCombo.add("Pareto Scaling");
		scalingCombo.add("Vast Scaling");
		scalingCombo.add("Level Scaling");
		scalingCombo.setLayoutData(GridDataFactory.copyData(layoutData));
	}

	private void addTransformation(Composite parent, GridData layoutData) {

		transformationButton = new Button(parent, SWT.CHECK);
		transformationButton.setText("Transform Data");
		transformationButton.addListener(SWT.Selection, e -> {
			transformationCombo.setEnabled(transformationButton.getSelection());
			updatePreprocessoring();
		});
		transformationButton.setLayoutData(GridDataFactory.copyData(layoutData));
		transformationCombo = new CCombo(parent, SWT.READ_ONLY);
		transformationCombo.addListener(SWT.Selection, e -> updatePreprocessoring());
		transformationCombo.add("Log Transformation 10log(x)");
		transformationCombo.add("Power Transformation");
		transformationCombo.setLayoutData(GridDataFactory.copyData(layoutData));
	}

	public ISamples<? extends IVariable, ? extends ISample> getSamples() {

		return samples;
	}

	private void init(Composite parent, Object layoutData) {

		Composite composite = new Composite(parent, SWT.None);
		composite.setLayoutData(layoutData);
		composite.setLayout(new GridLayout(2, true));
		addNormalization(composite, new GridData(GridData.FILL_HORIZONTAL));
		addTransformation(composite, new GridData(GridData.FILL_HORIZONTAL));
		addCentering(composite, new GridData(GridData.FILL_HORIZONTAL));
		addScaling(composite, new GridData(GridData.FILL_HORIZONTAL));
	}

	public void setSamples(ISamplesVisualization<? extends IVariable, ? extends ISample> samples) {

		this.samples = samples;
	}

	private void setWidgetsCentering(ICentering centering) {

		centeringButton.setSelection(true);
		centeringButton.setEnabled(true);
		centeringCombo.setEnabled(true);
		scalingButton.setSelection(false);
		scalingCombo.setEnabled(false);
		if(centering instanceof CenteringMean) {
			centeringCombo.select(CENTERING_MEAN);
		} else if(centering instanceof CenteringMedian) {
			centeringCombo.select(CENTERING_MEDIAN);
		} else {
			centeringCombo.select(CENTERING_MEAN);
		}
	}

	private void setWidgetsNormalization(INormalization normalization) {

		if(normalization == null) {
			normalizationButton.setSelection(false);
			normalizationCombo.select(TRANSFORMATION_POWER);
			normalizationCombo.setEnabled(false);
		} else {
			normalizationButton.setSelection(true);
			normalizationCombo.setEnabled(true);
			if(normalization instanceof Normalization1Norm) {
				normalizationCombo.select(NORMALIZATION_1_NORM);
			} else if(normalization instanceof Normalization2Norm) {
				normalizationCombo.select(NORMALIZATION_2_NORM);
			} else if(normalization instanceof NormalizationInfNorm) {
				normalizationCombo.select(NORMALIZATION_INF_NORM);
			}
		}
	}

	private void setWidgetsScaling(IScaling scaling) {

		centeringButton.setSelection(true);
		centeringButton.setEnabled(false);
		centeringCombo.setEnabled(true);
		scalingButton.setSelection(true);
		scalingCombo.setEnabled(true);
		if(scaling.getCenteringType() == AbstractCentering.CENTERING_MEAN) {
			centeringCombo.select(CENTERING_MEAN);
		} else {
			centeringCombo.select(CENTERING_MEDIAN);
		}
		if(scaling instanceof ScalingAuto) {
			scalingCombo.select(SCALING_AUTO);
		} else if(scaling instanceof ScalingPareto) {
			scalingCombo.select(SCALING_PARETO);
		} else if(scaling instanceof ScalingLevel) {
			scalingCombo.select(SCALING_LEVEL);
		} else if(scaling instanceof ScalingRange) {
			scalingCombo.select(SCALING_RANGE);
		} else if(scaling instanceof ScalingVast) {
			scalingCombo.select(SCALING_VAST);
		} else {
			scalingCombo.select(SCALING_AUTO);
		}
	}

	private void setWidgetsScalingAndCentering() {

		centeringButton.setEnabled(true);
		scalingButton.setEnabled(true);
		centeringButton.setSelection(false);
		scalingButton.setSelection(false);
		scalingCombo.setEnabled(false);
		centeringCombo.setEnabled(false);
		centeringCombo.select(CENTERING_MEAN);
		scalingCombo.select(SCALING_AUTO);
	}

	private void setWidgetsTransformation(ITransformation transformation) {

		if(transformation == null) {
			transformationButton.setSelection(false);
			transformationCombo.select(TRANSFORMATION_POWER);
			transformationCombo.setEnabled(false);
		} else {
			transformationButton.setSelection(true);
			transformationCombo.setEnabled(true);
			if(transformation instanceof TransformationPower) {
				transformationCombo.select(TRANSFORMATION_POWER);
			} else if(transformation instanceof TransformationLOG10) {
				transformationCombo.select(TRANSFORMATION_LOG10);
			}
		}
	}

	public void update(PcaPreprocessingData pcaPreprocessingData) {

		this.pcaPreprocessingData = pcaPreprocessingData;
		ICentering centeringScaling = pcaPreprocessingData.getCenteringScaling();
		if(centeringScaling != null) {
			if(centeringScaling instanceof IScaling) {
				setWidgetsScaling((IScaling)centeringScaling);
			} else {
				setWidgetsCentering(centeringScaling);
			}
		} else {
			setWidgetsScalingAndCentering();
		}
		setWidgetsTransformation(pcaPreprocessingData.getTransformation());
		setWidgetsNormalization(pcaPreprocessingData.getNormalization());
	}

	private void updateCentering() {

		switch(centeringCombo.getSelectionIndex()) {
			case CENTERING_MEAN:
				pcaPreprocessingData.setCenteringScaling(new CenteringMean());
				break;
			case CENTERING_MEDIAN:
				pcaPreprocessingData.setCenteringScaling(new CenteringMedian());
				break;
			default:
				pcaPreprocessingData.setCenteringScaling(null);
				break;
		}
	}

	private void updateNormalization() {

		if(!normalizationButton.getSelection()) {
			pcaPreprocessingData.setNormalization(null);
		} else {
			switch(normalizationCombo.getSelectionIndex()) {
				case NORMALIZATION_1_NORM:
					pcaPreprocessingData.setNormalization(new Normalization1Norm());
					break;
				case NORMALIZATION_2_NORM:
					pcaPreprocessingData.setNormalization(new Normalization2Norm());
					break;
				case NORMALIZATION_INF_NORM:
					pcaPreprocessingData.setNormalization(new NormalizationInfNorm());
					break;
				default:
					pcaPreprocessingData.setNormalization(null);
					break;
			}
		}
	}

	private void updatePreprocessoring() {

		updateNormalization();
		updateTransformation();
		if(scalingButton.getSelection()) {
			updateScaling();
		} else if(centeringButton.getSelection()) {
			updateCentering();
		} else {
			pcaPreprocessingData.setCenteringScaling(null);
		}
		if(samples != null) {
			pcaPreprocessingData.process(samples, new NullProgressMonitor());
			samples.updateDataAllSamples();
		}
	}

	private void updateScaling() {

		int centering = 1;
		switch(centeringCombo.getSelectionIndex()) {
			case CENTERING_MEAN:
				centering = AbstractCentering.CENTERING_MEAN;
				break;
			case CENTERING_MEDIAN:
				centering = AbstractCentering.CENTERING_MEADIAN;
		}
		switch(scalingCombo.getSelectionIndex()) {
			case SCALING_AUTO:
				pcaPreprocessingData.setCenteringScaling(new ScalingAuto(centering));
				break;
			case SCALING_RANGE:
				pcaPreprocessingData.setCenteringScaling(new ScalingRange(centering));
				break;
			case SCALING_PARETO:
				pcaPreprocessingData.setCenteringScaling(new ScalingPareto(centering));
				break;
			case SCALING_VAST:
				pcaPreprocessingData.setCenteringScaling(new ScalingVast(centering));
				break;
			case SCALING_LEVEL:
				pcaPreprocessingData.setCenteringScaling(new ScalingLevel(centering));
				break;
			default:
				pcaPreprocessingData.setCenteringScaling(null);
				break;
		}
	}

	private void updateTransformation() {

		if(!transformationButton.getSelection()) {
			pcaPreprocessingData.setTransformation(null);
		} else {
			switch(transformationCombo.getSelectionIndex()) {
				case TRANSFORMATION_LOG10:
					pcaPreprocessingData.setTransformation(new TransformationLOG10());
					break;
				case TRANSFORMATION_POWER:
					pcaPreprocessingData.setTransformation(new TransformationPower());
					break;
				default:
					pcaPreprocessingData.setTransformation(null);
					break;
			}
		}
	}
}
