/*******************************************************************************
 * Copyright (c) 2008, 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.chromatogram;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.chemclipse.msd.swt.ui.components.AbstractChromatogramMSDLineSeriesUI;

public abstract class AbstractEditorChromatogramUI extends AbstractChromatogramMSDLineSeriesUI {

	public AbstractEditorChromatogramUI(Composite parent, int style) {
		super(parent, style);
	}
}
