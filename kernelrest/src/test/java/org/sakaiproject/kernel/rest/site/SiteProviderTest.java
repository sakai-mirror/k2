/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.sakaiproject.kernel.rest.site;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easymock.Capture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sakaiproject.kernel.api.authz.PermissionQuery;
import org.sakaiproject.kernel.api.site.SiteException;
import org.sakaiproject.kernel.model.SiteBean;
import org.sakaiproject.kernel.rest.test.BaseRestUT;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.ws.rs.WebApplicationException;

/**
 *
 */
public class SiteProviderTest extends BaseRestUT {

  private static final Log LOG = LogFactory.getLog(SiteProviderTest.class);
  private SiteProvider siteProvider;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    setupServices();
    newSession();
    createProvider();
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testCreateNoUser() throws IOException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes(null, baos);
    replayMocks();

    try {
      siteProvider.createSite("/testsite/in/some/location", "project", "My New Site",
          "A Short description", null, null);
      fail();
    } catch (WebApplicationException ex) {
      LOG.info("OK");
    }
    verifyMocks();

  }

  @Test
  public void testCreateNonAdmin() throws IOException, SiteException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("user1", baos);
    expect(siteService.createSite("/testsite/in/some/location", "project")).andThrow(
        new SecurityException());
    replayMocks();

    try {
      siteProvider.createSite("/testsite/in/some/location", "project", "My New Site",
          "A Short description", null,null);
      fail();
    } catch (WebApplicationException ex) {
      LOG.info("OK");
    }

    verifyMocks();

  }

  @Test
  public void testCreate() throws IOException, SiteException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("admin", baos);
    SiteBean siteBean = new SiteBean();
    siteBean.service(siteService);
    siteBean.setId("testSiteId");
    expect(siteService.createSite("/testsite/in/some/location", "project")).andReturn(
        siteBean);
    Capture<String> siteId = new Capture<String>();
    Capture<String> siteUser = new Capture<String>();
    Capture<String> siteMembershipType = new Capture<String>();
    userEnvironmentResolverService.addMembership(capture(siteUser), capture(siteId),
        capture(siteMembershipType));
    expectLastCall();
    siteService.save(siteBean);
    expectLastCall();

    replayMocks();

    siteProvider.createSite("/testsite/in/some/location", "project", "My New Site",
        "A Short description", new String[] {"maintain:read", "maintain:write",
            "maintain:remove", "access:read"},"access");
    assertEquals("My New Site", siteBean.getName());
    assertEquals("A Short description", siteBean.getDescription());
    assertArrayEquals(new String[] {"admin"}, siteBean.getOwners());
    assertEquals("admin", siteUser.getValue());
    assertEquals("testSiteId", siteId.getValue());
    assertEquals("owner", siteMembershipType.getValue());
    verifyMocks();

  }

  @Test
  public void testGetSite() throws IOException, SiteException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("admin", baos);
    SiteBean siteBean = new SiteBean();
    siteBean.service(siteService);
    siteBean.setId("testSiteId");
    expect(siteService.siteExists("/testsite/in/some/location")).andReturn(true);
    expect(siteService.getSite("/testsite/in/some/location")).andReturn(siteBean);
    expect(beanConverter.convertToString(siteBean)).andReturn("OK");
    replayMocks();

    String response = siteProvider.getSite("/testsite/in/some/location");
    assertEquals("OK", response);
    verifyMocks();

  }

  @Test
  public void testGetSiteFail() throws IOException, SiteException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("admin", baos);
    SiteBean siteBean = new SiteBean();
    siteBean.service(siteService);
    siteBean.setId("testSiteId");
    expect(siteService.siteExists("/testsite/in/some/location")).andReturn(false);
    replayMocks();

    try {
      siteProvider.getSite("/testsite/in/some/location");
      fail();
    } catch (WebApplicationException e) {
      assertEquals(404, e.getResponse().getStatus());
    }
    verifyMocks();

  }

  @Test
  public void testAddOwnerNotOwner() throws IOException, SiteException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("admin", baos);
    SiteBean siteBean = new SiteBean();
    siteBean.service(siteService);
    siteBean.setId("testSiteId");
    PermissionQuery writePermission = createMock(PermissionQuery.class);
    expect(permissionQueryService.getPermission("write")).andReturn(writePermission);
    authzResolverService.check("/testsite/in/some/location/.site", writePermission);
    expectLastCall();
    expect(siteService.siteExists("/testsite/in/some/location")).andReturn(true);
    expect(siteService.getSite("/testsite/in/some/location")).andReturn(siteBean);
    replayMocks(writePermission);

    try {
      siteProvider.addOwner("/testsite/in/some/location", "ieb");
      fail();
    } catch (WebApplicationException e) {
      assertEquals(403, e.getResponse().getStatus());
    }
    verifyMocks(writePermission);

  }

  @Test
  public void testAddOwner() throws IOException, SiteException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("admin", baos);
    SiteBean siteBean = new SiteBean();
    siteBean.service(siteService);
    siteBean.setId("testSiteId");
    siteBean.setOwners(new String[] {"admin"});
    PermissionQuery writePermission = createMock(PermissionQuery.class);
    expect(permissionQueryService.getPermission("write")).andReturn(writePermission);
    authzResolverService.check("/testsite/in/some/location/.site", writePermission);
    expectLastCall();
    authzResolverService.setRequestGrant("add Owner");
    expectLastCall();
    expect(siteService.siteExists("/testsite/in/some/location")).andReturn(true);
    expect(siteService.getSite("/testsite/in/some/location")).andReturn(siteBean);
    userEnvironmentResolverService.addMembership("ieb", "testSiteId", "owner");
    expectLastCall();
    siteService.save(siteBean);
    expectLastCall();
    authzResolverService.clearRequestGrant();
    expectLastCall();
    replayMocks(writePermission);

    String resp = siteProvider.addOwner("/testsite/in/some/location", "ieb");
    assertEquals("{\"response\", \"OK\"}", resp);
    assertArrayEquals(new String[] {"admin", "ieb"}, siteBean.getOwners());

    verifyMocks(writePermission);
  }

  @Test
  public void testRemoveOwnerNotOwner() throws IOException, SiteException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("admin", baos);
    SiteBean siteBean = new SiteBean();
    siteBean.service(siteService);
    siteBean.setId("testSiteId");
    PermissionQuery writePermission = createMock(PermissionQuery.class);
    expect(permissionQueryService.getPermission("write")).andReturn(writePermission);
    expect(siteService.siteExists("/testsite/in/some/location")).andReturn(true);
    authzResolverService.check("/testsite/in/some/location/.site", writePermission);
    expectLastCall().andThrow(new SecurityException());
    replayMocks(writePermission);

    try {
      siteProvider.removeOwner("/testsite/in/some/location", "ieb");
      fail();
    } catch (SecurityException e) {
      LOG.debug("OK");
    }
    verifyMocks(writePermission);

  }

  @Test
  public void testRemoveOwner() throws IOException, SiteException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("admin", baos);
    SiteBean siteBean = new SiteBean();
    siteBean.service(siteService);
    siteBean.setId("testSiteId");
    siteBean.setOwners(new String[] {"ieb", "admin"});
    PermissionQuery writePermission = createMock(PermissionQuery.class);
    expect(permissionQueryService.getPermission("write")).andReturn(writePermission);
    authzResolverService.check("/testsite/in/some/location/.site", writePermission);
    expectLastCall();
    authzResolverService.setRequestGrant("remove Owner");
    expectLastCall();
    expect(siteService.siteExists("/testsite/in/some/location")).andReturn(true);
    expect(siteService.getSite("/testsite/in/some/location")).andReturn(siteBean);
    userEnvironmentResolverService.removeMembership("ieb", "testSiteId", "owner");
    expectLastCall();
    siteService.save(siteBean);
    expectLastCall();
    authzResolverService.clearRequestGrant();
    expectLastCall();
    replayMocks(writePermission);

    String resp = siteProvider.removeOwner("/testsite/in/some/location", "ieb");
    assertEquals("{\"response\", \"OK\"}", resp);
    assertArrayEquals(new String[] {"admin"}, siteBean.getOwners());

    verifyMocks(writePermission);
  }

  @Test
  public void testRemoveLastOwner() throws IOException, SiteException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("admin", baos);
    SiteBean siteBean = new SiteBean();
    siteBean.service(siteService);
    siteBean.setId("testSiteId");
    siteBean.setOwners(new String[] {"admin"});
    PermissionQuery writePermission = createMock(PermissionQuery.class);
    expect(permissionQueryService.getPermission("write")).andReturn(writePermission);
    authzResolverService.check("/testsite/in/some/location/.site", writePermission);
    expectLastCall();
    expect(siteService.siteExists("/testsite/in/some/location")).andReturn(true);
    expect(siteService.getSite("/testsite/in/some/location")).andReturn(siteBean);
    replayMocks(writePermission);

    try {
      siteProvider.removeOwner("/testsite/in/some/location", "ieb");
      fail();
    } catch (WebApplicationException e) {
      assertEquals(409, e.getResponse().getStatus());
    }

    verifyMocks(writePermission);
  }

  @Test
  public void testAddMemberAnon() throws IOException, SiteException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes(null, baos);
    SiteBean siteBean = new SiteBean();
    siteBean.service(siteService);
    siteBean.setId("testSiteId");
    expect(siteService.siteExists("/testsite/in/some/location")).andReturn(true);
    PermissionQuery writePermission = createMock(PermissionQuery.class);
    expect(permissionQueryService.getPermission("write")).andReturn(writePermission);
    authzResolverService.check("/testsite/in/some/location/.site", writePermission);
    expectLastCall().andThrow(new SecurityException());

    replayMocks(writePermission);

    try {
      siteProvider.addMember("/testsite/in/some/location", new String[] {"ieb", "john",
          "nico"}, new String[] {"access", "maintain", "custom"});
    } catch (SecurityException e) {
      LOG.info("OK");
    }
    verifyMocks(writePermission);

  }

  @Test
  public void testAddMember() throws IOException, SiteException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("admin", baos);
    SiteBean siteBean = new SiteBean();
    siteBean.service(siteService);
    siteBean.setId("testSiteId");
    PermissionQuery writePermission = createMock(PermissionQuery.class);
    expect(permissionQueryService.getPermission("write")).andReturn(writePermission);
    authzResolverService.check("/testsite/in/some/location/.site", writePermission);
    expectLastCall();
    authzResolverService.setRequestGrant("Adding Membership");
    expectLastCall();
    expect(siteService.siteExists("/testsite/in/some/location")).andReturn(true);
    expect(siteService.getSite("/testsite/in/some/location")).andReturn(siteBean);
    userEnvironmentResolverService.addMembership("ieb", "testSiteId", "access");
    expectLastCall();
    userEnvironmentResolverService.addMembership("john", "testSiteId", "maintain");
    expectLastCall();
    userEnvironmentResolverService.addMembership("nico", "testSiteId", "custom");
    expectLastCall();
    authzResolverService.clearRequestGrant();
    expectLastCall();
    replayMocks(writePermission);

    String resp = siteProvider.addMember("/testsite/in/some/location", new String[] {
        "ieb", "john", "nico"}, new String[] {"access", "maintain", "custom"});
    assertEquals("{\"response\", \"OK\"}", resp);
    verifyMocks(writePermission);

  }

  @Test
  public void testAddMemberBadParams() throws IOException, SiteException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("admin", baos);
    SiteBean siteBean = new SiteBean();
    siteBean.service(siteService);
    siteBean.setId("testSiteId");
    expect(siteService.siteExists("/testsite/in/some/location")).andReturn(true);
    replayMocks();

    try {
      siteProvider.addMember("/testsite/in/some/location", new String[] {"ieb"},
          new String[] {});
    } catch (WebApplicationException e) {
      assertEquals(400, e.getResponse().getStatus());
    }
    verifyMocks();

  }

  @Test
  public void testRemoveMember() throws IOException, SiteException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("admin", baos);
    SiteBean siteBean = new SiteBean();
    siteBean.service(siteService);
    siteBean.setId("testSiteId");
    PermissionQuery writePermission = createMock(PermissionQuery.class);
    expect(permissionQueryService.getPermission("write")).andReturn(writePermission);
    authzResolverService.check("/testsite/in/some/location/.site", writePermission);
    expectLastCall();
    authzResolverService.setRequestGrant("Revoking Membership");
    expectLastCall();
    expect(siteService.siteExists("/testsite/in/some/location")).andReturn(true);
    expect(siteService.getSite("/testsite/in/some/location")).andReturn(siteBean);
    userEnvironmentResolverService.removeMembership("ieb", "testSiteId", "access");
    expectLastCall();
    authzResolverService.clearRequestGrant();
    expectLastCall();
    replayMocks(writePermission);

    String resp = siteProvider.removeMember("/testsite/in/some/location",
        new String[] {"ieb"}, new String[] {"access"});
    assertEquals("{\"response\", \"OK\"}", resp);
    verifyMocks(writePermission);

  }

  @Test
  public void testRemoveMemberBadParams() throws IOException, SiteException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    setupAnyTimes("admin", baos);
    SiteBean siteBean = new SiteBean();
    siteBean.service(siteService);
    siteBean.setId("testSiteId");
    expect(siteService.siteExists("/testsite/in/some/location")).andReturn(true);
    replayMocks();

    try {
      siteProvider.removeMember("/testsite/in/some/location", new String[] {"ieb"},
          new String[] {});
    } catch (WebApplicationException e) {
      assertEquals(400, e.getResponse().getStatus());
    }
    verifyMocks();

  }

  @Test
  public void testDocumentation() throws IOException, SiteException {

    replayMocks();
    assertNotNull(siteProvider.getRestDocumentation().toHtml());
    assertNotNull(siteProvider.getRestDocumentation().toJson());
    assertNotNull(siteProvider.getRestDocumentation().toXml());
    verifyMocks();

  }

  /**
   *
   */
  private void createProvider() {
    siteProvider = new SiteProvider(registryService, siteService,
        userEnvironmentResolverService, sessionManagerService, beanConverter,
        authzResolverService, permissionQueryService);
  }

}
