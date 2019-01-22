package org.libreccm;

import org.wildfly.swarm.Swarm;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CcmBundleDevel {
    
    public static void main(final String[] args) throws Exception {
        
        final Swarm swarm = new Swarm();
        
        swarm.start();
        
    }
    
}
