package com.rapleaf.hank.ui;

import java.util.HashMap;

import junit.framework.TestCase;

import com.rapleaf.hank.config.ClientConfigurator;
import com.rapleaf.hank.coordinator.Coordinator;
import com.rapleaf.hank.coordinator.DomainConfig;
import com.rapleaf.hank.coordinator.DomainGroupConfig;
import com.rapleaf.hank.coordinator.PartDaemonAddress;
import com.rapleaf.hank.coordinator.RingConfig;
import com.rapleaf.hank.coordinator.RingGroupConfig;
import com.rapleaf.hank.coordinator.in_memory.InMemoryCoordinator;
import com.rapleaf.hank.partitioner.Murmur64Partitioner;
import com.rapleaf.hank.storage.curly.Curly;

public class StatusWebDaemonTester extends TestCase {
  public void testIt() throws Exception {
    final Coordinator coord = new InMemoryCoordinator();

    final DomainConfig d0 = coord.addDomain("domain0", 1024, Curly.Factory.class.getName(), "---", Murmur64Partitioner.class.getName(), 1);
    final DomainConfig d1 = coord.addDomain("domain1", 1024, Curly.Factory.class.getName(), "---", Murmur64Partitioner.class.getName(), 1);


    DomainGroupConfig g1 = coord.addDomainGroup("Group_1");
    g1.addDomain(d0, 0);
    g1.addDomain(d1, 1);

    g1.createNewVersion(new HashMap<String, Integer>() {{
      put(d0.getName(), 1);
      put(d1.getName(), 1);
    }});

    DomainGroupConfig g2 = coord.addDomainGroup("Group_2");
    g2.addDomain(d1, 0);

    RingGroupConfig rgAlpha = coord.addRingGroup("RG_Alpha", g1.getName());
    RingConfig r1 = rgAlpha.addRing(1);
    r1.addHost(addy("alpha-1-1"));
    r1.addHost(addy("alpha-1-2"));
    r1.addHost(addy("alpha-1-3"));
    RingConfig r2 = rgAlpha.addRing(2);
    r2.addHost(addy("alpha-2-1"));
    r2.addHost(addy("alpha-2-2"));
    r2.addHost(addy("alpha-2-3"));
    RingConfig r3 = rgAlpha.addRing(3);
    r3.addHost(addy("alpha-3-1"));
    r3.addHost(addy("alpha-3-2"));
    r3.addHost(addy("alpha-3-3"));

    RingGroupConfig rgBeta = coord.addRingGroup("RG_Beta", g1.getName());
    r1 = rgBeta.addRing(1);
    r1.addHost(addy("beta-1-1"));
    r1.addHost(addy("beta-1-2"));
    r1.addHost(addy("beta-1-3"));
    r1.addHost(addy("beta-1-4"));
    r2 = rgBeta.addRing(2);
    r2.addHost(addy("beta-2-1"));
    r2.addHost(addy("beta-2-2"));
    r2.addHost(addy("beta-2-3"));
    r2.addHost(addy("beta-2-4"));
    r3 = rgBeta.addRing(3);
    r3.addHost(addy("beta-3-1"));
    r3.addHost(addy("beta-3-2"));
    r3.addHost(addy("beta-3-3"));
    r3.addHost(addy("beta-3-4"));
    RingConfig r4 = rgBeta.addRing(4);
    r4.addHost(addy("beta-4-1"));
    r4.addHost(addy("beta-4-2"));
    r4.addHost(addy("beta-4-3"));
    r4.addHost(addy("beta-4-4"));

    RingGroupConfig rgGamma = coord.addRingGroup("RG_Gamma", g2.getName());
    r1 = rgGamma.addRing(1);
    r1.addHost(addy("gamma-1-1"));

    ClientConfigurator mockConf = new ClientConfigurator(){
      @Override
      public Coordinator getCoordinator() {
        return coord;
      }
    };
    StatusWebDaemon daemon = new StatusWebDaemon(mockConf, 12345);
    daemon.run();
  }

  private PartDaemonAddress addy(String hostname) {
    return new PartDaemonAddress(hostname, 6200);
  }
}
