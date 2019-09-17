package org.eclipse.chemclipse.ux.fx.ui;

public abstract class LazyLoadingNumberProperty extends LazyLoadingObjectProperty<Number> {

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

		return DEFAULT_ERROR_VALUE;
	}
}
