/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.menu;

import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;

public interface ITableMenuEntry {

	String getCategory();

	String getName();

	default boolean isEnabled(ExtendedTableViewer extendedTableViewer) {

		return true;
	}

	void execute(ExtendedTableViewer extendedTableViewer);
}
