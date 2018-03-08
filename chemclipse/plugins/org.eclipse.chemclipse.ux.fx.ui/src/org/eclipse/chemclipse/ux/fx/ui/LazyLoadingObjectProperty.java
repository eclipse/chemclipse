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

import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;

public abstract class LazyLoadingObjectProperty<T> extends SimpleObjectProperty<T> {

	public LazyLoadingObjectProperty() {
		super();

	}

	public LazyLoadingObjectProperty(final Object bean, final String name, final T initialValue) {
		super(bean, name, initialValue);

	}

	public LazyLoadingObjectProperty(final Object bean, final String name) {
		super(bean, name);

	}

	public LazyLoadingObjectProperty(final T initialValue) {
		super(initialValue);

	}

	private boolean loaded = false;

	/**
	 * Is called after the background task's finished (success or failure). Override
	 * if needed. E.g. to bind the value afterwards.
	 */
	protected void afterLoaded() {

	}

	/**
	 * Returns a {@link Task} that will calculate this Property's value in the
	 * background.
	 *
	 * @return a {@link Task} that will calculate this Property's value in the
	 *         background.
	 */
	protected abstract Task<T> createTask();

	protected T getFailedValue(final Throwable t) {
		return null;
	}

	@Override
	public T getValue() {
		if (!loaded) {
			startLoadingService();
		}
		return super.getValue();
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(final boolean loaded) {
		this.loaded = loaded;
	}

	@Override
	public void setValue(final T v) {
		super.setValue(v);
	}

	@Override
	public void set(final T newValue) {
		super.set(newValue);
	}

	/**
	 * Starts the {@link Task} that will calculate this Property's value in the
	 * background.
	 */
	protected void startLoadingService() {
		setLoaded(true);
		final Task<T> s = LazyLoadingObjectProperty.this.createTask();

		LazyLoadingThreads.getExecutorService().submit(s);

		s.setOnFailed(e -> {
			setValue(getFailedValue(e.getSource().getException()));
			afterLoaded();
		});

		s.setOnSucceeded(e -> {
			setValue(s.getValue());
			afterLoaded();
		});

	}

}
