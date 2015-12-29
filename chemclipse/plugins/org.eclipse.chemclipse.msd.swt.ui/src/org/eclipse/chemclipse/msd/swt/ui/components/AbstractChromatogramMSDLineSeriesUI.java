/*******************************************************************************
 * Copyright (c) 2013, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.notifier.IChromatogramSelectionMSDUpdateNotifier;
import org.eclipse.chemclipse.swt.ui.components.AbstractChromatogramLineSeriesUI;
import org.eclipse.chemclipse.swt.ui.support.AxisTitlesMassScale;

public abstract class AbstractChromatogramMSDLineSeriesUI extends AbstractChromatogramLineSeriesUI implements IChromatogramSelectionMSDUpdateNotifier {

	public AbstractChromatogramMSDLineSeriesUI(Composite parent, int style) {
		super(parent, style, new AxisTitlesMassScale());
	}

	@Override
	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		updateSelection(chromatogramSelection, forceReload);
	}
}
