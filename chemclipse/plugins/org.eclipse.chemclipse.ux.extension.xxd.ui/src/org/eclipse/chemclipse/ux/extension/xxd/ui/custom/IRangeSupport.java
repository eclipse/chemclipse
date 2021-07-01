/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.custom;

import org.eclipse.swtchart.Range;

public interface IRangeSupport {

	void clearSelectedRange();

	void assignCurrentRangeSelection();

	Range getCurrentRangeX();

	void updateRangeX(Range selectedRangeX);

	Range getCurrentRangeY();

	void updateRangeY(Range selectedRangeY);

	void updateRange(Range selectedRangeX, Range selectedRangeY);

	void focusTraces(int percentOffset);

	void adjustChartRange();
}
