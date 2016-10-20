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

public final class TimeSeries implements TimeSeriesProducer {
	private final String name;
	private final Vector<DataPoint> data = new Vector<DataPoint>();
	
	public TimeSeries(String name) {
		this.name = name;
	}
	
	public String name() {
		return this.name;
	}
	
	public int size() {
		return data.size();
	}
	
	public boolean equalsData(TimeSeries other) {
		if(data.size() != other.data.size()) return false;
		for (int i= 0; i<data.size(); i++) {
			if (!data.elementAt(i).equals(other.data.elementAt(i))) return false;
		}
		return true;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (null == obj) return false;
		if (obj.getClass() != getClass()) return false;
		TimeSeries other = (TimeSeries)obj;
		return equalsData(other);
	}
	
	public boolean compareDataWithDelta(TimeSeries other, float delta) {
		if(data.size() != other.data.size()) return false;
		float d = delta;
		if (d<0.0f) d = 0.0f;
		for (int i= 0; i<data.size(); i++) {
			DataPoint dp1 = data.elementAt(i);
			DataPoint dp2 = other.data.elementAt(i);
			if (!dp1.date().equals(dp2.date)) {
				return false;
			}
			if (Math.abs(dp1.value() - dp2.value()) > d) {
				return false;
			}
		}
		return true;
	}
	
	public DataPoint getDataPoint(int index) {
		return data.elementAt(index);
	}
	
	public void addDataPoint(DataPoint dp) throws TimeSeriesException {
		if (data.size() == 0 || dp.date.after(data.lastElement().date)) {
			data.addElement(dp);
		} else {
			insertDataPoint(dp);
		}
	}
	
	private void insertDataPoint(DataPoint dp) throws TimeSeriesException {
		for (int i = 0; i< data.size(); i++) {
			DataPoint dpi = data.elementAt(i);
			if (dpi.date.equals(dp.date)) {
				throw new TimeSeriesException("duplicate data point");
			}
			if (dpi.date.after(dp.date)) {
				data.insertElementAt(dp, i);
			}
		}
	}
	
	//TODO public -> package
	public DataPoint interpolate(int endIdx, Date dt) throws InterpolationException {
		if (endIdx < 0 || endIdx >= size()) {
			throw new InterpolationException("index out of range");
		}
		DataPoint dpEnd = data.elementAt(endIdx);
		//System.out.println(name() + " interpolate endIdx=" + endIdx + "(" + dpEnd.date + ") dt=" + dt );

		if (size() == 1) {
			DataPoint dp = data.elementAt(0);
			//System.out.println("single dp guessing dt=" + dt);
			return new DataPoint(dt, dp.value);
		}
		if (dt.equals(dpEnd.date)) {
			//System.out.println("hit dpEnd.date dt=" + dt);
			return dpEnd;
		}
		
		if (endIdx == size() - 1) {
			DataPoint dpl = data.elementAt(endIdx-1);
			if (dt.after(dpEnd.date)) {
				long datediff1 = dpEnd.date.getTime() - dpl.date.getTime();
				long datediff2 = dt.getTime() - dpEnd.date.getTime();
				float valueDiff = dpEnd.value - dpl.value;
				float value = dpEnd.value + valueDiff * ((float)datediff2 / (float)datediff1); 
				return new DataPoint(new Date(dt.getTime()),value);
			} else {
				return DataPoint.interpolate(dpl, dpEnd, dt);
			}
		} else {
			if (endIdx == 0) {
				DataPoint dpr = data.elementAt(1);
				if (dt.before(dpEnd.date)) {
					long datediff1 = dpr.date.getTime() - dpEnd.date.getTime();
					long datediff2 = dpEnd.date.getTime() - dt.getTime();
					float valueDiff = dpr.value - dpEnd.value;
					float value = dpEnd.value - valueDiff * ((float)datediff2 / (float)datediff1); 
					return new DataPoint(new Date(dt.getTime()),value);
				} else {
					return DataPoint.interpolate(dpEnd, dpr, dt);
				}
			} else {
				return DataPoint.interpolate(data.elementAt(endIdx-1), data.elementAt(endIdx), dt);
			}
		}
	}
	
	//TODO binary search
	private int findEndIndexForInterpolate(Date dt) {
		if (size() == 0) return 0;
		for (int i= 0; i<size(); i++) {
			if (data.elementAt(i).after(dt)) return i;
		}
		return size()-1;
	}
	
	public DataPoint interpolate(Date dt) {
		int endIdx = findEndIndexForInterpolate(dt);
		try {
			return interpolate(endIdx, dt);
		} catch (InterpolationException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	public TimeSeries interpolateAtSamples(String name, TimeSeries ts) {
		TimeSeries ret = new TimeSeries(name);
		for (int i = 0; i< ts.size(); i++) {
			DataPoint dp = ts.getDataPoint(i);
			try {
				ret.addDataPoint(interpolate(dp.date()));
			} catch (TimeSeriesException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	*/
	
	public TimeSeries compute(String name) {
		TimeSeries ret = new TimeSeries(name);
		for (int i = 0; i< size(); i++) {
			try {
				ret.addDataPoint(getDataPoint(i));
			} catch (TimeSeriesException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	public TimeSeries compute() {
		return this;
	}
	
	public TimeSeries result() {
		return this;
	}
	
	public TimeSeries getPeriod(Date start, Date end, String name) {
		TimeSeries ret = new TimeSeries(name);
		for (int i = 0; i< size(); i++) {
			DataPoint dp = getDataPoint(i);
			if (dp.date().equals(start) || dp.date().equals(end) ||
				(dp.date().after(start) && dp.date().before(end))) {
				try {
					ret.addDataPoint(dp);
				} catch (TimeSeriesException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("time series '" + name() + "' [" + size() + "]\n");
		for (int i = 0; i< size(); i++) {
			DataPoint dp = getDataPoint(i);
			sb.append("[" + i + "] " + dp.toString() + "\n");
		}
		return sb.toString();
		
	}
}
