/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.matrix;


public interface IExtractedMatrix {
	
	float[] getMatrix();
	
	int getNumberOfScans();
	
	int getNumberOfIons();
	
	void updateSignal(float[] signal, int numberOfScans, int numberOfIons );
	
	int[] getScanNumbers();
	
	int[] getRetentionTimes();
	
	

}
