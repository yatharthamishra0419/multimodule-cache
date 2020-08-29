package com.multimodule.cache.redis;/*
 author : nitin.goyal
*/

public class RedisProperties {

  String endPoint;
  String password;
  int maxTotalConnections;
  int minIdleConnections;
  int defaultTimeoutInMillis;
  int maxwaitmillis;
  String masterName;
  String user;

  public String getEndPoint() {
    return endPoint;
  }

  public void setEndPoint(String endPoint) {
    this.endPoint = endPoint;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public int getMaxTotalConnections() {
    return maxTotalConnections;
  }

  public void setMaxTotalConnections(int maxTotalConnections) {
    this.maxTotalConnections = maxTotalConnections;
  }

  public int getMinIdleConnections() {
    return minIdleConnections;
  }

  public void setMinIdleConnections(int minIdleConnections) {
    this.minIdleConnections = minIdleConnections;
  }

  public int getDefaultTimeoutInMillis() {
    return defaultTimeoutInMillis;
  }

  public void setDefaultTimeoutInMillis(int defaultTimeoutInMillis) {
    this.defaultTimeoutInMillis = defaultTimeoutInMillis;
  }

  public int getMaxwaitmillis() {
    return maxwaitmillis;
  }

  public void setMaxwaitmillis(int maxwaitmillis) {
    this.maxwaitmillis = maxwaitmillis;
  }

  public String getMasterName() {
    return masterName;
  }

  public void setMasterName(String masterName) {
    this.masterName = masterName;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  @Override
  public String toString() {
    return "CacheProperties{" +
        "endPoint='" + endPoint + '\'' +
        ", password='" + password + '\'' +
        ", maxTotalConnections=" + maxTotalConnections +
        ", minIdleConnections=" + minIdleConnections +
        ", defaultTimeoutInMillis=" + defaultTimeoutInMillis +
        ", maxwaitmillis=" + maxwaitmillis +
        ", masterName='" + masterName + '\'' +
        '}';
  }
}
