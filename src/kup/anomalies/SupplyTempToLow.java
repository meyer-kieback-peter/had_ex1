/**
 * BaaS heating anomaly detection example
 * 
 * copyright Kieback&Peter GmbH & Co KG (2016) http://www.kieback-peter.de/
 * 
 * Sie k�nnen diese Datei unter folgenden Bedingungen weiterverwenden:
 * 
 * Die Datei wurde unter der Lizenz
 * �Creative Commons Namensnennung-Weitergabe unter gleichen Bedingungen Deutschland�
 * in Version 3.0 (abgek�rzt �CC-by-sa 3.0/de�) ver�ffentlicht.
 * 
 * Den rechtsverbindlichen Lizenzvertrag finden Sie unter http://creativecommons.org/licenses/by-sa/3.0/de/legalcode.
 * 
 * Es folgt eine vereinfachte Zusammenfassung des Vertrags in allgemeinverst�ndlicher Sprache ohne juristische Wirkung.
 * 
 * Es ist Ihnen gestattet,
 * - das Werk zu vervielf�ltigen, zu verbreiten und �ffentlich zug�nglich zu machen sowie
 * - Abwandlungen und Bearbeitungen des Werkes anzufertigen,
 * 
 * sofern Sie folgende Bedingungen einhalten:
 * Namensnennung: Sie m�ssen den Urheber bzw. den Rechteinhaber in der von ihm festgelegten Weise, die URI (z. B. die Internetadresse dieser Seite) sowie den Titel des Werkes und bei einer Abwandlung einen Hinweis darauf angeben.
 *
 * Weitergabe unter gleichen Bedingungen: Wenn Sie das lizenzierte Werk bearbeiten, abwandeln oder als Vorlage f�r ein neues Werk verwenden, d�rfen Sie die neu entstandenen Werke nur unter dieser oder einer zu dieser kompatiblen Lizenz nutzen und weiterverbreiten.
 * 
 * Lizenzangabe: Sie m�ssen anderen alle Lizenzbedingungen mitteilen, die f�r dieses Werk gelten. Am einfachsten ist es, wenn Sie dazu einen Link auf den Lizenzvertrag (siehe oben) einbinden.
 *
 * Bitte beachten Sie, dass andere Rechte die Weiterverwendung einschr�nken k�nnen.
 * 
 */

package kup.anomalies;

import kup.timeseries.CrossSampleException;
import kup.timeseries.TimeSeries;
import kup.timeseries.function.Abs;
import kup.timeseries.function.Clip;
import kup.timeseries.function.GreaterOrEqual;
import kup.timeseries.function.K;
import kup.timeseries.function.Sub;
import kup.timeseries.function.T1;
import kup.timeseries.function.Taper;

/**
 * HVAC anomaly: supply temperature way below setpoint - energy deficit situation.
 * 
 * This anomaly is detected, if the supply temperature does'nt exceed
 * supply temperature setpoint * (1.0 - limit) 
 *
 * parameters:
 * tSup - supply temperature
 * tsupSetpt - supply temperature setpoint
 * t1 - low-pass filter coefficient for inputs, useful default is 1800s
 * limit - detection limit for deficit situation 0 .. 1 (0 .. 100% ), useful default is 0.15 (15% below setpoint) 
 */
public class SupplyTempToLow {
	
	public static TimeSeries compute(TimeSeries tSup, TimeSeries tSupSetpt, float t1, float limit) throws CrossSampleException {
		if (t1 <= Float.MIN_NORMAL) t1 = Float.MIN_NORMAL;
		if (limit <= 0.0f) limit = 0.0f;
		if (limit >= 1.0f) limit = 1.0f;
		
		T1 tSupT1 = new T1(tSup, t1);
		T1 tSupSetptT1 = new T1(tSupSetpt, t1);
		Sub deviation = new Sub(tSupT1.compute(), tSupSetptT1.compute());
		Clip clippedDeviation = new Clip(deviation.compute(), Float.NEGATIVE_INFINITY, 0.0f);
		Abs absClippedDeviation = new Abs(clippedDeviation.compute());
		K maxDeviation = new K(tSupSetptT1.compute(), limit);
		GreaterOrEqual anomalyUntapered = new GreaterOrEqual(absClippedDeviation.compute(), maxDeviation.compute());
		Taper anomaly = new Taper(anomalyUntapered.compute(), 0.5f);
		return anomaly.compute();
	}
}
