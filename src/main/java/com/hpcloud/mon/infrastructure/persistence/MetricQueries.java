package com.hpcloud.mon.infrastructure.persistence;

import java.util.Iterator;
import java.util.Map;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;

import com.hpcloud.persistence.SqlQueries;

final class MetricQueries {
  private MetricQueries() {
  }

  static String buildJoinClauseFor(Map<String, String> dimensions) {
    StringBuilder sbJoin = null;
    if (dimensions != null) {
      sbJoin = new StringBuilder();
      for (int i = 0; i < dimensions.size(); i++)
        sbJoin.append(" inner join MonMetrics.Dimensions d")
            .append(i)
            .append(" on d")
            .append(i)
            .append(".name = :dname")
            .append(i)
            .append(" and d")
            .append(i)
            .append(".value = :dvalue")
            .append(i)
            .append(" and dd.dimension_set_id = d")
            .append(i)
            .append(".dimension_set_id");
    }

    return sbJoin == null ? "" : sbJoin.toString();
  }

  static void bindDimensionsToQuery(Query<?> query, Map<String, String> dimensions) {
    if (dimensions != null) {
      int i = 0;
      for (Iterator<Map.Entry<String, String>> it = dimensions.entrySet().iterator(); it.hasNext(); i++) {
        Map.Entry<String, String> entry = it.next();
        query.bind("dname" + i, entry.getKey());
        query.bind("dvalue" + i, entry.getValue());
      }
    }
  }

  static Map<String, String> dimensionsFor(Handle handle, byte[] dimensionSetId) {
    return SqlQueries.keyValuesFor(handle,
        "select name, value from MonMetrics.Dimensions where dimension_set_id = ?", dimensionSetId);
  }
}
