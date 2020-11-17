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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart3d.ScorePlot3D;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart3d.ScorePlot3DSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ISettingsHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ExtendedScorePlot3D extends Composite implements IExtendedPartUI {

	private ScorePlot3D plot;
	private PrincipalComponentUI principalComponentUI;
	//
	private EvaluationPCA evaluationPCA = null;

	public ExtendedScorePlot3D(Composite parent, int style) {

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

	private ScorePlot3D createPlot(Composite parent) {

		ScorePlot3D plot = new ScorePlot3D(parent, SWT.BORDER);
		return plot;
	}

	private PrincipalComponentUI createPrincipalComponentUI(Composite parent) {

		PrincipalComponentUI principalComponentUI = new PrincipalComponentUI(parent, SWT.NONE);
		principalComponentUI.setSelectionListener(new ISelectionListenerPCs() {

			@Override
			public void update(int pcX, int pcY, int pcZ) {

				updatePlot(pcX, pcY, pcZ);
			}
		});
		//
		return principalComponentUI;
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePage.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void applySettings() {

		int pcX = principalComponentUI.getPCX();
		int pcY = principalComponentUI.getPCY();
		int pcZ = principalComponentUI.getPCZ();
		updatePlot(pcX, pcY, pcZ);
	}

	private void updateWidgets() {

		if(evaluationPCA != null) {
			IAnalysisSettings analysisSettings = evaluationPCA.getSamples().getAnalysisSettings();
			principalComponentUI.setInput(analysisSettings);
		} else {
			principalComponentUI.setInput(null);
		}
	}

	private void updatePlot(int pcX, int pcY, int pcZ) {

		if(evaluationPCA != null) {
			ScorePlot3DSettings settings = plot.getSettings();
			settings.setPcX(pcX);
			settings.setPcX(pcY);
			settings.setPcX(pcZ);
			plot.setInput(evaluationPCA);
		} else {
			plot.setInput(null);
		}
	}
}
