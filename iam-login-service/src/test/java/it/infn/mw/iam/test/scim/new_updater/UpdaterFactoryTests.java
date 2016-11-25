package it.infn.mw.iam.test.scim.new_updater;

import static it.infn.mw.iam.api.scim.new_updater.UpdaterType.ACCOUNT_ADD_OIDC_ID;
import static it.infn.mw.iam.api.scim.new_updater.UpdaterType.ACCOUNT_ADD_SAML_ID;
import static it.infn.mw.iam.api.scim.new_updater.UpdaterType.ACCOUNT_ADD_SSH_KEY;
import static it.infn.mw.iam.api.scim.new_updater.UpdaterType.ACCOUNT_ADD_X509_CERTIFICATE;
import static it.infn.mw.iam.api.scim.new_updater.UpdaterType.ACCOUNT_REMOVE_OIDC_ID;
import static it.infn.mw.iam.api.scim.new_updater.UpdaterType.ACCOUNT_REMOVE_SAML_ID;
import static it.infn.mw.iam.api.scim.new_updater.UpdaterType.ACCOUNT_REMOVE_SSH_KEY;
import static it.infn.mw.iam.api.scim.new_updater.UpdaterType.ACCOUNT_REMOVE_X509_CERTIFICATE;
import static it.infn.mw.iam.api.scim.new_updater.UpdaterType.ACCOUNT_REPLACE_ACTIVE;
import static it.infn.mw.iam.api.scim.new_updater.UpdaterType.ACCOUNT_REPLACE_EMAIL;
import static it.infn.mw.iam.api.scim.new_updater.UpdaterType.ACCOUNT_REPLACE_FAMILY_NAME;
import static it.infn.mw.iam.api.scim.new_updater.UpdaterType.ACCOUNT_REPLACE_GIVEN_NAME;
import static it.infn.mw.iam.api.scim.new_updater.UpdaterType.ACCOUNT_REPLACE_PASSWORD;
import static it.infn.mw.iam.api.scim.new_updater.UpdaterType.ACCOUNT_REPLACE_PICTURE;
import static it.infn.mw.iam.api.scim.new_updater.UpdaterType.ACCOUNT_REPLACE_USERNAME;
import static it.infn.mw.iam.test.TestUtils.x509Certs;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import it.infn.mw.iam.api.scim.converter.OidcIdConverter;
import it.infn.mw.iam.api.scim.converter.SamlIdConverter;
import it.infn.mw.iam.api.scim.converter.SshKeyConverter;
import it.infn.mw.iam.api.scim.converter.X509CertificateConverter;
import it.infn.mw.iam.api.scim.model.ScimOidcId;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation;
import it.infn.mw.iam.api.scim.model.ScimSamlId;
import it.infn.mw.iam.api.scim.model.ScimSshKey;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.model.ScimUserPatchRequest;
import it.infn.mw.iam.api.scim.model.ScimX509Certificate;
import it.infn.mw.iam.api.scim.new_updater.DefaultAccountUpdaterFactory;
import it.infn.mw.iam.api.scim.new_updater.Updater;
import it.infn.mw.iam.api.scim.new_updater.UpdaterFactory;
import it.infn.mw.iam.api.scim.new_updater.UpdaterType;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.model.IamSamlId;
import it.infn.mw.iam.persistence.model.IamSshKey;
import it.infn.mw.iam.persistence.model.IamUserInfo;
import it.infn.mw.iam.persistence.model.IamX509Certificate;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.test.util.JacksonUtils;

@RunWith(MockitoJUnitRunner.class)
public class UpdaterFactoryTests {

  public static final String OLD = "old";
  public static final String NEW = "new";

  public static final IamOidcId OLD_ID = new IamOidcId(OLD, OLD);
  public static final IamOidcId NEW_ID = new IamOidcId(NEW, NEW);

  ObjectMapper mapper = JacksonUtils.createJacksonObjectMapper();

  PasswordEncoder encoder = new BCryptPasswordEncoder();

  @Mock
  IamAccountRepository repo;

  OidcIdConverter oidcConverter = new OidcIdConverter();
  SamlIdConverter samlConverter = new SamlIdConverter();
  SshKeyConverter sshKeyConverter = new SshKeyConverter();
  X509CertificateConverter x509Converter = new X509CertificateConverter();

