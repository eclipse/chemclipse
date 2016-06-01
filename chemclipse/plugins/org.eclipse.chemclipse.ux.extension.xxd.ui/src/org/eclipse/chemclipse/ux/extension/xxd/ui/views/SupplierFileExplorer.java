/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.views;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.ux.extension.ui.explorer.AbstractChromatogramFileExplorer;
import org.eclipse.chemclipse.ux.extension.ui.provider.IChromatogramEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.IChromatogramIdentifier;
import org.eclipse.swt.widgets.Composite;

public class SupplierFileExplorer extends AbstractChromatogramFileExplorer {

	@Inject
	public SupplierFileExplorer(Composite parent) {
		super(parent, getChromatogramIdentifierList(), getChromatogramEditorSupportList());
	}

	public static List<IChromatogramIdentifier> getChromatogramIdentifierList() {

		List<IChromatogramIdentifier> list = new ArrayList<IChromatogramIdentifier>();
		list.add(org.eclipse.chemclipse.ux.extension.msd.ui.support.ChromatogramSupport.getInstanceIdentifier());
		list.add(org.eclipse.chemclipse.ux.extension.csd.ui.support.ChromatogramSupport.getInstanceIdentifier());
		list.add(org.eclipse.chemclipse.ux.extension.wsd.ui.support.ChromatogramSupport.getInstanceIdentifier());
		return list;
	}

	public static List<IChromatogramEditorSupport> getChromatogramEditorSupportList() {

		List<IChromatogramEditorSupport> list = new ArrayList<IChromatogramEditorSupport>();
		list.add(org.eclipse.chemclipse.ux.extension.msd.ui.support.ChromatogramSupport.getInstanceEditorSupport());
		list.add(org.eclipse.chemclipse.ux.extension.csd.ui.support.ChromatogramSupport.getInstanceEditorSupport());
		list.add(org.eclipse.chemclipse.ux.extension.wsd.ui.support.ChromatogramSupport.getInstanceEditorSupport());
		return list;
	}
}
