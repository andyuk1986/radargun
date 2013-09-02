/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.radargun.cachewrappers;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.TermTermination;
import org.infinispan.Cache;
import org.infinispan.query.CacheQuery;
import org.infinispan.query.Search;
import org.infinispan.query.SearchManager;
import org.radargun.features.Queryable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Wrapper which will be able to run queries on Infinispan caches.
 *
 * @author Anna Manukyan
 */
public class InfinispanQueryWrapper extends InfinispanXSWrapper implements Queryable, Serializable {

   @Override
   public Integer executeQuery(Map<String, Object> queryParameters) {
      Cache cache = cacheManager.getCache(getCacheName());

      SearchManager searchManager = Search.getSearchManager(cache);
      BooleanJunction queryBuilder = searchManager.buildQueryBuilderForClass(QueryableData.class).get().bool();

      Boolean isWildcardQuery = (Boolean) queryParameters.get(IS_WILDCARD);
      String onField = (String) queryParameters.get(QUERYABLE_FIELD);
      List<String> matching = (List<String>) queryParameters.get(MATCH_STRING);

      TermTermination termTermination = null;
      QueryBuilder queryBuilder1 = searchManager.buildQueryBuilderForClass(QueryableData.class).get();

      for(String matchingWord : matching) {
         if (isWildcardQuery) {
            termTermination = queryBuilder1.keyword().wildcard().onField(onField).matching(matchingWord);
         } else {
            termTermination = queryBuilder1.keyword().onField(onField).matching(matchingWord);
         }

         queryBuilder.should(termTermination.createQuery());
      }

      CacheQuery cacheQuery = searchManager.getQuery(queryBuilder.createQuery());
      int resultSize = cacheQuery.getResultSize();

      System.out.println("Result Size: " + resultSize);

      return resultSize;
   }

   public void empty() {
      //Do nothing
   }

   public void put(String bucket, Object key, Object value) throws Exception {
      QueryableData data = new QueryableData((String) value);

      super.put(getCacheName(), key, data);
   }

   public Object get(String bucket, Object key) throws Exception {
      return super.get(getCacheName(), key);
   }

   @Indexed(index = "query")
   public class QueryableData implements Serializable {

      @Field(store = Store.YES)
      private String description;

      public QueryableData(String description) {
         this.description = description;
      }

      public String getDescription() {
         return description;
      }

      public String toString() {
         return description;
      }

   }
}
