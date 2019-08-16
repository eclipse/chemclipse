/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved.
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.chemclipse.ux.fx.ui;

/**
 *
 * @author Alexander Kerner
 *
 */
public abstract class LazyLoadingStringProperty extends LazyLoadingObjectProperty<String> {

	public static final String DEFAULT_LOADING_STRING = "Loading..";

	public LazyLoadingStringProperty(final Object bean, final String name, final String initialValue) {

		super(bean, name, initialValue);
	}

	public LazyLoadingStringProperty(final Object bean, final String name) {

		super(bean, name);
	}

	public LazyLoadingStringProperty(final String initialValue) {

		super(initialValue);
	}

	public LazyLoadingStringProperty() {

		this(DEFAULT_LOADING_STRING);
	}

	@Override
	protected String getFailedValue(final Throwable t) {

		return t.getLocalizedMessage();
	}
}
