/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.notifier;

import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;

public interface IChromatogramSelectionWSDUpdateNotifier {

	void update(IChromatogramSelectionWSD chromatogramSelection, boolean forceReload);
}
