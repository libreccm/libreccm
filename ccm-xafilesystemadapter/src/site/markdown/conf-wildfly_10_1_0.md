XAFileSystem configuration for Wildfly
======================================

The following instructions are for Wildfly 10.1.0.FINAL but should also work on 
earlier or later versions.

1. Download XADisk from [xadisk.java.net]: [http://java.net/projects/xadisk/downloads/download/XADisk%201.2.2%20Everything.zip]

2. Extract the ZIP file

3. Deploy the `XADisk.rar` file found in `binaries` directory of the extracted
   ZIP file to your Wildfly installation, either by using the administration UI
   of Wildfly or by copying the file to the `deployments` directory of your
   Wildfly installation.

4.  Create a new resource adapter, either by using the Wildfly administration UI
    or the editing the configuration file of your Wildfly installation.
    
    Configuration of the resource adapter via the Wildfly Administration Console:
    
    ![Wildfly Configuration Attributes](resources/wildfly_conf_attributes.png)

    ![Wildfly Configuration Properties](resources/wildfly_conf_properties.png)

    ![Wildfly Connection Definition Attributes](resources/wildfly_connectiondef_attributes.png)

    ![Wildfly Connection Definition Properties](resources/wildfly_connectiondef_properties.png)

    Or if you prefer to edit the XML file add the following configuration in
    the subsystem section for resource adapters:

    <subsystem xmlns="urn:jboss:domain:resource-adapters:4.0">
        <resource-adapters>
            <resource-adapter id="Disk">
                <archive>
                    XADisk.rar
                </archive>
                <transaction-support>XATransaction</transaction-support>
                <config-property name="instanceId">
                    xadisk1
                </config-property>
                <config-property name="xaDiskHome">
                    /path/to/xahome
                </config-property>
                <connection-definitions>
                    <connection-definition class-name="org.xadisk.connector.outbound.XADiskManagedConnectionFactory" jndi-name="java:/xadiskcf" pool-name="XADiskConnectionFactoryPool">
                        <config-property name="instanceId">
                            xadisk1
                        </config-property>
                        <xa-pool>
                            <min-pool-size>1</min-pool-size>
                            <max-pool-size>5</max-pool-size>
                        </xa-pool>
                    </connection-definition>
                </connection-definitions>
            </resource-adapter>
        </resource-adapters>
    </subsystem>

    The name of the instance id can be choosen freely, as the id of the resource
    adapter. The `xaDiskHome` property must point to an existing directory. This
    directory is used by XADisk to store various data. The property does *not* 
    limit the paths XADisk can access.

5. Configure the directory where LibreCCM should store its data by setting
   the `dataPath` property of the `CcmFilesConfiguration`. 
