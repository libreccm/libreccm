            -----------------------
            The XAFileSystemAdapter
            -----------------------
                 Jens Pelzetter
            -----------------------
                  2017-03-27
            -----------------------

This (Maven) module provides an implementation of the {@link FileSystemAdapter} 
which uses [XADisk](http://xadisk.java.net) to provides a transaction safe 
access to the file system. 

# Including into a CCM Bundle

Simply add the `ccm-xafilesystemadapter` as a dependency in the bundle. The
`CCMFiles` class in ccm-core will automatically find and use the adapter.

# Configuration

The XADisk file system adapter uses a JCA resource adapter which needs to be
installed and configured into the application server. Pleae refer to the
installation guides for your application server for more information.

 
