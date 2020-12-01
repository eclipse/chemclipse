/*******************************************************************************
 * Copyright (c) 2013, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.notifier;

import java.util.List;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public interface IChromatogramSelectionUpdateNotifier {

	void updateSelection(IChromatogramSelection<?, ?> chromatogramSelection, boolean forceReload);

	void updateSelection(List<IChromatogramSelection<?, ?>> chromatogramSelections, boolean forceReload);
}
