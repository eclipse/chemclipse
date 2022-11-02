/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

public interface IFlavorMarker {

	void clear();

	public String getOdor();

	public void setOdor(String odor);

	public String getMatrix();

	public void setMatrix(String matrix);

	public String getSolvent();

	public void setSolvent(String solvent);
}