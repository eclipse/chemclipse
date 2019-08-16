/*******************************************************************************
 * Copyright (c) 2016, 2018 Dr. Janko Diminic.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model;

public class Peak extends AbstractPeak {

	public static final int version = 1;

	public Peak(double mz, double intensity) {
		super(mz, intensity);
	}

	@Override
	public String toString() {

		return getMz() + "->" + getIntensity();
	}
	// @Override
	// public void writeExternal(ObjectOutput out) throws IOException {
	//
	// out.writeInt(version);
	// switch(version) {
	// case 1:
	// out.writeDouble(getMz());
	// out.writeDouble(getIntensity());
	// break;
	// default:
	// throw new NotSerializableException("Not implemented version: " + version);
	// }
	// }
	//
	// @Override
	// public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
	//
	// int fileVersion = in.readInt();
	// switch(fileVersion) {
	// case 1:
	// setMz(in.readDouble());
	// setIntensity(in.readDouble());
	// break;
	// default:
	// throw new NotSerializableException("Wronn version: " + fileVersion);
	// }
	// }
}
