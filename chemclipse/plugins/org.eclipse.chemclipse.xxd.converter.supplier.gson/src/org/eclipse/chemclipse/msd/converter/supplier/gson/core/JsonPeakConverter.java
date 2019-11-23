package org.eclipse.chemclipse.msd.converter.supplier.gson.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.converter.peak.IPeakExportConverter;
import org.eclipse.chemclipse.msd.converter.peak.IPeakImportConverter;
import org.eclipse.chemclipse.msd.converter.supplier.gson.model.JsonPeak;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

/**
 * Read/writes {@link IPeaks} in JSON-Format
 *
 */
public class JsonPeakConverter implements IPeakExportConverter, IPeakImportConverter {

	private static final String WRITER_ID_V1 = JsonPeakConverter.class.getName() + "#v1";
	private static final String KEY_NAME = "name";
	private static final String KEY_PEAKS = "peaks";
	private static final String KEY_WRITER = "id";

	public JsonPeakConverter() {
	}

	@Override
	public IProcessingInfo<?> convert(File file, IPeaks<? extends IPeakMSD> peaks, boolean append, IProgressMonitor monitor) {

		ProcessingInfo<?> info = new ProcessingInfo<>();
		try {
			try (FileOutputStream outputStream = new FileOutputStream(file)) {
				writePeaks(peaks, outputStream, monitor);
			}
		} catch(IOException e) {
			info.addErrorMessage("JSONPeakConverter", "Writing to file failed", e);
			file.delete();
		}
		return info;
	}

	@Override
	public IProcessingInfo<IPeaks<?>> convert(File file, IProgressMonitor monitor) {

		ProcessingInfo<IPeaks<?>> info = new ProcessingInfo<>();
		try {
			try (FileInputStream stream = new FileInputStream(file)) {
				info.setProcessingResult(readPeaks(stream, monitor));
			}
		} catch(IOException e) {
			info.addErrorMessage("JSONPeakConverter", "Writing to file failed", e);
		}
		return info;
	}

	/**
	 * Reads a stream that was previously written by this converter, this is for example useful in unit-tests where you don't want to fire-up a whole framework just to use the converters
	 * 
	 * @param inputStream
	 * @param monitor
	 * @return
	 * @throws IOException
	 */
	public static IPeaks<IPeakMSD> readPeaks(InputStream inputStream, IProgressMonitor monitor) throws IOException {

		JsonObject root;
		try (InputStreamReader reader = new InputStreamReader(new GZIPInputStream(inputStream), StandardCharsets.UTF_8)) {
			root = new JsonParser().parse(reader).getAsJsonObject();
		}
		if(!WRITER_ID_V1.equals(root.get(KEY_WRITER).getAsString())) {
			// does not seem to be valid format for us to read
			return null;
		}
		final String name = root.get(KEY_NAME).getAsString();
		final List<IPeakMSD> result = new ArrayList<>();
		JsonArray peakArray = root.get(KEY_PEAKS).getAsJsonArray();
		SubMonitor subMonitor = SubMonitor.convert(monitor, peakArray.size());
		for(final JsonElement element : peakArray) {
			result.add(JsonPeak.readPeak(element));
			subMonitor.worked(1);
		}
		return new IPeaks<IPeakMSD>() {

			@Override
			public String getName() {

				return name;
			}

			@Override
			public List<IPeakMSD> getPeaks() {

				return Collections.unmodifiableList(result);
			}
		};
	}

	/**
	 * Write peaks as if they where written by this converter, this must normally not be called from client code as one can always trigger this from the UI. In some case it might still be useful e.g. to make a deep copy of an unknown list, but care must be taken as this converter does not necessarily writes every attribute of a peak
	 * 
	 * @param peaks
	 * @param outputStream
	 * @throws IOException
	 */
	public static void writePeaks(IPeaks<? extends IPeakMSD> data, OutputStream outputStream, IProgressMonitor monitor) throws IOException {

		try (JsonWriter json = new JsonWriter(new OutputStreamWriter(new GZIPOutputStream(outputStream), StandardCharsets.UTF_8))) {
			json.setIndent("    ");
			json.beginObject();
			json.name(KEY_WRITER);
			json.value(WRITER_ID_V1);
			json.name(KEY_NAME);
			json.value(data.getName());
			json.name(KEY_PEAKS);
			json.beginArray();
			List<? extends IPeakMSD> peaks = data.getPeaks();
			SubMonitor subMonitor = SubMonitor.convert(monitor, peaks.size());
			for(final IPeakMSD peak : peaks) {
				JsonPeak.writePeak(json, peak);
				subMonitor.worked(1);
			}
			json.endArray();
			json.endObject();
		}
	}
}
