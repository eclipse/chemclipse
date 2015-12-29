/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.components.chromatogram;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.chemclipse.swt.ui.components.AbstractChromatogramLineSeriesUI;
import org.eclipse.chemclipse.swt.ui.support.IAxisTitles;

public abstract class AbstractViewChromatogramUI extends AbstractChromatogramLineSeriesUI {

	public AbstractViewChromatogramUI(Composite parent, int style, IAxisTitles axisTitles) {
		super(parent, style, axisTitles);
	}
}
