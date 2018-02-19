/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */


package main.java.com.appdynamics.monitors.staticmonitor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;

public class StaticMonitor extends AManagedMonitor{

	private Logger logger;
	private Map<String, String> taskArguments;
	private Map<String, Integer> metrics;
	private String metricPath = "Custom Metrics|Static Monitor|";

	private final int REPORTED_DEFAULT = 0;

	/**
	 * Parses the properties file for the Static Monitor
	 * @param xml: The path of the xml file
	 * @throws DocumentException
	 */
	private void parseXML(String xml){
		try{
			
			
			int unnamedCnt = 1;
			metrics.clear();

			SAXReader reader = new SAXReader();
			Document document = reader.read(xml);
			Element root = document.getRootElement();
			

			for (Iterator<Element> i = root.elementIterator(); i.hasNext();) {
				Element element = (Element)i.next();

				if (element.getName().equals("metric"))	{
					Iterator<Element> j = element.elementIterator();

					String name = "unnamed";
					int value = REPORTED_DEFAULT;
					Element elt = (Element)j.next();
					
					if(elt.getName().equals("metric-name")){
						if(!(elt.getText()).equals("")) {					
							name = elt.getText();
						} else {							
							name += unnamedCnt;
							logger.error(xml + ": Empty <metric-name> tag! Naming this metric '" + name + "'");
							unnamedCnt++;
						}
					} else {
						logger.error(xml + ": First tag in a metric must be the name of the metric (<metric-name>)!");
						break;
					}

					// check that there is another tag
					if(!j.hasNext()){
						logger.error(xml + ": There have to be exactly two tags: <metric-name> and <metric-value>!" +
								" This metric will not be reported!");
						break;
					}

					elt = (Element)j.next();
					if(elt.getName().equals("metric-value")){
						if(!(elt.getText()).equals("")) {		
							try{
								value = Integer.parseInt(elt.getText());
							} catch (NumberFormatException e){
								logger.error("Attribute not a number: " + e.getMessage() + "... Reporting default value (" + REPORTED_DEFAULT + ")");
							}
						} else {
							// info, not error:
							logger.info(xml + ": Empty <metric-value> tag. Reporting default value (" + REPORTED_DEFAULT + ")");
						}
					} else {
						logger.error(xml + ": Second tag in a metric must be the value of the metric (<metric-value>)!");
						break;
					}

					// check that there are NO more tags
					if(j.hasNext()){
						logger.error(xml + ": There have to be exactly two tags: <metric-name> and <metric-value>!" +
								" This metric will not be reported!");
						break;
					}
					

					// finally, if everything was ok, metric can be recorded
					metrics.put(name, value);

				} else {
					logger.error("Unknown element in the xml file ('" + element.getName() + "')!");
				}
			}
			

		} catch (DocumentException e){
			logger.error("Unable to read " + taskArguments.get("metrics-path") + "\n" + e.getMessage());
		}
	}

	@Override
	public TaskOutput execute(Map<String, String> taskArguments,
			TaskExecutionContext taskContext) throws TaskExecutionException {
		try {

			logger = Logger.getLogger(StaticMonitor.class);	
			this.taskArguments = taskArguments;
			this.metrics = new HashMap<String, Integer>();

			// configuring custom metric path, if there is one.
			if(taskArguments.containsKey("metric-path") && !taskArguments.get("metric-path").equals("")){
				metricPath = taskArguments.get("metric-path");
				if(!metricPath.endsWith("|")){
					metricPath += "|";
				}
			}

			// working with threads to ensure a more accurate sleep time.
			while(true) {

				(new ParseAndReportThread()).start();
				Thread.sleep(60000);

			}

		} catch (InterruptedException e) {
			logger.error("Static Monitor interrupted. Quitting Monitor.");
		}
		return null;
	}


	/**
	 * Returns the metric to the AppDynamics Controller.
	 * @param 	metricValue		Value of the Metric
	 */
	private void printMetrics()
	{
		for(String metricName : metrics.keySet())
		{
			MetricWriter metricWriter = getMetricWriter(metricPath + metricName, 
					MetricWriter.METRIC_AGGREGATION_TYPE_OBSERVATION,
					MetricWriter.METRIC_TIME_ROLLUP_TYPE_CURRENT,
					MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_COLLECTIVE);

			metricWriter.printMetric(String.valueOf(metrics.get(metricName)));
		}
	}

	private class ParseAndReportThread extends Thread{
		public void run(){

			if(!taskArguments.containsKey("static-values-path")){
				logger.error("monitor.xml needs to contain a task-argument 'static-values-path' describing " +
						"the path to the metrics.xml with the static values.");
				return;
			}

			parseXML(taskArguments.get("static-values-path"));	
			printMetrics();
		}
	}


}
