/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;

public interface ISubtractUpdateSupport extends IUpdateSupport {

	void updateScanMSD(IScanMSD scanMSD);

	IScanMSD getScanMSD();

	void updateChromatogramSelectionMSD(IChromatogramSelectionMSD chromatogramSelectionMSD);

	IChromatogramSelectionMSD getChromatogramSelectionMSD();
}