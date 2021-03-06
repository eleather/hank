package com.rapleaf.hank.coordinator.in_memory;

import java.io.IOException;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.rapleaf.hank.coordinator.DomainConfig;
import com.rapleaf.hank.partitioner.Partitioner;
import com.rapleaf.hank.storage.StorageEngine;
import com.rapleaf.hank.storage.StorageEngineFactory;

public class MemDomainConfig implements DomainConfig {

  private final int numParts;
  private final String storageEngineFactoryName;
  private final String storageEngineOptions;
  private final String partitionerName;
  private int version;
  private final String name;

  public MemDomainConfig(String name,
      int numParts,
      String storageEngineFactoryName,
      String storageEngineOptions,
      String partitionerName, int initialVersion)
  {
    this.name = name;
    this.numParts = numParts;
    this.storageEngineFactoryName = storageEngineFactoryName;
    this.storageEngineOptions = storageEngineOptions;
    this.partitionerName = partitionerName;
    this.version = initialVersion;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getNumParts() {
    return numParts;
  }

  @Override
  public Partitioner getPartitioner() {
    try {
      return (Partitioner) Class.forName(partitionerName).newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public StorageEngine getStorageEngine() {
    try {
      return ((StorageEngineFactory)Class.forName(storageEngineFactoryName).newInstance()).getStorageEngine((Map<String, Object>) new Yaml().load(storageEngineOptions), name);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int getVersion() {
    return version;
  }

  @Override
  public int newVersion() throws IOException {
    return ++version;
  }

  @Override
  public String toString() {
    return "MemDomainConfig [name=" + name + ", numParts=" + numParts
        + ", partitionerName=" + partitionerName
        + ", storageEngineFactoryName=" + storageEngineFactoryName
        + ", storageEngineOptions=" + storageEngineOptions + ", version="
        + version + "]";
  }

  @Override
  public Class<? extends StorageEngineFactory> getStorageEngineFactoryClass() {
    try {
      return (Class<? extends StorageEngineFactory>) Class.forName(storageEngineFactoryName);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
