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

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public interface ChromatogramOverlayUIConfig extends ToolbarConfig, AxisConfig {

	enum DisplayModus {
		NORMAL, MIRRORED;
	}

	void setDisplayModus(DisplayModus modus, IChromatogramSelection<?> selection);
}
