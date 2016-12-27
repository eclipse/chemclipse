/*******************************************************************************
 * Copyright (c) 2013, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.wsd.ui.views;

import javax.inject.Inject;

import org.eclipse.chemclipse.ux.extension.ui.explorer.AbstractSupplierFileExplorer;
import org.eclipse.chemclipse.ux.extension.wsd.ui.support.ChromatogramSupport;
import org.eclipse.swt.widgets.Composite;

public class ChromatogramFileExplorer extends AbstractSupplierFileExplorer {

	@Inject
	public ChromatogramFileExplorer(Composite parent) {
		super(parent, ChromatogramSupport.getInstanceEditorSupport());
	}
}
