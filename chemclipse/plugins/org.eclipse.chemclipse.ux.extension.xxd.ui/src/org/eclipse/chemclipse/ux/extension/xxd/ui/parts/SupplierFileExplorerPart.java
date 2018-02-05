/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.ux.extension.ui.explorer.AbstractSupplierFileExplorer;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors.EditorSupportFactory;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.DataType;
import org.eclipse.swt.widgets.Composite;

public class SupplierFileExplorerPart extends AbstractSupplierFileExplorer {

	@Inject
	public SupplierFileExplorerPart(Composite parent) {
		super(parent, getSupplierFileEditorSupport());
	}

	public static List<ISupplierFileEditorSupport> getSupplierFileEditorSupport() {

		List<ISupplierFileEditorSupport> list = new ArrayList<ISupplierFileEditorSupport>();
		// list.add(org.eclipse.chemclipse.ux.extension.msd.ui.support.ChromatogramSupport.getInstanceEditorSupport());
		// list.add(org.eclipse.chemclipse.ux.extension.csd.ui.support.ChromatogramSupport.getInstanceEditorSupport());
		// list.add(org.eclipse.chemclipse.ux.extension.wsd.ui.support.ChromatogramSupport.getInstanceEditorSupport());
		list.add(org.eclipse.chemclipse.ux.extension.msd.ui.support.DatabaseSupport.getInstanceEditorSupport());
		list.add(org.eclipse.chemclipse.ux.extension.msd.ui.support.MassSpectrumSupport.getInstanceEditorSupport());
		list.add(new EditorSupportFactory(DataType.CSD).getInstanceEditorSupport());
		list.add(new EditorSupportFactory(DataType.MSD).getInstanceEditorSupport());
		list.add(new EditorSupportFactory(DataType.WSD).getInstanceEditorSupport());
		return list;
	}
}
