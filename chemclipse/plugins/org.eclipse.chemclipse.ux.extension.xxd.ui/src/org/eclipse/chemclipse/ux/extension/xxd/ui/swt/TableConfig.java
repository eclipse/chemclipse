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
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.Set;

public interface TableConfig {

	/**
	 * Set what columns should be visible
	 * 
	 * @param visibleColumns
	 */
	void setVisibleColumns(Set<String> visibleColumns);

	/**
	 * 
	 * @return a set of columns available for this table config
	 */
	Set<String> getColumns();
}
