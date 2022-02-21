/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.util.Date;

/**
 * An interface for single MALDI-TOF MS spectra which contain additional metadata.
 * 
 * @author Matthias Mailänder
 */
public interface IVendorStandaloneMassSpectrum extends IVendorMassSpectrum {

	String getSampleName();

	void setSampleName(String name);

	String getDescription();

	void setDescription(String description);

	String getOperator();

	void setOperator(String operator);

	Date getDate();

	void setDate(Date date);

	String getInstrument();

	void setInstrument(String instrument);
}
