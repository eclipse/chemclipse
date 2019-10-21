/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.methods;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

@Component(service = {IProcessTypeSupplier.class})
public class MethodProcessTypeSupplier implements IProcessTypeSupplier, BundleTrackerCustomizer<Collection<IProcessSupplier<?>>> {

	private BundleTracker<Collection<IProcessSupplier<?>>> bundleTracker;
	private final AtomicReference<LogService> logService = new AtomicReference<>();

	@Override
	public String getCategory() {

		return "User Methods";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		List<IProcessSupplier<?>> list = new ArrayList<>();
		Collection<IProcessMethod> userMethods = MethodConverter.getUserMethods();
		for(IProcessMethod processMethod : userMethods) {
			list.add(new UserMethodProcessSupplier(processMethod, this));
		}
		Collection<Collection<IProcessSupplier<?>>> values = bundleTracker.getTracked().values();
		values.forEach(list::addAll);
		return list;
	}

	private static final class UserMethodProcessSupplier extends AbstractProcessSupplier<Void> implements ProcessEntryContainer {

		private final IProcessMethod method;

		public UserMethodProcessSupplier(IProcessMethod method, MethodProcessTypeSupplier parent) {
			super("ProcessMethod." + method.getUUID(), method.getName(), method.getDescription(), null, parent, getDataTypes(method));
			this.method = method;
		}

		@Override
		public Iterator<IProcessEntry> iterator() {

			return method.iterator();
		}

		@Override
		public int getNumberOfEntries() {

			return method.getNumberOfEntries();
		}
	}

	public static DataCategory[] getDataTypes(IProcessMethod method) {

		if(method.getNumberOfEntries() == 0) {
			return DataCategory.values();
		}
		Set<DataCategory> categories = new HashSet<>();
		for(IProcessEntry entry : method) {
			categories.addAll(entry.getDataCategories());
		}
		return categories.toArray(new DataCategory[0]);
	}

	@Activate
	public void start(BundleContext bundleContext) {

		bundleTracker = new BundleTracker<>(bundleContext, Bundle.ACTIVE, this);
		bundleTracker.open();
	}

	@Deactivate
	public void stop() {

		bundleTracker.close();
	}

	@Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL)
	public void setLogService(LogService logService) {

		this.logService.set(logService);
	}

	public void unsetLogService(LogService logService) {

		this.logService.compareAndSet(logService, null);
	}

	@Override
	public Collection<IProcessSupplier<?>> addingBundle(Bundle bundle, BundleEvent event) {

		List<IProcessSupplier<?>> list = new ArrayList<>();
		Enumeration<URL> entries = bundle.findEntries("OSGI-INF/processors", "*." + MethodConverter.DEFAULT_METHOD_FILE_NAME_EXTENSION, true);
		if(entries != null) {
			while(entries.hasMoreElements()) {
				URL url = entries.nextElement();
				try {
					IProcessingInfo<IProcessMethod> load = MethodConverter.load(url.openStream(), url.getPath(), null);
					IProcessMethod result = load.getProcessingResult();
					if(result != null) {
						list.add(new MetaProcessorProcessSupplier(bundle.getSymbolicName(), result, this));
					}
				} catch(IOException e) {
					LogService log = logService.get();
					if(log != null) {
						log.log(LogService.LOG_ERROR, "loading of method from URL " + url + " failed", e);
					}
				}
			}
		}
		return list;
	}

	@Override
	public void modifiedBundle(Bundle bundle, BundleEvent event, Collection<IProcessSupplier<?>> object) {

		// don't mind
	}

	@Override
	public void removedBundle(Bundle bundle, BundleEvent event, Collection<IProcessSupplier<?>> object) {

		// nothing to do here
	}
}
