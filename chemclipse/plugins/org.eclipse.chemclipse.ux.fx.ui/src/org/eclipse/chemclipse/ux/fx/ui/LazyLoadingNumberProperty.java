package org.eclipse.chemclipse.ux.fx.ui;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public abstract class LazyLoadingNumberProperty extends LazyLoadingObjectProperty<Number> {

	private static final Logger logger = Logger.getLogger(LazyLoadingNumberProperty.class);
	public static final Number DEFAULT_LOADING_VALUE = -2;
	public static final Number DEFAULT_ERROR_VALUE = -3;

	public LazyLoadingNumberProperty() {

		this(DEFAULT_LOADING_VALUE);
	}

	public LazyLoadingNumberProperty(final Number initialValue) {

		super(initialValue);
	}

	public LazyLoadingNumberProperty(final Object bean, final String name, final Number initialValue) {

		super(bean, name, initialValue);
	}

	public LazyLoadingNumberProperty(final Object bean, final String name) {

		super(bean, name);
	}

	@Override
	protected Number getFailedValue(final Throwable t) {

		if(logger.isEnabledFor(Level.ERROR)) {
			logger.error(t.getLocalizedMessage(), t);
		}
		return DEFAULT_ERROR_VALUE;
	}
}
