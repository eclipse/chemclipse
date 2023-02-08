/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - support for sorting / icons
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences;

import java.util.List;

import org.eclipse.chemclipse.support.ui.processors.PreferencesSupport;
import org.eclipse.chemclipse.support.ui.processors.Processor;
import org.eclipse.chemclipse.support.ui.processors.ProcessorToolbarSelectionUI;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ProcessorToolbarPreferencePage extends PreferencePage {

	private static final int DEFAULT_WIDTH = 400;
	private static final int DEFAULT_HEIGHT = 600;
	//
	private final PreferencesSupport preferencesSupport;
	private ProcessorToolbarSelectionUI processorToolbarSelectionUI;

	public ProcessorToolbarPreferencePage(PreferencesSupport preferencesSupport) {

		setTitle("Processor Quick-Access");
		setDescription("Select the processor toolbar items.");
		noDefaultAndApplyButton();
		setSize(new Point(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		//
		this.preferencesSupport = preferencesSupport;
		setPreferenceStore(preferencesSupport.getPreferenceStore());
	}

	@Override
	public boolean performOk() {

		if(processorToolbarSelectionUI != null) {
			List<Processor> processors = processorToolbarSelectionUI.getProcessors();
			preferencesSupport.persist(processors);
		}
		//
		return true;
	}

	@Override
	protected Control createContents(Composite parent) {

		processorToolbarSelectionUI = new ProcessorToolbarSelectionUI(parent, SWT.NONE);
		processorToolbarSelectionUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		processorToolbarSelectionUI.setInput(preferencesSupport.getStoredProcessors());
		//
		return processorToolbarSelectionUI;
	}
}