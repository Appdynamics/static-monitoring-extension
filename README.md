# AppDynamics Flag Monitoring Extension

##Use Case

Once per minute, this monitoring extension reads metrics from the metrics.xml file and reports
them to the AppDynamics Controller.

For example, you can create a metric named "maintenance mode" and put a
value of 0 if the node (where the corresponding Machine Agent is
installed) is not in maintenance mode, and a value of 1 if it is in
maintenance mode.

At any time you can make manual (or automated) updates -- such as
changes in the value or name of a metric -- to the metrics.xml file. It
will take up to one minute for the Flag monitoring extension to report these updates to
the Controller.

This extension also gives you the opportunity to create policy alerting based on
AND or OR clauses.

For example, if the traffic of a node falls below a certain threshold
AND this node is not in maintenance mode, you can have an AppDynamics policy trigger an
action.


##Files

<table><tbody>
<tr>
<th align = 'left'> Directory/File </th>
<th align = 'left'> Description </th>
</tr>
<tr>
<td align = 'left'> bin </td>
<td align = 'left'> Contains class files </td>
</tr>
<tr>
<td align = 'left'> conf </td>
<td align = 'left'> Contains the monitor.xml and metrics.xml files </td>
</tr>
<tr>
<td align = 'left'> lib </td>
<td align = 'left'> Contains third-party project references </td>
</tr>
<tr>
<td align = 'left'> src </td>
<td align = 'left'> Contains source code to the Flag monitoring extension </td>
</tr>
<tr>
<td align = 'left'> dist </td>
<td align = 'left'> Contains the distribution package (monitor.xml, metrics.xml, the lib
directory, and flagmonitor.jar) </td>
</tr>
<tr>
<td align = 'left'> build.xml </td>
<td align = 'left'> Ant build script to package the project (required only if changing Java code) </td>
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
    

![](images/emoticons/information.gif) Main Java File: **src/com/appdynamics/monitors/flagmonitor/FlagMonitor.java**  -> This file contains the metric parsing and printing.


##Installation

1. In the \<machine-agent-home\>/monitors/ directory, create a new folder for the Flag monitor extension. (Suggested folder name is FlagMonitor.)
2. Copy the contents in the 'dist' folder to the folder created in step 1.
3. If the directory you created in step 1 is not named FlagMonitor, open monitor.xml and configure the path to the metrics.xml file.
4. Edit metrics.xml and enter data for at least one metric.
3. Restart the Machine Agent.
4. Look for the metrics in the AppDynamics Metric Browser at: Application Infrastructure
    Performance | \<Tier\> | Custom Metrics | Flag Monitor | \<Metric
    name\>

##XML Examples

###monitor.xml

|**Parameter** | **Description**|
| ------------- |:-------------|
| metrics-path|Location of the metrics.xml file |


~~~~

<monitor>
        <name>FlagMonitor</name>
        <type>managed</type>
        <description>Flag monitor. Reports metrics in metrics.xml to controller once per minute</description>
        <monitor-configuration></monitor-configuration>
        <monitor-run-task>
                <execution-style>continuous</execution-style>
                <name>Flag Monitor Run Task</name>
                <display-name>Flag Monitor Task</display-name>
                <description>Flag Monitor Task</description>
                <type>java</type>
                <java-task>
                        <classpath>flagmonitor.jar;lib/dom4j-2.0.0-ALPHA-2.jar</classpath>
                        <impl-class>com.appdynamics.monitors.flagmonitor.FlagMonitor</impl-class>
                </java-task>
                <!-- CONFIGURE IF NECESSARY:
                    this is the path to the file metrics.xml file. 
                    If you created a directory in 'monitors' named other than 'FlagMonitor', 
                    change the field  'default-value' to the appropriate directory.
                -->
                <task-arguments>
                        <argument name="metrics-path" is-required="true" default-value="monitors/FlagMonitor/metrics.xml"/>
                </task-arguments>
        </monitor-run-task>
</monitor>
~~~~

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
        <metric-name> </metric-name>

        <!-- Enter an integer. This value is reported to the controller once 
             per minute (default is 0) -->
        <metric-value> </metric-value>
    </metric>
</flag-monitor>
~~~~

##Contributing

Always feel free to fork and contribute any changes directly via GitHub.


##Support

For any support questions, please contact ace@appdynamics.com.
