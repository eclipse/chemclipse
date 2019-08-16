/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - force SWT#FULL_SELECTION, require actual ProcessTypeSupport
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.MethodListLabelProvider;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class MethodListUI extends ExtendedTableViewer {

	private String[] titles = MethodListLabelProvider.TITLES;
	private int[] bounds = MethodListLabelProvider.BOUNDS;

	public MethodListUI(Composite parent, int style, ProcessTypeSupport processTypeSupport) {
		super(parent, style | SWT.FULL_SELECTION);
		createColumns(titles, bounds);
		setLabelProvider(new MethodListLabelProvider(processTypeSupport));
		setContentProvider(new ListContentProvider());
	}

	public void clear() {

		setInput(null);
	}
}