  @Test
  public void testGivenNamePatchOpParsing() {

    UpdaterFactory<IamAccount, ScimUser> factory = new DefaultAccountUpdaterFactory(encoder, repo,
        oidcConverter, samlConverter, sshKeyConverter, x509Converter);

    IamAccount account = new IamAccount();

    account.setUserInfo(new IamUserInfo());
    account.getUserInfo().setGivenName(OLD);

    ScimUser user = ScimUser.builder().buildName(NEW, null).build();

    ScimUserPatchRequest req = ScimUserPatchRequest.builder().add(user).build();

    ScimPatchOperation<ScimUser> op = req.getOperations().get(0);

    List<Updater> updaters = factory.getUpdatersForPatchOperation(account, op);

    assertThat(updaters.size(), equalTo(1));

    assertThat(updaters.get(0).getType(), equalTo(ACCOUNT_REPLACE_GIVEN_NAME));
    assertThat(updaters.get(0).update(), equalTo(true));
    assertThat(account.getUserInfo().getGivenName(), equalTo(NEW));

  }

  @Test
  public void testPicturePatchOpParsing() {

    UpdaterFactory<IamAccount, ScimUser> factory = new DefaultAccountUpdaterFactory(encoder, repo,
        oidcConverter, samlConverter, sshKeyConverter, x509Converter);

    IamAccount account = new IamAccount();

    account.setUserInfo(new IamUserInfo());
    account.getUserInfo().setPicture(OLD);

    ScimUser user = ScimUser.builder().buildPhoto(NEW).build();

    ScimUserPatchRequest req = ScimUserPatchRequest.builder().replace(user).build();

    ScimPatchOperation<ScimUser> op = req.getOperations().get(0);

    List<Updater> updaters = factory.getUpdatersForPatchOperation(account, op);

    assertThat(updaters.size(), equalTo(1));

    assertThat(updaters.get(0).getType(), equalTo(ACCOUNT_REPLACE_PICTURE));
    assertThat(updaters.get(0).update(), equalTo(true));
    assertThat(account.getUserInfo().getPicture(), equalTo(NEW));

  }

  @Test
  public void testSshKeyPatchAddOpParsing() {

    UpdaterFactory<IamAccount, ScimUser> factory = new DefaultAccountUpdaterFactory(encoder, repo,
        oidcConverter, samlConverter, sshKeyConverter, x509Converter);

    IamAccount account = new IamAccount();

    ScimUser user = ScimUser.builder().addSshKey(ScimSshKey.builder().value(NEW).build()).build();

    ScimUserPatchRequest req = ScimUserPatchRequest.builder().add(user).build();

    ScimPatchOperation<ScimUser> op = req.getOperations().get(0);

    Mockito.when(repo.findBySshKeyValue(NEW)).thenReturn(Optional.empty());

    List<Updater> updaters = factory.getUpdatersForPatchOperation(account, op);

    assertThat(updaters.size(), equalTo(1));

    assertThat(updaters.get(0).getType(), equalTo(ACCOUNT_ADD_SSH_KEY));
    assertThat(updaters.get(0).update(), equalTo(true));
    assertThat(account.getSshKeys().get(0), equalTo(new IamSshKey(NEW)));

  }

