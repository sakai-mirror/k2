package org.sakaiproject.sdata.tool.functions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;

public class JCRTagging {

  private static final Log log = LogFactory.getLog(JCRTagging.class);

  /**
   * @param context
   * @param propertyName
   * @param values
   * @param start
   * @param nresults
   * @return
   */
  public List<String> getPropertyMatches(String context, String propertyName,
      String[] values, int start, int nresults) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @param context
   * @param propertyName
   * @return
   */
  public Map<String, Integer> getPropertyVector(String context,
      String propertyName) {
    // TODO Auto-generated method stub
    return null;
  }

}
