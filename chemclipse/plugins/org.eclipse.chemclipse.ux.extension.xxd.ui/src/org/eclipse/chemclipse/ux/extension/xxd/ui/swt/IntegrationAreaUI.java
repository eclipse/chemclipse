/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.IntegrationAreaContentProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.IntegrationAreaLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.IntegrationAreaTableComparator;
import org.eclipse.swt.widgets.Composite;

public class IntegrationAreaUI extends ExtendedTableViewer {

	private IntegrationAreaTableComparator integrationAreaTableComparator;

	public IntegrationAreaUI(Composite parent, int style) {
		super(parent, style);
		integrationAreaTableComparator = new IntegrationAreaTableComparator();
		createColumns();
	}

	public void clear() {

		setInput(null);
	}

	public void sortTable() {

	}

	private void createColumns() {

		createColumns(IntegrationAreaLabelProvider.TITLES, IntegrationAreaLabelProvider.BOUNDS);
		setLabelProvider(new IntegrationAreaLabelProvider());
		setContentProvider(new IntegrationAreaContentProvider());
		setComparator(integrationAreaTableComparator);
		setEditingSupport();
	}

	private void setEditingSupport() {

	}
}
