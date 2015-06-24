/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.components.baseline;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.chemclipse.swt.ui.components.AbstractChromatogramLineSeriesUI;
import org.eclipse.chemclipse.swt.ui.support.IAxisTitles;

/**
 * Use this class to show different views of baselines.
 * 
 * @author eselmeister
 */
public abstract class AbstractViewBaselineUI extends AbstractChromatogramLineSeriesUI {

	public AbstractViewBaselineUI(Composite parent, int style, IAxisTitles axisTitles) {

		super(parent, style, axisTitles);
	}
}
