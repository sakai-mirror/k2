/**
 * TestDataPreload.java - created by Sakai App Builder -AZ
 */

package org.sakaiproject.entitybroker.impl.data;

import org.sakaiproject.entitybroker.dao.EntityProperty;
import org.sakaiproject.entitybroker.dao.EntityTagApplication;
import org.sakaiproject.entitybroker.mocks.data.TestData;
import org.sakaiproject.genericdao.api.GenericDao;

/**
 * Contains test data for preloading and test constants
 * 
 * @author Aaron Zeckoski (aaronz@vt.edu)
 */
public class TestDataPreload {

   public GenericDao dao;

   public void setDao(GenericDao dao) {
      this.dao = dao;
   }

   public void init() {
      preloadTestData(dao);
   }

   // testing data objects here
   public EntityProperty prop1 = new EntityProperty(TestData.REF5, TestData.PREFIX5,
         TestData.PROPERTY_NAME5A, TestData.PROPERTY_VALUE5A);
   public EntityProperty prop1B = new EntityProperty(TestData.REF5, TestData.PREFIX5,
         TestData.PROPERTY_NAME5B, TestData.PROPERTY_VALUE5B);
   public EntityProperty prop1C = new EntityProperty(TestData.REF5_2, TestData.PREFIX5,
         TestData.PROPERTY_NAME5C, TestData.PROPERTY_VALUE5C);

   public EntityTagApplication tag1_aaronz = new EntityTagApplication(TestData.REFT1, TestData.PREFIXT1, "test");
   public EntityTagApplication tag1_test = new EntityTagApplication(TestData.REFT1, TestData.PREFIXT1, "AZ");
   public EntityTagApplication tag2_test = new EntityTagApplication(TestData.REFT1_2, TestData.PREFIXT1, "AZ");
   // no tags on the third one

   public boolean preloaded = false;

   /**
    * Preload a bunch of test data into the database
    * 
    * @param dao
    */
   public void preloadTestData(GenericDao dao) {
      dao.save(prop1);
      dao.save(prop1B);
      dao.save(prop1C);

      dao.save(tag1_aaronz);
      dao.save(tag1_test);
      dao.save(tag2_test);

      preloaded = true;
   }

}