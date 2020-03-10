/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Dr. Philip Wenig - get rid of JavaFX
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PreprocessingSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISampleVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISamplesVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.swt.PreprocessingSettingsUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class PreprocessingPart extends GeneralPcaPart {

	private PreprocessingSettingsUI preprocessingSettingsUI;

	@Inject
	public PreprocessingPart() {
		super();
	}

	@PostConstruct
	public void postConstruct(Composite composite) {

		init(composite);
		initializeHandler();
	}

	private void init(final Composite parent) {

		preprocessingSettingsUI = new PreprocessingSettingsUI(parent, SWT.NONE);
	}

	@Override
	protected void variablesHasBeenUpdated() {

	}

	@Override
	protected void samplesHasBeenUpdated() {

	}

	@Override
	protected void samplesHasBeenSet() {

		setSamples();
	}

	@Override
	protected void settingsHasBeenChanged() {

	}

	private void setSamples() {

		ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization> samples = getSamples();
		if(preprocessingSettingsUI != null) {
			if(samples != null) {
				preprocessingSettingsUI.update(getSelectionManagerSamples().getPreprocessingSettings(samples));
			} else {
				preprocessingSettingsUI.update(new PreprocessingSettings());
			}
		}
	}
}
