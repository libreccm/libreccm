## Prepare Wildfly:

mvn -pl ccm-bundle-devel-wildfly-web wildfly:start wildfly:deploy-artifact@deploy-jdbc-driver wildfly:add-resource@add-datasource wildfly:shutdown

## Running

mvn -pl ccm-bundle-devel-wildfly-web -am wildfly:run
