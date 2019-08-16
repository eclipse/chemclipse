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

public interface ComparisonScanUIConfig extends AxisConfig, ToolbarConfig {

	enum DisplayOption {
		UPDATE_SCAN_1, UPDATE_SCAN_2, LIBRARY_SEARCH;
	};

	void setDisplayOption(DisplayOption option);

	void setDisplayDifference(boolean displayDifference);

	void setDisplayMirrored(boolean displayMirrored);

	void setDisplayShifted(boolean displayShifted);

	void setComparisonLabelVisible(boolean visible);
}
