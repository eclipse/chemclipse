/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.extraction;

public class ExtractionSettings {

	private boolean useTargets = false;
	private int retentionTimeWindow = 0;

	public ExtractionSettings(boolean useTargets, int retentionTimeWindow) {

		this.useTargets = useTargets;
		this.retentionTimeWindow = retentionTimeWindow;
	}

	public boolean isUseTargets() {

		return useTargets;
	}

	public int getRetentionTimeWindow() {

		return retentionTimeWindow;
	}
}
