# AppDynamics Static Monitoring Extension

##Use Case

Once per minute, this monitoring extension reads metrics from the metrics.xml file and reports them to the AppDynamics Controller.

For example, you can create a metric named "maintenance mode" and put a value of 0 if the node (where the corresponding Machine Agent is installed) is not in maintenance mode, and a value of 1 if it is in maintenance mode.

At any time you can make manual (or automated) updates -- such as changes in the value or name of a metric -- to the metrics.xml file. It will take up to one minute for the Static Monitoring Extension to report these updates to the Controller.

This extension also gives you the opportunity to create policy alerting based on AND or OR clauses.

For example, if the traffic of a node falls below a certain threshold AND this node is not in maintenance mode, you can have an AppDynamics policy trigger an action.


##Installation

1. Run 'ant package' from the static-monitoring-extension directory
2. Deploy the file StaticMonitor.zip located in the 'dist' directory into \<machineagent install dir\>/monitors/
3. Unzip the deployed file
4. In \<machineagent install dir\>/monitors/StaticMonitor/ open metrics.xml and enter data for at least one metric.
5. Optional but recommended. Open monitor.xml and configure a custom metric path (see monitor.xml for instructions)
6. Restart the Machine Agent.

Look for the metrics in the AppDynamics Metric Browser at: Application Infrastructure
    Performance | \<Tier\> | Custom Metrics | Static Monitor | \<Metric
    name\> or at: Application Infrastructure Performance | \<Tier\> | \<your configured name (optional)\>


##Directory Structure

<table><tbody>
<tr>
<th align="left"> Directory/File </th>
<th align="left"> Description </th>
</tr>
<tr>
<td align="left"> conf </td>
<td align="left"> Contains the monitor.xml and metrics.xml files </td>
</tr>
<tr>
<td align="left"> lib </td>
<td align="left"> Contains third-party project references </td>
</tr>
<tr>
<td align="left"> src </td>
<td align="left"> Contains source code to the Static monitoring extension </td>
</tr>
<tr>
<td align="left"> dist </td>
<td align="left"> Only obtained when using ant. Run 'ant build' to get binaries. Run 'ant package' to get the distributable .zip file </td>
</tr>
<tr>
<td align="left"> build.xml </td>
<td align="left"> Ant build script to package the project (required only if changing Java code) </td>
</tr>
</tbody>
</table>

XML files:

-   monitor.xml: This is used to execute the Java code which will start
    the monitoring extension. You might need to configure the path to metrics.xml
    (explained in Installation Steps)
-   metrics.xml: Define your own custom metrics here, and update this
    file at any time. The metrics will be reported to the Controller
    once per minute.
    

**Note**: Main Java File: **src/main/java/com/appdynamics/monitors/staticmonitor/StaticMonitor.java**  -> This file contains the metric parsing and printing.


##XML Example

###metrics.xml

|**Parameter** | **Description**|
| ------------- |:-------------|
|\<metric-name\>|The name of the metric |
| \<metric-value\>|An integer value of the metric. This metric is reported to the Controller once per minute.|

~~~~

<flag-monitor>
    <!-- You can have more than one metric if you wish -->
    <metric>
        <!-- Enter the name of your metric -->
        <metric-name>Machine ABC Maintenance Mode</metric-name>

        <!-- Enter an integer. This value is reported to the controller once 
             per minute (default is 0) -->
        <metric-value>1</metric-value>
    </metric>
    <metric>
        <!-- Enter the name of your metric -->
        <metric-name>Machine XYZ Maintenance Mode</metric-name>

        <!-- Enter an integer. This value is reported to the controller once 
             per minute (default is 0) -->
        <metric-value>0</metric-value>
    </metric>
</flag-monitor>
~~~~


##Contributing

Always feel free to fork and contribute any changes directly via [GitHub](https://github.com/Appdynamics/static-monitoring-extension).

##Community

Find out more in the [AppSphere](http://appsphere.appdynamics.com/t5/Extensions/Static-Monitoring-Extension/idi-p/1103) community.

##Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto:ace-request@appdynamics.com).
