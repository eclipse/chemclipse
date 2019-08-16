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
package org.eclipse.chemclipse.model.quantitation;

public abstract class AbstractQuantitationSignal implements IQuantitationSignal {

	private double signal = 0.0d;
	private double relativeResponse = ABSOLUTE_RELATIVE_RESPONSE;
	private double uncertainty = 0.0d;
	private boolean use = true;

	public AbstractQuantitationSignal(double signal, double relativeResponse) {
		this(signal, relativeResponse, 0.0d, true);
	}

	public AbstractQuantitationSignal(double signal, double relativeResponse, double uncertainty) {
		this(signal, relativeResponse, uncertainty, true);
	}

	public AbstractQuantitationSignal(double signal, double relativeResponse, double uncertainty, boolean use) {
		this.signal = signal;
		this.relativeResponse = relativeResponse;
		this.uncertainty = uncertainty;
		this.use = use;
	}

	@Override
	public double getSignal() {

		return signal;
	}

	@Override
	public double getRelativeResponse() {

		return relativeResponse;
	}

	@Override
	public void setRelativeResponse(double relativeResponse) {

		this.relativeResponse = relativeResponse;
	}

	@Override
	public double getUncertainty() {

		return uncertainty;
	}

	@Override
	public void setUncertainty(double uncertainty) {

		this.uncertainty = uncertainty;
	}

	@Override
	public boolean isUse() {

		return use;
	}

	@Override
	public void setUse(boolean use) {

		this.use = use;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(signal);
		result = prime * result + (int)(temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		AbstractQuantitationSignal other = (AbstractQuantitationSignal)obj;
		if(Double.doubleToLongBits(signal) != Double.doubleToLongBits(other.signal))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "AbstractQuantitationSignal [signal=" + signal + ", relativeResponse=" + relativeResponse + ", uncertainty=" + uncertainty + ", use=" + use + "]";
	}

	@Override
	public int compareTo(IQuantitationSignal quantitationSignal) {

		int result = 0;
		if(quantitationSignal != null) {
			result = Double.compare(getSignal(), quantitationSignal.getSignal());
		}
		return result;
	}
}
