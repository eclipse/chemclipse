/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.logging.ui.swt.components;

import org.eclipse.chemclipse.logging.ui.provider.LogListLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class LogListUI extends ExtendedTableViewer {

	public static final String DATE = "Date";
	public static final String TIME = "Time";
	public static final String LEVEL = "Level";
	public static final String THREAD = "Thread";
	public static final String LOCATION = "Location";
	public static final String MESSAGE = "Message";
	//
	private String[] titles = {//
			DATE, //
			TIME, //
			LEVEL, //
			THREAD, //
			LOCATION, //
			MESSAGE, //
	};
	private int[] bounds = {//
			100, //
			120, //
			80, //
			120, //
			200, //
			500, //
	};

	public LogListUI(Composite parent) {

		this(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
	}

	public LogListUI(Composite parent, int style) {

		super(parent, style);
		createColumns();
		setUseHashlookup(true);
		setComparator(null);
	}

	private void createColumns() {

		createColumns(titles, bounds);
		setLabelProvider(new LogListLabelProvider());
		setContentProvider(new ArrayContentProvider());
	}
}
