/**
 *  Copyright 2011 Rapleaf
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.rapleaf.hank.cli;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.rapleaf.hank.config.ClientConfigurator;
import com.rapleaf.hank.config.yaml.YamlClientConfigurator;
import com.rapleaf.hank.coordinator.Coordinator;
import com.rapleaf.hank.coordinator.DomainConfig;
import com.rapleaf.hank.coordinator.DomainConfigVersion;
import com.rapleaf.hank.coordinator.DomainGroupConfig;
import com.rapleaf.hank.coordinator.DomainGroupConfigVersion;
import com.rapleaf.hank.coordinator.HostConfig;
import com.rapleaf.hank.coordinator.HostDomainConfig;
import com.rapleaf.hank.coordinator.PartDaemonAddress;
import com.rapleaf.hank.coordinator.RingConfig;
import com.rapleaf.hank.coordinator.RingGroupConfig;

public class AddRing {

  public static void main(String[] args) throws Exception {
    Options options = new Options();
    options.addOption("r", "ring-group", true,
        "the name of the ring group");
    options.addOption("n", "ring-number", true,
        "the number of the new ring you are creating");
    options.addOption("h", "hosts", true,
        "a comma-separated list of host:port pairs");
    options.addOption("c", "config", true,
        "path of a valid config file with coordinator connection information");
    try {
      CommandLine line = new GnuParser().parse(options, args);
      ClientConfigurator configurator = new YamlClientConfigurator(line.getOptionValue("config"));
      addRing(configurator,
          line.getOptionValue("ring-group"),
          Integer.parseInt(line.getOptionValue("ring-number")),
          line.getOptionValue("hosts"));
    } catch (ParseException e) {
      new HelpFormatter().printHelp("add_domain", options);
      throw e;
    }
  }

  private static void addRing(ClientConfigurator configurator,
      String ringGroupName,
      int ringNumber,
      String hostsString)
  throws Exception {
    // create the ring
    Coordinator coord = configurator.getCoordinator();
    RingGroupConfig ringGroup = coord.getRingGroupConfig(ringGroupName);
    RingConfig newRing = ringGroup.addRing(ringNumber);

    // add all the hosts to the ring
    String[] hosts = hostsString.split(",");
    List<HostConfig> hostConfigs = new LinkedList<HostConfig>();
    for (String host : hosts) {
      PartDaemonAddress address = PartDaemonAddress.parse(host);
      hostConfigs.add(newRing.addHost(address));
    }

    // assign all the domains to the hosts
    DomainGroupConfig domainGroupConfig = ringGroup.getDomainGroupConfig();
    DomainGroupConfigVersion latestVersion = domainGroupConfig.getLatestVersion();
    int verNum = latestVersion.getVersionNumber();
    for (DomainConfigVersion domainConfigVersion : latestVersion.getDomainConfigVersions()) {
      DomainConfig domainConfig = domainConfigVersion.getDomainConfig();

      Queue<HostDomainConfig> q = new LinkedList<HostDomainConfig>();
      for (HostConfig hostConfig : hostConfigs) {
        q.add(hostConfig.addDomain(domainGroupConfig.getDomainId(domainConfig.getName())));
      }

      for (int i = 0; i < domainConfig.getNumParts(); i++) {
        HostDomainConfig hdc = q.poll();
        hdc.addPartition(i, verNum);
        q.add(hdc);
      }
    }
  }

}
