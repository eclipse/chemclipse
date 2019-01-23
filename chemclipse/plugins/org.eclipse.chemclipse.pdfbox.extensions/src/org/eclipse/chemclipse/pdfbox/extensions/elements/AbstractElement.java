/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pdfbox.extensions.elements;

public abstract class AbstractElement<T> implements IElement<T> {

	private float x = 0.0f;
	private float y = 0.0f;

	@Override
	public float getX() {

		return x;
	}

	public T setX(float x) {

		this.x = x;
		return reference();
	}

	@Override
	public float getY() {

		return y;
	}

	public T setY(float y) {

		this.y = y;
		return reference();
	}

	@SuppressWarnings("unchecked")
	protected T reference() {

		return (T)this;
	}
}
