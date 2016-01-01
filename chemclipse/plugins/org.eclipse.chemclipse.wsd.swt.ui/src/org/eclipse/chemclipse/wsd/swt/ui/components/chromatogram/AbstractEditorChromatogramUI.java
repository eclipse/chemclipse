/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.swt.ui.components.chromatogram;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.chemclipse.wsd.swt.ui.components.AbstractChromatogramWSDLineSeriesUI;

public abstract class AbstractEditorChromatogramUI extends AbstractChromatogramWSDLineSeriesUI {

	public AbstractEditorChromatogramUI(Composite parent, int style) {
		super(parent, style);
	}
}
