/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PeakTableTargetComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PeakTableTargetLabelProvider;
import org.eclipse.swt.widgets.Composite;

public class PeakTableTargetViewerUI extends ExtendedTableViewer {

	private String[] titles = {"Retention Time (Minutes)", "Target", "S/N", "Peak Area"};
	private int[] bounds = {150, 150, 150, 150};

	public PeakTableTargetViewerUI(Composite parent) {
		super(parent);
		initialize();
	}

	public PeakTableTargetViewerUI(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {

		createColumns(titles, bounds);
		setLabelProvider(new PeakTableTargetLabelProvider());
		setContentProvider(new ListContentProvider());
		setComparator(new PeakTableTargetComparator());
	}
}
