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
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.eclipse.chemclipse.filter.Filter;
import org.eclipse.chemclipse.filter.FilterFactory;
import org.eclipse.chemclipse.filter.FilterList;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.filter.IChromatogramSelectionFilter;
import org.eclipse.chemclipse.model.filter.IMeasurementFilter;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.filter.IScanFilter;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.processing.core.IProcessingResult;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;

/**
 * This class handles all the hard parts of creating of filter menu items out of filterable objects
 *
 * @author Christoph Läubrich
 *
 */
public class FilterMenuFactory {

	private static final Logger logger = Logger.getLogger(FilterMenuFactory.class);
	private static final String CATEGORY_SELECTION = "Filter Selection";
	private static final String CATEGORY_MEASUREMENT = "Filter Measurement";
	private static final String CATEGORY_ALL_PEAKS = "Filter all Peaks";
	private static final String CATEGORY_SELECTION_PEAKS = "Filter Peaks in Selection";
	private static final String CATEGORY_ALL_SCANS = "Filter all Scans";

	public static List<IChartMenuEntry> createChromatogramSelectionEntries(FilterFactory filterFactory, Supplier<IChromatogramSelection<?, ?>> supplier) {

		List<IChartMenuEntry> result = new ArrayList<>();
		Collection<IChromatogramSelectionFilter<?>> chromatogramSelectionFilter = filterFactory.getFilters(FilterFactory.genericClass(IChromatogramSelectionFilter.class), new BiFunction<IChromatogramSelectionFilter<?>, Map<String, ?>, Boolean>() {

			private IChromatogramSelection<?, ?> chromatogramSelection = supplier.get();

			@Override
			public Boolean apply(IChromatogramSelectionFilter<?> t, Map<String, ?> u) {

				if(chromatogramSelection != null) {
					return t.acceptsIChromatogramSelection(chromatogramSelection);
				}
				return false;
			}
		});
		for(IChromatogramSelectionFilter<?> filter : chromatogramSelectionFilter) {
			result.add(new ChromatogramSelectionFilterMenuEntry<>(filter, supplier));
		}
		Supplier<IChromatogram<?>> chromatogramSupplier = new Supplier<IChromatogram<?>>() {

			@Override
			public IChromatogram<?> get() {

				IChromatogramSelection<?, ?> selection = supplier.get();
				if(selection != null) {
					return selection.getChromatogram();
				}
				return null;
			}
		};
		Supplier<FilterList<IPeak>> allPeaksSuplier = new Supplier<FilterList<IPeak>>() {

			@Override
			public FilterList<IPeak> get() {

				IChromatogram<?> chromatogram = chromatogramSupplier.get();
				if(chromatogram != null) {
					return IChromatogramSelectionFilter.peakList(chromatogram, null);
				} else {
					return null;
				}
			}
		};
		Supplier<FilterList<IPeak>> peaksInSelectionSupplier = new Supplier<FilterList<IPeak>>() {

			@Override
			public FilterList<IPeak> get() {

				IChromatogram<?> chromatogram = chromatogramSupplier.get();
				if(chromatogram != null) {
					return IChromatogramSelectionFilter.peakList(chromatogram, supplier.get());
				} else {
					return null;
				}
			}
		};
		result.addAll(createMeasurementEntries(filterFactory, chromatogramSupplier));
		result.addAll(createPeakEntries(CATEGORY_ALL_PEAKS, filterFactory, allPeaksSuplier));
		result.addAll(createPeakEntries(CATEGORY_SELECTION_PEAKS, filterFactory, peaksInSelectionSupplier));
		result.addAll(createScanEntries(CATEGORY_ALL_SCANS, filterFactory, new Supplier<FilterList<IScan>>() {

			@Override
			public FilterList<IScan> get() {

				return IChromatogramSelectionFilter.scanList(chromatogramSupplier.get());
			}
		}));
		sortEntries(result);
		return result;
	}

	private static Supplier<FilterList<IScan>> peakScans(Supplier<FilterList<IPeak>> supplier) {

		return new Supplier<FilterList<IScan>>() {

			@Override
			public FilterList<IScan> get() {

				FilterList<IPeak> peakList = supplier.get();
				return new FilterList<IScan>() {

					@Override
					public Iterator<IScan> iterator() {

						Iterator<IPeak> iterator = peakList.iterator();
						return new Iterator<IScan>() {

							@Override
							public boolean hasNext() {

								return iterator.hasNext();
							}

							@Override
							public IScan next() {

								IPeak next = iterator.next();
								if(next instanceof IPeakMSD) {
									return ((IPeakMSD)next).getExtractedMassSpectrum();
								}
								return null;
							}
						};
					}

					@Override
					public int size() {

						return peakList.size();
					}
				};
			}
		};
	}

