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

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DataPoint {
	final Date date;
	final float value;
	public static final String dateFormat = "dd.MM.yyyy HH:mm:ss.S";
	static final SimpleDateFormat dateFormater = new SimpleDateFormat(dateFormat);   

	public DataPoint(Date date, float value) {
		this.date = date;
		this.value = value;
	}

	public Date date() {
		return date;
	}

	public long getTime() {
		return date.getTime();
	}
	
	public boolean before(Date when) {
		return date.before(when);
	}
	
	public boolean after(Date when) {
		return date.after(when);
	}
	
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (null == obj) return false;
		if (obj.getClass() != getClass()) return false;
		DataPoint other = (DataPoint)obj;
		if (!date.equals(other.date)) return false;
		if(value != other.value) return false;
		return true;
	}
	
	public float value() {
		return value;
	}
	
	public String toString() {
		return dateFormater.format(date) + " : " + value;
	}
	
	public static DataPoint interpolate(DataPoint dpl, DataPoint dpr, Date dt) throws InterpolationException {
		if (dpr.date.before(dpl.date)) {
			throw new InterpolationException("interpolation dates out of order");
		}
		if (dt.before(dpl.date) || dt.after(dpr.date)) {
			throw new InterpolationException("off date interpolation");
		}
		if (dt.equals(dpl.date)) {
			return dpl;
		}
		if (dt.equals(dpr.date)) {
			return dpr;
		}
		
		long datediff1 = dpr.date.getTime() - dpl.date.getTime();
		long datediff2 = dt.getTime() - dpl.date.getTime();
		float valueDiff = dpr.value - dpl.value;
		float value = dpl.value + valueDiff * ((float)datediff2 / (float)datediff1); 
		return new DataPoint(new Date(dt.getTime()),value);
	}

}
