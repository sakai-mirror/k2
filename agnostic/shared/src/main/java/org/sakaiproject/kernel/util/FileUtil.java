/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
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
package org.sakaiproject.kernel.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for working on the file system.
 */
public class FileUtil {

  /**
   * @param f
   */
  public static void deleteAll(File f) {
    if (f.exists()) {
      if (f.isDirectory()) {
        for (File fc : f.listFiles()) {
          deleteAll(fc);
        }
      }
      f.delete();
    }
  }

  /**
   * @param string
   * @return
   */
  public static File[] findAll(String file, final String endsWith) {
    List<File> list = new ArrayList<File>();
    File f = new File(file);
    FileFilter ff = new FileFilter() {

      public boolean accept(File pathname) {
        if (pathname.isDirectory()) {
          return true;
        }
        return (pathname.getName().endsWith(endsWith));
      }

    };
    addFile(f, list, ff);
    // TODO Auto-generated method stub
    return list.toArray(new File[0]);
  }

  /**
   * @param f
   * @param list
   * @param ff
   */
  private static void addFile(File f, List<File> list, FileFilter ff) {
    if (f.exists()) {
      if (f.isDirectory()) {
        for (File fn : f.listFiles(ff)) {
          if (fn.isDirectory()) {
            addFile(fn, list, ff);
          } else {
            list.add(fn);
          }
        }
      } else if (ff.accept(f)) {
        list.add(f);
      }
    }
  }

}
