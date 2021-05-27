/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - add profile support
 *******************************************************************************/
package org.eclipse.chemclipse.converter.methods;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.converter.PathResolver;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.logging.support.Settings;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionConsumer;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutor;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;
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

	private static final Logger logger = Logger.getLogger(MethodProcessTypeSupplier.class);
	private static final String SKIP_MESSAGE = "SKIP CHECK: Is this method used?";
	//
	private static final String PROCESSORS_ENTRY_PATH = "/OSGI-INF/processors/";
	private BundleTracker<Collection<IProcessSupplier<?>>> bundleTracker;
	private final List<IProcessSupplier<?>> systemMethods = new ArrayList<>();

	@Override
	public String getCategory() {

		return "User Methods";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		List<IProcessSupplier<?>> list = new ArrayList<>();
		Collection<IProcessMethod> userMethods = MethodConverter.getUserMethods();
		Set<String> ids = new HashSet<>();
		for(IProcessMethod processMethod : userMethods) {
			UserMethodProcessSupplier supplier = new UserMethodProcessSupplier(processMethod, this);
			if(ids.contains(supplier.getId())) {
				logger.warn("Duplicate id for method " + processMethod.getName() + " (id: " + supplier.getId() + ")");
				continue;
			}
			list.add(supplier);
			ids.add(supplier.getId());
		}
		Collection<Collection<IProcessSupplier<?>>> values = bundleTracker.getTracked().values();
		values.forEach(list::addAll);
		list.addAll(systemMethods);
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> IProcessSupplier<T> getSupplier(String id) {

		IProcessSupplier<T> supplier = IProcessTypeSupplier.super.getSupplier(id);
		if(supplier == null) {
			String[] split = id.split(":", 2);
			if(split.length == 2) {
				String baseId = split[0];
				for(IProcessSupplier<?> s : getProcessorSuppliers()) {
					if(s.getId().startsWith(baseId)) {
						if(supplier == null) {
							// choose this supplier for now but check if there are more...
							supplier = (IProcessSupplier<T>)s;
						} else {
							// Ambiguous id, to be safe we return null here instead of doing something nasty...
							return null;
						}
					}
				}
			}
		}
		return supplier;
	}

	private static String getUserMethodID(IProcessMethod method) {

		File sourceFile = method.getSourceFile();
		if(sourceFile != null) {
			return getID(method, "user:" + sourceFile.getName());
		}
		return getID(method, "user");
	}

	private static String getID(IProcessMethod method, String qualifier) {

		String id = "ProcessMethod." + method.getUUID();
		if(qualifier != null) {
			return id + ":" + qualifier;
		}
		return id;
	}

	private static final class UserMethodProcessSupplier extends AbstractProcessSupplier<Void> implements ProcessEntryContainer, ProcessExecutor {

		private final IProcessMethod method;

		public UserMethodProcessSupplier(IProcessMethod method, MethodProcessTypeSupplier parent) {

			super(getUserMethodID(method), method.getName(), method.getDescription(), null, parent, getDataTypes(method));
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

		@Override
		public <X> void execute(ProcessorPreferences<X> preferences, ProcessExecutionContext context) throws Exception {

			ProcessExecutionConsumer<?> consumer = context.getContextObject(ProcessExecutionConsumer.class);
			ProcessEntryContainer.applyProcessEntries(method, context, consumer);
		}

		@Override
		public String getActiveProfile() {

			System.out.println(SKIP_MESSAGE);
			return "";
		}

		@Override
		public void setActiveProfile(String activeProfile) {

			System.out.println(SKIP_MESSAGE);
		}

		@Override
		public void addProfile(String profile) {

			System.out.println(SKIP_MESSAGE);
		}

		@Override
		public void deleteProfile(String profile) {

			System.out.println(SKIP_MESSAGE);
		}

		@Override
		public Set<String> getProfiles() {

			System.out.println(SKIP_MESSAGE);
			return Collections.emptySet();
		}

		@Override
		public boolean isSupportResume() {

			return method.isSupportResume();
		}

		@Override
		public void setSupportResume(boolean supportResume) {

			System.out.println(SKIP_MESSAGE);
		}

		@Override
		public int getResumeIndex() {

			return method.getResumeIndex();
		}

		@Override
		public void setResumeIndex(int resumeIndex) {

			System.out.println(SKIP_MESSAGE);
		}
	}

	public static DataCategory[] getDataTypes(IProcessMethod method) {

		Set<DataCategory> categories = method.getDataCategories();
		if(categories.isEmpty()) {
			// for backward compatibility
			categories = EnumSet.of(DataCategory.CSD, DataCategory.MSD, DataCategory.WSD);
		}
		if(method.getNumberOfEntries() == 0) {
			// when there are no entries, we return the categories of the method even though this will be a noop when executed
			return categories.toArray(new DataCategory[0]);
		}
		// now we search if maybe only entries of a given type are in this method, e.g. it is possible to create a processmethod
		// with MSD+WSD type, but only add processors that are valid for WSD, then we want to return only WSD...
		Set<DataCategory> entryCategories = new HashSet<>();
		for(IProcessEntry entry : method) {
			for(DataCategory entryDataCategory : entry.getDataCategories()) {
				if(categories.contains(entryDataCategory)) {
					entryCategories.add(entryDataCategory);
				}
			}
		}
		return categories.toArray(new DataCategory[0]);
	}

	@Activate
	public void start(BundleContext bundleContext) {

		bundleTracker = new BundleTracker<>(bundleContext, Bundle.ACTIVE, this);
		bundleTracker.open();
		//
		File systemMethodFolder = Settings.getSystemMethodDirectory();
		if(systemMethodFolder.isDirectory()) {
			File[] listFiles = systemMethodFolder.listFiles();
			if(listFiles != null) {
				for(File file : listFiles) {
					if(file.isFile() && file.getName().toLowerCase().endsWith("." + MethodConverter.DEFAULT_METHOD_FILE_NAME_EXTENSION)) {
						try {
							try (InputStream inputStream = new FileInputStream(file)) {
								IProcessingInfo<IProcessMethod> load = MethodConverter.load(inputStream, file.getAbsolutePath(), null);
								IProcessMethod processMethod = load.getProcessingResult();
								if(processMethod != null) {
									/*
									 * Set the File to allow editing the profiles.
									 */
									if(processMethod instanceof ProcessMethod) {
										((ProcessMethod)processMethod).setSourceFile(file);
									}
									systemMethods.add(new MetaProcessorProcessSupplier(getID(processMethod, "system:" + file.getName()), processMethod, this));
								}
							}
						} catch(IOException e) {
							logger.error("Loading of method from system path " + file.getAbsolutePath() + " failed", e);
						}
					}
				}
			}
		}
	}

	@Deactivate
	public void stop() {

		bundleTracker.close();
	}

	@Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL)
	public void setLogService(LogService logService) {

		// Not used
	}

	public void unsetLogService(LogService logService) {

		// Not used
	}

	@Override
	public Collection<IProcessSupplier<?>> addingBundle(Bundle bundle, BundleEvent event) {

		List<IProcessSupplier<?>> processSupplierList = new ArrayList<>();
		Enumeration<URL> entries = bundle.findEntries(PROCESSORS_ENTRY_PATH, "*." + MethodConverter.DEFAULT_METHOD_FILE_NAME_EXTENSION, true);
		if(entries != null) {
			while(entries.hasMoreElements()) {
				URL url = entries.nextElement();
				try {
					try (InputStream inputStream = url.openStream()) {
						/*
						 * Try to resolve the file.
						 */
						String urlPath = url.getPath().toString();
						String absolutePath = PathResolver.getAbsolutePath(bundle, urlPath);
						File sourceFile = new File(absolutePath);
						//
						String path = url.getPath().replace(PROCESSORS_ENTRY_PATH, "").replace("." + MethodConverter.DEFAULT_METHOD_FILE_NAME_EXTENSION, "");
						String externalForm = url.toExternalForm();
						IProcessingInfo<IProcessMethod> processingInfo = MethodConverter.load(inputStream, externalForm, null);
						IProcessMethod processMethod = processingInfo.getProcessingResult();
						if(processMethod != null) {
							/*
							 * Set the File (if available) to allow editing the profiles.
							 * The containing bundle should define in the MANIFEST.MF:
							 * Eclipse-BundleShape: dir
							 */
							if(processMethod instanceof ProcessMethod && sourceFile.exists()) {
								((ProcessMethod)processMethod).setSourceFile(sourceFile);
							}
							processSupplierList.add(new MetaProcessorProcessSupplier(getID(processMethod, "bundle:" + bundle.getSymbolicName() + ":" + path), processMethod, this));
						}
					}
				} catch(IOException e) {
					logger.error("Loading of method from URL " + url + " failed", e);
				}
			}
		}
		return processSupplierList;
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
