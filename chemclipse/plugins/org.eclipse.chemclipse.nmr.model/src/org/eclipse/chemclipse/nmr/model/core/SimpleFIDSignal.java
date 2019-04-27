/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *
 * Contributors:
 * Alexander Stark - initial API and implementation
 * Christoph Läubrich - changes for new processor api and process optimizations
 * Alexander Kerner - serialization
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.core;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleFIDSignal implements FIDSignal, Externalizable {

	private static final long serialVersionUID = -2308641154730056520L;
	private BigDecimal time;
	private Number real;
	private Number imag;

	public static List<SimpleFIDSignal> copy(Collection<? extends FIDSignal> signals) {
		List<SimpleFIDSignal> result = new ArrayList<>();
		for (FIDSignal s : signals) {
			if (s instanceof SimpleFIDSignal) {
				result.add((SimpleFIDSignal) s);
			} else
				result.add(new SimpleFIDSignal(s));
		}
		return result;
	}

	public SimpleFIDSignal(BigDecimal time, Number real, Number imag) {
		this.time = time;
		this.real = real;
		this.imag = imag;
	}

	/**
	 * No-arg constructor for serialization only.
	 */
	public SimpleFIDSignal() {

	}

	/**
	 * Copy constructor.
	 *
	 * @param template the {@link FIDSignal} to copy values from
	 */
	public SimpleFIDSignal(FIDSignal template) {
		this(template.getSignalTime(), template.getRealComponent(), template.getImaginaryComponent());
	}

	@Override
	public BigDecimal getSignalTime() {

		return time;
	}

	@Override
	public Number getRealComponent() {

		return real;
	}

	@Override
	public Number getImaginaryComponent() {

		return imag;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(time);
		out.writeObject(real);
		out.writeObject(imag);

	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		time = (BigDecimal) in.readObject();
		real = (Number) in.readObject();
		imag = (Number) in.readObject();

	}


}