  @Test
  public void testPatchAddOpMultipleParsing() {

    UpdaterFactory<IamAccount, ScimUser> factory = new DefaultAccountUpdaterFactory(encoder, repo,
        oidcConverter, samlConverter, sshKeyConverter, x509Converter);

    List<UpdaterType> expectedUpdatersType =
        Lists.newArrayList(ACCOUNT_REPLACE_GIVEN_NAME, ACCOUNT_REPLACE_FAMILY_NAME,
            ACCOUNT_REPLACE_EMAIL, ACCOUNT_REPLACE_PASSWORD, ACCOUNT_REPLACE_USERNAME,
            ACCOUNT_REPLACE_ACTIVE, ACCOUNT_REPLACE_PICTURE, ACCOUNT_ADD_OIDC_ID,
            ACCOUNT_ADD_SAML_ID, ACCOUNT_ADD_SSH_KEY, ACCOUNT_ADD_X509_CERTIFICATE);

    IamAccount account = new IamAccount();

    account.setUsername(OLD);
    account.setActive(false);
    account.setUserInfo(new IamUserInfo());
    account.getUserInfo().setGivenName(OLD);
    account.getUserInfo().setFamilyName(OLD);
    account.getUserInfo().setPicture(OLD);
    account.setPassword(OLD);
    account.getUserInfo().setEmail(OLD);

    Mockito.when(repo.findByUsername(NEW)).thenReturn(Optional.empty());
    Mockito.when(repo.findByEmail(NEW)).thenReturn(Optional.empty());
    Mockito.when(repo.findByOidcId(NEW, NEW)).thenReturn(Optional.empty());
    Mockito.when(repo.findBySamlId(NEW, NEW)).thenReturn(Optional.empty());
    Mockito.when(repo.findBySshKeyValue(NEW)).thenReturn(Optional.empty());
    Mockito.when(repo.findByCertificate(x509Certs.get(0).certificate)).thenReturn(Optional.empty());

    ScimUser user = ScimUser.builder()
      .buildName(NEW, NEW)
      .userName(NEW)
      .active(true)
      .buildPhoto(NEW)
      .buildEmail(NEW)
      .password(NEW)
      .addOidcId(ScimOidcId.builder().issuer(NEW).subject(NEW).build())
      .addSamlId(ScimSamlId.builder().idpId(NEW).userId(NEW).build())
      .addSshKey(ScimSshKey.builder().value(NEW).build())
      .addX509Certificate(ScimX509Certificate.builder().value(x509Certs.get(0).certificate).build())
      .build();

    ScimUserPatchRequest req = ScimUserPatchRequest.builder().add(user).build();

    ScimPatchOperation<ScimUser> op = req.getOperations().get(0);

    List<Updater> updaters = factory.getUpdatersForPatchOperation(account, op);

    assertThat(updaters.size(), equalTo(expectedUpdatersType.size()));

    updaters.forEach(u -> assertThat(u.getType(), isIn(expectedUpdatersType)));
    updaters.forEach(u -> assertThat(u.update(), equalTo(true)));
    updaters.forEach(u -> assertThat(u.update(), equalTo(false)));

    assertThat(account.getUsername(), equalTo(NEW));
    assertThat(account.isActive(), equalTo(true));
    assertThat(account.getUserInfo().getGivenName(), equalTo(NEW));
    assertThat(account.getUserInfo().getFamilyName(), equalTo(NEW));
    assertThat(account.getUserInfo().getPicture(), equalTo(NEW));
    assertThat(account.getUserInfo().getEmail(), equalTo(NEW));
    assertThat(encoder.matches(NEW, account.getPassword()), equalTo(true));
    assertThat(account.getOidcIds().get(0), equalTo(new IamOidcId(NEW, NEW)));
    assertThat(account.getSamlIds().get(0), equalTo(new IamSamlId(NEW, NEW)));
    assertThat(account.getSshKeys().get(0), equalTo(new IamSshKey(NEW)));
    assertThat(account.getX509Certificates().get(0),
        equalTo(new IamX509Certificate(x509Certs.get(0).certificate)));

  }

