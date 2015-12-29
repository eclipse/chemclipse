/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.csd.ui.views;

import javax.inject.Inject;

import org.eclipse.chemclipse.ux.extension.csd.ui.support.ChromatogramSupport;
import org.eclipse.chemclipse.ux.extension.ui.explorer.AbstractChromatogramFileExplorer;

import org.eclipse.swt.widgets.Composite;

/**
 * This view part supports a simple chromatogram file explorer.
 * 
 * @author eselmeister
 */
public class ChromatogramFileExplorer extends AbstractChromatogramFileExplorer {

	@Inject
	public ChromatogramFileExplorer(Composite parent) {
		super(parent, ChromatogramSupport.getInstanceIdentifier(), ChromatogramSupport.getInstanceEditorSupport());
	}
}
