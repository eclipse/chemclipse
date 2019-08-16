/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.notifier;

import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;

/**
 * Returns the actual chromatogram selection. It useful if a e.g. filter is
 * loaded when one of its classes is loaded.<br/>
 * The static updatee still holds the fired chromatogram selection reference.<br/>
 * If the filter handler is dynamically created, it can directly hold the
 * chromatogram selection object from its static inner class.<br/>
 * See org.eclipse.chemclipse.chromatogram.msd.filter.supplier.normalizer.ui ->
 * FilterHandler
 * 
 * @author eselmeister
 */
public interface IUpdater {

	/**
	 * Returns the actual chromatogram selection.
	 * 
	 * @return {@link IChromatogramSelectionMSD}
	 */
	IChromatogramSelectionMSD getChromatogramSelection();
}
