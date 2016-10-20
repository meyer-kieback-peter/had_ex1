/**
 * BaaS heating anomaly detection example
 * 
 * copyright Kieback&Peter GmbH & Co KG (2016) http://www.kieback-peter.de/
 * 
 * Sie können diese Datei unter folgenden Bedingungen weiterverwenden:
 * 
 * Die Datei wurde unter der Lizenz
 * „Creative Commons Namensnennung-Weitergabe unter gleichen Bedingungen Deutschland“
 * in Version 3.0 (abgekürzt „CC-by-sa 3.0/de“) veröffentlicht.
 * 
 * Den rechtsverbindlichen Lizenzvertrag finden Sie unter http://creativecommons.org/licenses/by-sa/3.0/de/legalcode.
 * 
 * Es folgt eine vereinfachte Zusammenfassung des Vertrags in allgemeinverständlicher Sprache ohne juristische Wirkung.
 * 
 * Es ist Ihnen gestattet,
 * - das Werk zu vervielfältigen, zu verbreiten und öffentlich zugänglich zu machen sowie
 * - Abwandlungen und Bearbeitungen des Werkes anzufertigen,
 * 
 * sofern Sie folgende Bedingungen einhalten:
 * Namensnennung: Sie müssen den Urheber bzw. den Rechteinhaber in der von ihm festgelegten Weise, die URI (z. B. die Internetadresse dieser Seite) sowie den Titel des Werkes und bei einer Abwandlung einen Hinweis darauf angeben.
 *
 * Weitergabe unter gleichen Bedingungen: Wenn Sie das lizenzierte Werk bearbeiten, abwandeln oder als Vorlage für ein neues Werk verwenden, dürfen Sie die neu entstandenen Werke nur unter dieser oder einer zu dieser kompatiblen Lizenz nutzen und weiterverbreiten.
 * 
 * Lizenzangabe: Sie müssen anderen alle Lizenzbedingungen mitteilen, die für dieses Werk gelten. Am einfachsten ist es, wenn Sie dazu einen Link auf den Lizenzvertrag (siehe oben) einbinden.
 *
 * Bitte beachten Sie, dass andere Rechte die Weiterverwendung einschränken können.
 * 
 */

package kup.timeseries;

import java.util.Date;
import java.util.Vector;

public final class CrossSample {
	private final Vector<TimeSeries> tsin;
	private final String namePostfix;
	private final int[] pos;
	private final Vector<TimeSeries> result;
	private Date nextDate = null;
	
	public CrossSample(Vector<TimeSeries> tsin, String namePostfix) {
		this.tsin = tsin;
		this.namePostfix = namePostfix;
		pos = new int[tsin.size()];
		result = new Vector<TimeSeries>(tsin.size());
	}
	
	private Date nextDataPoint() {
		Date ret = null;
		for (int i = 0; i < tsin.size(); i++) {
			TimeSeries ts = tsin.elementAt(i);
			if (pos[i] >= 0) {
				DataPoint dp = ts.getDataPoint(pos[i]);
				//System.out.println("nextDataPointInTs() " + ts.name() + " [" + pos[i] + "] dt=" + dp.date);
				if (null == ret) {
					ret = dp.date;
				}
				if (dp.before(ret)) {
					ret = dp.date;
				}
			}
		}
		return ret;
	}
	
	private void getNextDataPoint() {
		nextDate = nextDataPoint();
		if (nextDate != null) {
			//System.out.println("next data point at " + nextDate);
		} else {
			//System.out.println("no next data point in ts");
			nextDate = null;
		}
	}
	
	private void prepareCompute() throws CrossSampleException {
		for (int i = 0; i < tsin.size(); i++) {
			TimeSeries ts = tsin.elementAt(i);
			pos[i] = 0;
			if (ts.size() == 0) {
				throw new CrossSampleException("illegal cross sample of empty time series");
			}
			TimeSeries tso = new TimeSeries(ts.name() + namePostfix);
			result.add(tso);
		}
		getNextDataPoint();
	}
	
	public Vector<TimeSeries> compute() throws CrossSampleException {
		prepareCompute();
		while (nextDate != null) {
			for (int i = 0; i < tsin.size(); i++) {
				TimeSeries tsi = tsin.elementAt(i);
				TimeSeries tso = result.elementAt(i);
				try {
					if (pos[i] >= 0) {
						tso.addDataPoint(tsi.interpolate(pos[i], nextDate));
					} else {
						tso.addDataPoint(tsi.interpolate(tsi.size()-1, nextDate));
					}
				} catch (TimeSeriesException | InterpolationException e) {
					e.printStackTrace();
				}
				if (pos[i] >= 0) {
					if (tsi.getDataPoint(pos[i]).date.equals(nextDate)) {
						if (tsi.size() > pos[i] + 1) {
							//System.out.println("next data point in " + tsi.name() + " " + tsi.getDataPoint(pos[i]+1).date);
							pos[i]++;
						} else {
							//System.out.println("no next data point in " + tsi.name());
							pos[i] = -1;
						}
					}
				}
			}
			getNextDataPoint();
		}
		
		return result;
	}
}
