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

import java.util.Comparator;

public class TableMenuEntryComparator implements Comparator<ITableMenuEntry> {

	@Override
	public int compare(ITableMenuEntry tableMenuEntry0, ITableMenuEntry tableMenuEntry1) {

		return tableMenuEntry0.getName().compareTo(tableMenuEntry1.getName());
	}
}
