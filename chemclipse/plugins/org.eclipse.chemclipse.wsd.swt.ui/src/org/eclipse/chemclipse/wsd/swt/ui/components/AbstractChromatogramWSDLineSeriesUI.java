/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.swt.ui.components;

import org.eclipse.chemclipse.swt.ui.components.AbstractChromatogramLineSeriesUI;
import org.eclipse.chemclipse.swt.ui.support.AxisTitlesIntensityScale;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.notifier.IChromatogramSelectionWSDUpdateNotifier;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractChromatogramWSDLineSeriesUI extends AbstractChromatogramLineSeriesUI implements IChromatogramSelectionWSDUpdateNotifier {

	public AbstractChromatogramWSDLineSeriesUI(Composite parent, int style) {
		super(parent, style, new AxisTitlesIntensityScale());
	}

	@Override
	public void update(IChromatogramSelectionWSD chromatogramSelection, boolean forceReload) {

		updateSelection(chromatogramSelection, forceReload);
	}
}
