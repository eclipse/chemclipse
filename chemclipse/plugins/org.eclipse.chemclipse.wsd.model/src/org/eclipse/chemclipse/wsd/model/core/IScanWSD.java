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
package org.eclipse.chemclipse.wsd.model.core;

import java.util.List;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.wsd.model.core.identifier.scan.IScanTargetsWSD;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignal;

public interface IScanWSD extends IScan, IScanTargetsWSD {

	IScanSignalWSD getScanSignal(int scan);

	void addScanSignal(IScanSignalWSD scanSignalWSD);

	void removeScanSignal(IScanSignalWSD scanSignalWSD);

	List<IScanSignalWSD> getScanSignals();

	void removeScanSignal(int scan);

	IExtractedWavelengthSignal getExtractedWavelengthSignal();

	IExtractedWavelengthSignal getExtractedWavelengthSignal(double startIon, double stopIon);

	boolean hasScanSignals();

	IWavelengthBounds getWavelengthBounds();
}
