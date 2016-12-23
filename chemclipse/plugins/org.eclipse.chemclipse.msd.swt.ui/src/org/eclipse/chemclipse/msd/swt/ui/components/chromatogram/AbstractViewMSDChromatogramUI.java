/*******************************************************************************
 * Copyright (c) 2008, 2016 Dr. Philip Wenig.
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

import org.eclipse.chemclipse.msd.swt.ui.components.AbstractChromatogramMSDLineSeriesUI;

import org.eclipse.swt.widgets.Composite;

/**
 * Use this class as the basis for presentations of chromatogram selections that
 * are used in a view.
 * 
 * @author eselmeister
 */
public abstract class AbstractViewMSDChromatogramUI extends AbstractChromatogramMSDLineSeriesUI {

	public AbstractViewMSDChromatogramUI(Composite parent, int style) {
		super(parent, style);
	}
}