	public static List<IChartMenuEntry> createMeasurementEntries(FilterFactory filterFactory, Supplier<? extends IMeasurement> supplier) {

		List<IChartMenuEntry> result = new ArrayList<>();
		Collection<IMeasurementFilter<?>> filterList = filterFactory.getFilters(FilterFactory.genericClass(IMeasurementFilter.class), new BiFunction<IMeasurementFilter<?>, Map<String, ?>, Boolean>() {

			IMeasurement measurement = supplier.get();

			@Override
			public Boolean apply(IMeasurementFilter<?> t, Map<String, ?> u) {

				if(measurement != null) {
					return t.acceptsIMeasurement(measurement);
				}
				return false;
			}
		});
		for(IMeasurementFilter<?> filter : filterList) {
			result.add(new MeasurementFilterMenuEntry<>(filter, supplier));
		}
		sortEntries(result);
		return result;
	}

	public static List<IChartMenuEntry> createPeakEntries(String categoryName, FilterFactory filterFactory, Supplier<FilterList<IPeak>> supplier) {

		List<IChartMenuEntry> result = new ArrayList<>();
		Collection<IPeakFilter<?>> filterList = filterFactory.getFilters(FilterFactory.genericClass(IPeakFilter.class), new BiFunction<IPeakFilter<?>, Map<String, ?>, Boolean>() {

			private IPeak example;

			@Override
			public Boolean apply(IPeakFilter<?> t, Map<String, ?> u) {

				IPeak examplePeak = getExamplePeak();
				if(examplePeak != null) {
					return t.acceptsIPeak(examplePeak);
				}
				return false;
			}

			private IPeak getExamplePeak() {

				if(example == null) {
					FilterList<IPeak> collection = supplier.get();
					if(collection != null) {
						for(IPeak peak : collection) {
							example = peak;
							break;
						}
					}
				}
				return example;
			}
		});
		for(IPeakFilter<?> filter : filterList) {
			result.add(new IPeakFilterMenuEntry<>(categoryName, filter, supplier));
		}
		result.addAll(createScanEntries(categoryName, filterFactory, peakScans(supplier)));
		sortEntries(result);
		return result;
	}

	public static List<IChartMenuEntry> createScanEntries(String categoryName, FilterFactory filterFactory, Supplier<FilterList<IScan>> supplier) {

		List<IChartMenuEntry> result = new ArrayList<>();
		Collection<IScanFilter<?>> filterList = filterFactory.getFilters(FilterFactory.genericClass(IScanFilter.class), new BiFunction<IScanFilter<?>, Map<String, ?>, Boolean>() {

			private IScan example;

			@Override
			public Boolean apply(IScanFilter<?> t, Map<String, ?> u) {

				IScan exampleScan = getExampleScan();
				if(exampleScan != null) {
					return t.acceptsIScan(exampleScan);
				}
				return false;
			}

			private IScan getExampleScan() {

				if(example == null) {
					FilterList<IScan> filterList = supplier.get();
					if(filterList != null) {
						for(IScan scan : filterList) {
							example = scan;
							break;
						}
					}
				}
				return example;
			}
		});
		for(IScanFilter<?> filter : filterList) {
			result.add(new IScanFilterMenuEntry<>(categoryName, filter, supplier));
		}
		sortEntries(result);
		return result;
	}

	private static void sortEntries(List<IChartMenuEntry> result) {

		Collections.sort(result, new Comparator<IChartMenuEntry>() {

			@Override
			public int compare(IChartMenuEntry e1, IChartMenuEntry e2) {

				int cmp = e1.getCategory().compareToIgnoreCase(e2.getCategory());
				if(cmp != 0) {
					return cmp;
				}
				return e1.getName().compareToIgnoreCase(e2.getName());
			}
		});
	}

	private static final class IScanFilterMenuEntry<T> extends AbstractFilterChartMenuEntry<IScanFilter<T>, FilterList<IScan>> {

		public IScanFilterMenuEntry(String category, IScanFilter<T> filter, Supplier<? extends FilterList<IScan>> supplier) {
			super(category, filter, supplier);
		}

		@Override
		protected boolean isValid(IScanFilter<T> filter, FilterList<IScan> item) {

			for(IScan scan : item) {
				if(scan == null || !filter.acceptsIScan(scan)) {
					return false;
				}
			}
			return true;
		}

		@Override
		protected IProcessingResult<Boolean> filter(IScanFilter<T> filter, FilterList<IScan> item, IProgressMonitor monitor) {

			if(item.size() == 0) {
				return null;
			}
			T configuration = filter.createConfiguration(item.iterator().next());
			return filter.filterIScans(item, configuration, monitor);
		}
	}

