/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.handlers;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Named;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputEntriesWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputWizardSettings;
import org.eclipse.chemclipse.xxd.process.files.ISupplierFileIdentifier;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractOpenHandler {

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		DataType dataType = getDataType();
		InputWizardSettings inputWizardSettings = InputWizardSettings.create(Activator.getDefault().getPreferenceStore(), getPreferenceKey(), getDataType());
		inputWizardSettings.setTitle("Open " + dataType + " Files");
		inputWizardSettings.setDescription("You can select one or more files to be opened.");
		Map<File, Collection<ISupplierFileIdentifier>> selected = InputEntriesWizard.openWizard(shell, inputWizardSettings);
		for(Entry<File, Collection<ISupplierFileIdentifier>> entry : selected.entrySet()) {
			File file = entry.getKey();
			for(ISupplierFileIdentifier supplier : entry.getValue()) {
				if(supplier instanceof ISupplierFileEditorSupport) {
					if(((ISupplierFileEditorSupport)supplier).openEditor(file)) {
						break;
					}
				}
			}
		}
	}

	protected abstract DataType getDataType();

	protected abstract String getPreferenceKey();
}