  @Test
  public void testPatchReplaceOpMultipleParsing() {

    UpdaterFactory<IamAccount, ScimUser> factory = new DefaultAccountUpdaterFactory(encoder, repo,
        oidcConverter, samlConverter, sshKeyConverter, x509Converter);

    List<UpdaterType> expectedUpdatersType = Lists.newArrayList(ACCOUNT_REPLACE_GIVEN_NAME,
        ACCOUNT_REPLACE_FAMILY_NAME, ACCOUNT_REPLACE_EMAIL, ACCOUNT_REPLACE_USERNAME,
        ACCOUNT_REPLACE_ACTIVE, ACCOUNT_REPLACE_PASSWORD, ACCOUNT_REPLACE_PICTURE);

    IamAccount account = new IamAccount();

    account.setUsername(OLD);
    account.setActive(false);
    account.setUserInfo(new IamUserInfo());
    account.getUserInfo().setGivenName(OLD);
    account.getUserInfo().setFamilyName(OLD);
    account.getUserInfo().setPicture(OLD);
    account.setPassword(OLD);
    account.getUserInfo().setEmail(OLD);

    Mockito.when(repo.findByUsername(NEW)).thenReturn(Optional.empty());
    Mockito.when(repo.findByEmail(NEW)).thenReturn(Optional.empty());

    ScimUser user = ScimUser.builder()
      .userName(NEW)
      .active(true)
      .buildName(NEW, NEW)
      .buildPhoto(NEW)
      .buildEmail(NEW)
      .password(NEW)
      .build();

    ScimUserPatchRequest req = ScimUserPatchRequest.builder().replace(user).build();

    ScimPatchOperation<ScimUser> op = req.getOperations().get(0);

    List<Updater> updaters = factory.getUpdatersForPatchOperation(account, op);

    assertThat(updaters.size(), equalTo(expectedUpdatersType.size()));

    updaters.forEach(u -> assertThat(u.getType(), isIn(expectedUpdatersType)));
    updaters.forEach(u -> assertThat(u.update(), equalTo(true)));
    updaters.forEach(u -> assertThat(u.update(), equalTo(false)));

    assertThat(account.getUsername(), equalTo(NEW));
    assertThat(account.isActive(), equalTo(true));
    assertThat(account.getUserInfo().getGivenName(), equalTo(NEW));
    assertThat(account.getUserInfo().getFamilyName(), equalTo(NEW));
    assertThat(account.getUserInfo().getPicture(), equalTo(NEW));
    assertThat(account.getUserInfo().getEmail(), equalTo(NEW));
    assertThat(encoder.matches(NEW, account.getPassword()), equalTo(true));
  }

  @Test
  public void testPatchRemoveOpMultipleParsing() {

    UpdaterFactory<IamAccount, ScimUser> factory = new DefaultAccountUpdaterFactory(encoder, repo,
        oidcConverter, samlConverter, sshKeyConverter, x509Converter);

    List<UpdaterType> expectedUpdatersType = Lists.newArrayList(ACCOUNT_REMOVE_OIDC_ID,
        ACCOUNT_REMOVE_SAML_ID, ACCOUNT_REMOVE_SSH_KEY, ACCOUNT_REMOVE_X509_CERTIFICATE);

    IamAccount account = new IamAccount();
    account.setOidcIds(Lists.newArrayList(new IamOidcId(OLD, OLD)));
    account.setSamlIds(Lists.newArrayList(new IamSamlId(OLD, OLD)));
    account.setSshKeys(Lists.newArrayList(new IamSshKey(OLD)));
    account.setX509Certificates(
        Lists.newArrayList(new IamX509Certificate(x509Certs.get(0).certificate)));

    Mockito.when(repo.findByOidcId(OLD, OLD)).thenReturn(Optional.of(account));
    Mockito.when(repo.findBySamlId(OLD, OLD)).thenReturn(Optional.of(account));
    Mockito.when(repo.findBySshKeyValue(OLD)).thenReturn(Optional.of(account));
    Mockito.when(repo.findByCertificate(x509Certs.get(0).certificate))
      .thenReturn(Optional.of(account));

    ScimUser user = ScimUser.builder()
      .addOidcId(ScimOidcId.builder().issuer(OLD).subject(OLD).build())
      .addSamlId(ScimSamlId.builder().idpId(OLD).userId(OLD).build())
      .addSshKey(ScimSshKey.builder().value(OLD).build())
      .addX509Certificate(ScimX509Certificate.builder().value(x509Certs.get(0).certificate).build())
      .build();

    ScimUserPatchRequest req = ScimUserPatchRequest.builder().remove(user).build();

    ScimPatchOperation<ScimUser> op = req.getOperations().get(0);

    List<Updater> updaters = factory.getUpdatersForPatchOperation(account, op);

    assertThat(updaters.size(), equalTo(expectedUpdatersType.size()));

    updaters.forEach(u -> assertThat(u.getType(), isIn(expectedUpdatersType)));
    updaters.forEach(u -> assertThat(u.update(), equalTo(true)));
    updaters.forEach(u -> assertThat(u.update(), equalTo(false)));

    assertThat(account.getOidcIds().isEmpty(), equalTo(true));
    assertThat(account.getSamlIds().isEmpty(), equalTo(true));
    assertThat(account.getSshKeys().isEmpty(), equalTo(true));
    assertThat(account.getX509Certificates().isEmpty(), equalTo(true));

  }
}
