/*******************************************************************************
 * Copyright (c) 2018, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.chemclipse.model.types.DataType;
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

		Display display = Display.getDefault();

		if(display != null) {
			for(File file : files) {
				if(file.exists()) {
					exitloop:
					for(ISupplierEditorSupport supplierEditorSupport : supplierEditorSupportList) {
						if(isSupplierFile(supplierEditorSupport, file)) {
							display.asyncExec(() -> supplierEditorSupport.openEditor(file));
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
