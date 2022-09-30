/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.swt;

import java.util.Arrays;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Variance;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d.VarianceChart;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences.PreferencePageScorePlot;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ISettingsHandler;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ExtendedVarianceUI extends Composite implements IExtendedPartUI {

	private VarianceChart plot;
	private EvaluationPCA evaluationPCA = null;

	public ExtendedVarianceUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(EvaluationPCA evaluationPCA) {

		this.evaluationPCA = evaluationPCA;
		updatePlot();
	}

	public void updatePlot() {

		plot.setInput(evaluationPCA);
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		plot = createPlot(this);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(2, false));
		//
		createComboViewerVariance(composite);
		createSettingsButton(composite);
	}

	private VarianceChart createPlot(Composite parent) {

		VarianceChart plot = new VarianceChart(parent, SWT.BORDER);
		plot.setLayoutData(new GridData(GridData.FILL_BOTH));
		return plot;
	}

	private ComboViewer createComboViewerVariance(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setInput(Variance.values());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof Variance variance) {
					return variance.label();
				}
				return null;
			}
		});
		//
		Combo combo = comboViewer.getCombo();
		combo.setToolTipText("Select the variance");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof Variance variance) {
					plot.setVariance(variance);
				}
			}
		});
		//
		comboViewer.setSelection(new StructuredSelection(Variance.EXPLAINED));
		//
		return comboViewer;
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePage.class, PreferencePageScorePlot.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void applySettings() {

		updatePlot();
	}
}