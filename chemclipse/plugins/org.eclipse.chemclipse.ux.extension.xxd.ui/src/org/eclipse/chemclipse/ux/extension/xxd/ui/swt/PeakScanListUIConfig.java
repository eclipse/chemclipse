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
	 * Defines how the UI interacts with the Chromatogram selection
	 *
	 */
	enum InteractionMode {
		/**
		 * No interaction
		 */
		NONE,
		/**
		 * The UI is the source of selections
		 */
		SOURCE,
		/**
		 * The UI is the sink of selections
		 */
		SINK,
		/**
		 * the UI is the source and the sink of selections
		 */
		BIDIRECTIONAL;
	}

	/**
	 * Sets whether this list displays scans
	 * 
	 * @param show
	 * @param inRange
	 *            if true only select in given range, ignored when show is false
	 */
	void setShowScans(boolean show, boolean inRange);

	/**
	 * Sets whether this list displays peaks
	 * 
	 * @param showScans
	 * @param inRange
	 *            if true only select in given range, ignored when show is false
	 */
	void setShowPeaks(boolean show, boolean inRange);

	/**
	 * Set what columns should be visible
	 * 
	 * @param visibleColumns
	 */
	void setVisibleColumns(Set<String> visibleColumns);

	void setMoveRetentionTimeOnPeakSelection(boolean enabled);

	/**
	 * The interaction mode to use
	 * 
	 * @param interactionMode
	 */
	void setInteractionMode(InteractionMode interactionMode);
}
