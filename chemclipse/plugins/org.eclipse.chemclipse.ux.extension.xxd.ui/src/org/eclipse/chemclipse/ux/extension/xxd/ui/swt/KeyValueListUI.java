/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
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
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.KeyValueLabelProvider;
import org.eclipse.swt.widgets.Composite;

public class KeyValueListUI extends ExtendedTableViewer {

	private static final String[] TITLES = KeyValueLabelProvider.TITLES;
	private static final int[] BOUNDS = KeyValueLabelProvider.BOUNDS;
	//
	private KeyValueLabelProvider labelProvider = new KeyValueLabelProvider();

	public KeyValueListUI(Composite parent, int style) {

		super(parent, style);
		createColumns();
	}

	public void clear() {

		super.setInput(null);
	}

	private void createColumns() {

		createColumns(TITLES, BOUNDS);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
	}
}