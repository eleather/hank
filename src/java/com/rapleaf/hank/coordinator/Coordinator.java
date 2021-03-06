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
package com.rapleaf.hank.coordinator;


import java.io.IOException;
import java.util.Set;

import com.rapleaf.hank.exception.DataNotFoundException;

/**
 * The Coordinator is the top-level interface for wrapping up all the
 * configuration of the entire Hank cluster and data artifacts. It's not a
 * service per se, but rather more like an interface onto whatever backing
 * system actually stores all the configuration.
 */
public interface Coordinator {
  //
  // Domains
  //

  /**
   * Add a new domain.
   * @return TODO
   */
  public DomainConfig addDomain(String domainName,
      int numParts,
      String storageEngineFactoryName,
      String storageEngineOptions,
      String partitionerName,
      int initialVersion)
  throws IOException;

  /**
   * Get the set of known DomainConfigs.
   * @return
   */
  public Set<DomainConfig> getDomainConfigs();

  /**
   * @param domainName
   * @return configuration information on the specified domain
   * @throws DataNotFoundException if no domain with the specified name exists
   */
  public DomainConfig getDomainConfig(String domainName)
  throws DataNotFoundException;

  //
  // DomainGroups
  //

  /**
   * Add a new domain group. (You will be able to add domains to the new group
   * once it is created.)
   */
  public DomainGroupConfig addDomainGroup(String name) throws IOException;

  /**
   * Get the set of known DomainGroupConfigs.
   * @return
   */
  public Set<DomainGroupConfig> getDomainGroupConfigs();

  /**
   * @param domainGroupName
   * @return configuration information on the specified domain group
   * @throws DataNotFoundException if no domain group with the specified name exists
   */
  public DomainGroupConfig getDomainGroupConfig(String domainGroupName)
  throws DataNotFoundException;

  //
  // RingGroups
  //

  /**
   * Get the set of known RingGroupConfigs.
   * @return
   */
  public Set<RingGroupConfig> getRingGroups();

  /**
   * @param ringGroupName
   * @return configuration information on the specified ring group
   * @throws DataNotFoundException
   *           if no ring group with the specified name exists
   */
  public RingGroupConfig getRingGroupConfig(String ringGroupName)
  throws DataNotFoundException;

  /**
   * Add a new ring group. Note that the domain group must exist in advance.
   * @param ringGroupName
   * @param domainGroupName
   * @return
   * @throws IOException
   */
  public RingGroupConfig addRingGroup(String ringGroupName, String domainGroupName)
  throws IOException;
}
