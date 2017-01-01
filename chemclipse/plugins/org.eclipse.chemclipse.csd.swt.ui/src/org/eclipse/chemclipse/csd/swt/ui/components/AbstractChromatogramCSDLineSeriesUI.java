/*******************************************************************************
 * Copyright (c) 2014, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.swt.ui.components;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.notifier.IChromatogramSelectionCSDUpdateNotifier;
import org.eclipse.chemclipse.swt.ui.components.AbstractChromatogramLineSeriesUI;
import org.eclipse.chemclipse.swt.ui.support.AxisTitlesCurrentScale;

public abstract class AbstractChromatogramCSDLineSeriesUI extends AbstractChromatogramLineSeriesUI implements IChromatogramSelectionCSDUpdateNotifier {

	public AbstractChromatogramCSDLineSeriesUI(Composite parent, int style) {
		super(parent, style, new AxisTitlesCurrentScale());
	}

	@Override
	public void update(IChromatogramSelectionCSD chromatogramSelection, boolean forceReload) {

		updateSelection(chromatogramSelection, forceReload);
	}
}
