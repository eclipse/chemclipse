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

public interface PeakScanListUIConfig extends ToolbarConfig {

	/**
	 * Sets whether this list displays scans
	 * 
	 * @param showScans
	 */
	void setShowScans(boolean showScans);

	/**
	 * Sets whether this list displays peaks
	 * 
	 * @param showScans
	 */
	void setShowPeaks(boolean showPeaks);

	/**
	 * Sets whether this list displays only scans/peaks from the selected range
	 * 
	 * @param showSelectedRange
	 */
	void setShowSelectedRange(boolean showSelectedRange);

	/**
	 * Set what columns should be visible
	 * 
	 * @param visibleColumns
	 */
	void setVisibleColumns(Set<String> visibleColumns);
}
