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
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import java.util.List;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.support.ui.processors.Processor;
import org.eclipse.chemclipse.support.ui.processors.ProcessorSupport;
import org.eclipse.chemclipse.support.ui.processors.ProcessorToolbarSelectionUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PreferencesProcessSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public abstract class AbstractPreferencePageToolbar extends PreferencePage implements IWorkbenchPreferencePage {

	private static final int DEFAULT_WIDTH = 400;
	private static final int DEFAULT_HEIGHT = 600;
	//
	private ProcessorToolbarSelectionUI processorToolbarSelectionUI;
	private PreferencesProcessSupport preferencesProcessSupport;
	private String preference = "";

	public AbstractPreferencePageToolbar(IPreferenceStore preferenceStore, String preference, DataCategory dataCategory) {

		setTitle(ExtensionMessages.processorQuickAccess);
		setDescription(ExtensionMessages.selectProcessorToolbarItems);
		noDefaultAndApplyButton();
		setSize(new Point(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		//
		setPreferenceStore(preferenceStore);
		this.preference = preference + dataCategory.name();
		//
		preferencesProcessSupport = new PreferencesProcessSupport(dataCategory);
	}

	@Override
	public boolean performOk() {

		if(processorToolbarSelectionUI != null) {
			List<Processor> processors = processorToolbarSelectionUI.getProcessors();
			getPreferenceStore().setValue(preference, ProcessorSupport.getActiveProcessors(processors));
		}
		//
		return true;
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected Control createContents(Composite parent) {

		processorToolbarSelectionUI = createProcessorToolbarSelectionUI(parent);
		return processorToolbarSelectionUI;
	}

	private ProcessorToolbarSelectionUI createProcessorToolbarSelectionUI(Composite parent) {

		ProcessorToolbarSelectionUI processorToolbarSelectionUI = new ProcessorToolbarSelectionUI(parent, SWT.NONE);
		processorToolbarSelectionUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		processorToolbarSelectionUI.setInput(preferencesProcessSupport.getActiveProcessors());
		//
		return processorToolbarSelectionUI;
	}
}