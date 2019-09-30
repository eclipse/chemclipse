/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Contains utility methods to construct standard SWT items in a uniform way
 *
 */
public class ControlBuilder {

	public static Composite createContainer(Composite parent) {

		return createContainer(parent, 1);
	}

	public static Composite createContainer(Composite parent, int columns) {

		return createContainer(parent, columns, false);
	}

	/**
	 * Create a "container-composite", that is one with a gridlayout that does not add additional spaces
	 * 
	 * @param parent
	 * @param columns
	 * @param equal
	 * @return
	 */
	public static Composite createContainer(Composite parent, int columns, boolean equal) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(columns, equal);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginTop = 0;
		layout.marginRight = 0;
		composite.setLayout(layout);
		return fill(composite);
	}

	public static <T extends Control> T fill(T control) {

		if(control.getParent().getLayout() instanceof GridLayout) {
			Object layoutData = control.getLayoutData();
			GridData gridData;
			if(layoutData instanceof GridData) {
				gridData = (GridData)layoutData;
			} else {
				gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
				control.setLayoutData(gridData);
			}
			gridData.grabExcessHorizontalSpace = true;
		}
		return control;
	}
}
