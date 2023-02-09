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
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.handlers;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Named;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputEntriesWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputWizardSettings;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractOpenHandler {

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		DataType dataType = getDataType();
		InputWizardSettings inputWizardSettings = InputWizardSettings.create(Activator.getDefault().getPreferenceStore(), getPreferenceKey(), getDataType());
		inputWizardSettings.setTitle(NLS.bind(ExtensionMessages.openDataTypeFiles, dataType));
		inputWizardSettings.setDescription(ExtensionMessages.selectFilesToBeOpened);
		Map<File, Map<ISupplierFileIdentifier, Collection<ISupplier>>> selected = InputEntriesWizard.openWizard(shell, inputWizardSettings);
		for(Entry<File, Map<ISupplierFileIdentifier, Collection<ISupplier>>> entry : selected.entrySet()) {
			File file = entry.getKey();
			for(Entry<ISupplierFileIdentifier, Collection<ISupplier>> supplier : entry.getValue().entrySet()) {
				ISupplierFileIdentifier identifier = supplier.getKey();
				if(identifier instanceof ISupplierFileEditorSupport supplierFileEditor) {
					for(ISupplier converter : supplier.getValue()) {
						if(supplierFileEditor.openEditor(file, converter)) {
							break;
						}
					}
				}
			}
		}
	}

	protected abstract DataType getDataType();

	protected abstract String getPreferenceKey();
}
