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

import javax.inject.Named;

import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.DataCategoryGroup;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.IExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.SupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.DataTypeTypeSelectionWizard;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class CreateProcessMethodHandler {

	private static final String FILTER_EXTENSION = "*.ocm";
	private static final String PROCESS_METHOD = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.PROCESS_METHOD);
	private static final String FILTER_NAME = PROCESS_METHOD + "(" + FILTER_EXTENSION + ")";
	private static final String FILE_NAME = PROCESS_METHOD + FILTER_EXTENSION;

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, IEclipseContext eclipseContext) {

		DataCategoryGroup dataCategoryGroup = DataTypeTypeSelectionWizard.open(shell, ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.CHOOSE_DESIRED_CATEGORIES_TO_CREATE_NEW_METHOD), Activator.getDefault().getPreferenceStore());
		if(dataCategoryGroup != null) {
			FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
			fileDialog.setOverwrite(true);
			fileDialog.setText(PROCESS_METHOD);
			fileDialog.setFilterExtensions(new String[]{FILTER_EXTENSION});
			fileDialog.setFilterNames(new String[]{FILTER_NAME});
			fileDialog.setFileName(FILE_NAME);
			fileDialog.setFilterPath(MethodConverter.getUserMethodDirectory().getAbsolutePath());
			//
			String pathname = fileDialog.open();
			if(pathname != null) {
				File file = new File(pathname);
				IProcessingInfo<?> processingInfo = MethodConverter.convert(file, new ProcessMethod(dataCategoryGroup.getDataCategories()), MethodConverter.DEFAULT_METHOD_CONVERTER_ID, new NullProgressMonitor());
				if(!processingInfo.hasErrorMessages()) {
					SupplierEditorSupport supplierEditorSupport = new SupplierEditorSupport(DataType.MTH, () -> Activator.getDefault().getEclipseContext());
					supplierEditorSupport.openEditor(file, false);
				}
			}
		}
	}
}
