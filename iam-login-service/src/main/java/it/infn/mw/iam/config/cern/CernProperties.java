/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2019
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.infn.mw.iam.config.cern;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("cern")
public class CernProperties {

  public static class HrDbApiProperties {

    String url;
    String username;
    String password;

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }

  private String ssoIssuer = "https://auth.cern.ch/auth/realms/cern";
  
  private String personIdClaim = "cern_person_id";
  
  private String ssoEntityId = "https://cern.ch/login";

  private String experimentName;
  
  private HrDbApiProperties hrApi;

  public String getSsoEntityId() {
    return ssoEntityId;
  }

  public void setSsoEntityId(String ssoEntityId) {
    this.ssoEntityId = ssoEntityId;
  }

  public HrDbApiProperties getHrApi() {
    return hrApi;
  }

  public void setHrApi(HrDbApiProperties hrApi) {
    this.hrApi = hrApi;
  }

  public String getExperimentName() {
    return experimentName;
  }

  public void setExperimentName(String experimentName) {
    this.experimentName = experimentName;
  }
  
  public void setSsoIssuer(String ssoIssuer) {
    this.ssoIssuer = ssoIssuer;
  }
  
  public String getSsoIssuer() {
    return ssoIssuer;
  }
  
  public String getPersonIdClaim() {
    return personIdClaim;
  }
  
  public void setPersonIdClaim(String personIdClaim) {
    this.personIdClaim = personIdClaim;
  }
}
