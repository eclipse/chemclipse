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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public abstract class LazyLoadingBooleanProperty extends LazyLoadingObjectProperty<Boolean> {

	private static final Logger logger = Logger.getLogger(LazyLoadingBooleanProperty.class);
	public static final Boolean DEFAULT_LOADING_VALUE = Boolean.FALSE;
	public static final Boolean DEFAULT_ERROR_VALUE = Boolean.FALSE;

	public LazyLoadingBooleanProperty() {

		super();
	}

	public LazyLoadingBooleanProperty(final Boolean initialValue) {

		super(initialValue);
	}

	public LazyLoadingBooleanProperty(final Object bean, final String name, final Boolean initialValue) {

		super(bean, name, initialValue);
	}

	public LazyLoadingBooleanProperty(final Object bean, final String name) {

		super(bean, name);
	}

	@Override
	protected Boolean getFailedValue(final Throwable t) {

		if(logger.isEnabledFor(Level.ERROR)) {
			logger.error(t.getLocalizedMessage(), t);
		}
		return DEFAULT_ERROR_VALUE;
	}
}
