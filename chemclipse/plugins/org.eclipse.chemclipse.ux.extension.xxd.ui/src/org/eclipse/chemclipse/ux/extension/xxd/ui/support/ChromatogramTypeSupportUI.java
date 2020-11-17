/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.EditorSupportFactory;
import org.eclipse.chemclipse.xxd.process.support.ChromatogramTypeSupport;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.widgets.Display;

public class ChromatogramTypeSupportUI extends ChromatogramTypeSupport {

	private List<ISupplierEditorSupport> supplierEditorSupportList = new ArrayList<>();

	public ChromatogramTypeSupportUI(DataType[] dataTypes) {

		super(dataTypes);
		Supplier<IEclipseContext> context = () -> Activator.getDefault().getEclipseContext();
		for(DataType dataType : dataTypes) {
			supplierEditorSupportList.add(new EditorSupportFactory(dataType, context).getInstanceEditorSupport());
		}
	}

	public void openFiles(List<File> files) throws Exception {

		Display display = DisplayUtils.getDisplay();
		//
		if(display != null) {
			for(File file : files) {
				if(file.exists()) {
					exitloop:
					for(ISupplierEditorSupport supplierEditorSupport : supplierEditorSupportList) {
						if(isSupplierFile(supplierEditorSupport, file)) {
							display.asyncExec(new Runnable() {

								@Override
								public void run() {

									supplierEditorSupport.openEditor(file);
								}
							});
							break exitloop;
						}
					}
				} else {
					throw new Exception();
				}
			}
		}
	}
}