	private static final class IPeakFilterMenuEntry<T> extends AbstractFilterChartMenuEntry<IPeakFilter<T>, FilterList<IPeak>> {

		public IPeakFilterMenuEntry(String category, IPeakFilter<T> filter, Supplier<? extends FilterList<IPeak>> supplier) {
			super(category, filter, supplier);
		}

		@Override
		protected boolean isValid(IPeakFilter<T> filter, FilterList<IPeak> item) {

			for(IPeak peak : item) {
				if(peak == null || !filter.acceptsIPeak(peak)) {
					return false;
				}
			}
			return true;
		}

		@Override
		protected IProcessingResult<Boolean> filter(IPeakFilter<T> filter, FilterList<IPeak> item, IProgressMonitor monitor) {

			if(item.size() == 0) {
				return null;
			}
			T configuration = filter.createConfiguration(item.iterator().next());
			return filter.filterIPeaks(item, configuration, monitor);
		}
	}

	private static final class MeasurementFilterMenuEntry<T> extends AbstractFilterChartMenuEntry<IMeasurementFilter<T>, IMeasurement> {

		public MeasurementFilterMenuEntry(IMeasurementFilter<T> filter, Supplier<? extends IMeasurement> supplier) {
			super(CATEGORY_MEASUREMENT, filter, supplier);
		}

		@Override
		protected boolean isValid(IMeasurementFilter<T> filter, IMeasurement item) {

			return filter.acceptsIMeasurement(item);
		}

		@Override
		protected IProcessingResult<Boolean> filter(IMeasurementFilter<T> filter, IMeasurement item, IProgressMonitor monitor) {

			T configuration = filter.createConfiguration(item);
			return filter.filterIMeasurements(FilterList.singelton(item), configuration, monitor);
		}
	}

	private static final class ChromatogramSelectionFilterMenuEntry<T> extends AbstractFilterChartMenuEntry<IChromatogramSelectionFilter<T>, IChromatogramSelection<?, ?>> {

		public ChromatogramSelectionFilterMenuEntry(IChromatogramSelectionFilter<T> filter, Supplier<? extends IChromatogramSelection<?, ?>> supplier) {
			super(CATEGORY_SELECTION, filter, supplier);
		}

		@Override
		protected boolean isValid(IChromatogramSelectionFilter<T> filter, IChromatogramSelection<?, ?> item) {

			return filter.acceptsIChromatogramSelection(item);
		}

		@Override
		protected IProcessingResult<Boolean> filter(IChromatogramSelectionFilter<T> filter, IChromatogramSelection<?, ?> item, IProgressMonitor monitor) {

			T configuration = filter.createConfiguration(item);
			return filter.filterIChromatogramSelections(FilterList.singelton(item), configuration, monitor);
		}
	}

	private static abstract class AbstractFilterChartMenuEntry<T extends Filter<?>, I> implements IChartMenuEntry {

		private String category;
		private T filter;
		private Supplier<? extends I> supplier;

		public AbstractFilterChartMenuEntry(String category, T filter, Supplier<? extends I> supplier) {
			this.category = category;
			this.filter = filter;
			this.supplier = supplier;
		}

		@Override
		public String getCategory() {

			return category;
		}

		@Override
		public String getName() {

			return filter.getFilterName();
		}

		@Override
		public void execute(Shell shell, ScrollableChart scrollableChart) {

			I item = supplier.get();
			if(item != null && isValid(filter, item)) {
				ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(shell);
				try {
					monitorDialog.run(true, true, new IRunnableWithProgress() {

						@Override
						public void run(IProgressMonitor monitor) {

							try {
								try {
									IProcessingResult<Boolean> result = filter(filter, item, monitor);
									if(result != null && !result.getMessages().isEmpty()) {
										ProcessingInfoViewSupport.updateProcessingInfo(result.toInfo(), result.hasErrorMessages());
									}
								} finally {
									monitor.done();
								}
							} catch(RuntimeException e) {
								e.printStackTrace();
								ProcessingInfo info = new ProcessingInfo();
								info.addErrorMessage(filter.getFilterName(), "Failed " + e);
								ProcessingInfoViewSupport.updateProcessingInfo(info, true);
							}
						}
					});
				} catch(InvocationTargetException e) {
					e.printStackTrace();
					logger.warn(e);
				} catch(InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
			}
		}

		@Override
		public boolean isEnabled(ScrollableChart scrollableChart) {

			I item = supplier.get();
			return item != null && isValid(filter, item);
		}

		protected abstract boolean isValid(T filter, I item);

		protected abstract IProcessingResult<Boolean> filter(T filter, I item, IProgressMonitor monitor);
	}
}
