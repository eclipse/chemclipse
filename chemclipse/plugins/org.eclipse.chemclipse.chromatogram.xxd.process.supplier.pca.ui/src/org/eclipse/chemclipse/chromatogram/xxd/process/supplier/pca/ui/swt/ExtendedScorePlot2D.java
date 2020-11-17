/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d.ScorePlot;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences.PreferencePageScorePlot;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ISettingsHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ExtendedScorePlot2D extends Composite implements IExtendedPartUI {

	private ScorePlot plot;
	private PrincipalComponentUI principalComponentUI;
	//
	private EvaluationPCA evaluationPCA = null;

	public ExtendedScorePlot2D(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(EvaluationPCA evaluationPCA) {

		this.evaluationPCA = evaluationPCA;
		updateWidgets();
		applySettings();
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
		principalComponentUI = createPrincipalComponentUI(composite);
		createSettingsButton(composite);
	}

	private ScorePlot createPlot(Composite parent) {

		ScorePlot plot = new ScorePlot(parent, SWT.BORDER);
		plot.setLayoutData(new GridData(GridData.FILL_BOTH));
		return plot;
	}

	private PrincipalComponentUI createPrincipalComponentUI(Composite parent) {

		PrincipalComponentUI principalComponentUI = new PrincipalComponentUI(parent, SWT.NONE, PrincipalComponentUI.SPINNER_X | PrincipalComponentUI.SPINNER_Y);
		principalComponentUI.setSelectionListener(new ISelectionListenerPCs() {

			@Override
			public void update(int pcX, int pcY, int pcZ) {

				updatePlot(pcX, pcY);
			}
		});
		//
		return principalComponentUI;
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

		int pcX = principalComponentUI.getPCX();
		int pcY = principalComponentUI.getPCY();
		updatePlot(pcX, pcY);
	}

	private void updateWidgets() {

		if(evaluationPCA != null) {
			IAnalysisSettings analysisSettings = evaluationPCA.getSamples().getAnalysisSettings();
			principalComponentUI.setInput(analysisSettings);
		} else {
			principalComponentUI.setInput(null);
		}
	}

	private void updatePlot(int pcX, int pcY) {

		if(evaluationPCA != null) {
			plot.setInput(evaluationPCA, pcX, pcY);
		} else {
			plot.setInput(null, pcX, pcY);
		}
	}
}
