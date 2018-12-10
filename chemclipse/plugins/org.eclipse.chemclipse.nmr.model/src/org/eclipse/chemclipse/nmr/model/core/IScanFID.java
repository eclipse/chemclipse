/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.core;

import java.util.NavigableSet;

import org.eclipse.chemclipse.model.core.IScanSerializable;

public interface IScanFID extends IScanNMRInfo, IScanSerializable {

	void removeSignalFID(ISignalFID signalFID);

	NavigableSet<ISignalFID> getSignalsFID();

	void addSignalFID(ISignalFID signalFID);

	void removeAllSignalsFID();

	long getAcqusitionTime();

	int getNumberOfSignalsFid();

	void setSweepWidth(double sweepWidth);

	void setFirstDataPointOffset(double firstDataPointOffset);
}
