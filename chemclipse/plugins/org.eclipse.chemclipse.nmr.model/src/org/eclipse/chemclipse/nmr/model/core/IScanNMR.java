/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.core;

import java.util.NavigableSet;

import org.eclipse.chemclipse.model.core.IScanSerializable;

public interface IScanNMR extends IScanSerializable {

	NavigableSet<ISignalNMR> getSignalsNMR();

	void addSignalNMR(ISignalNMR signalNMR);

	void removeSignalNMR(ISignalNMR signalNMR);

	void removeAllSignalsNMR();

	int getNumberOfFourierPoints();

	double getSweepWidth();

	double getFirstOrderPhaseCorrection();

	void setFirstOrderPhaseCorrection(double firstOrderPhaseCorrection);

	double getZeroOrderPhaseCorrection();

	void setZeroOrderPhaseCorrection(double zeroOrderPhaseCorrection);
}