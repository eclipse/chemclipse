/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.model;

import java.util.Objects;

public class ResultDelta {

	private IResultPCA resultPCA = null;
	private double deltaX = 0;
	private double deltaY = 0;

	public ResultDelta(IResultPCA resultPCA, double deltaX, double deltaY) {

		this.resultPCA = resultPCA;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}

	public IResultPCA getResultPCA() {

		return resultPCA;
	}

	public double getDeltaX() {

		return deltaX;
	}

	public double getDeltaY() {

		return deltaY;
	}

	@Override
	public int hashCode() {

		return Objects.hash(resultPCA);
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		ResultDelta other = (ResultDelta)obj;
		return Objects.equals(resultPCA, other.resultPCA);
	}

	@Override
	public String toString() {

		return "ResultDelta [resultPCA=" + resultPCA + ", deltaX=" + deltaX + ", deltaY=" + deltaY + "]";
	}
}