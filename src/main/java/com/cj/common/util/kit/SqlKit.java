package com.cj.common.util.kit;

import java.util.List;

/**
 * SqlKit
 */
@SuppressWarnings("all")
public class SqlKit {

    /**
     * 将 id 列表 join 起来，用逗号分隔，并且用小括号括起来
     * 方法重载
     */
    public static void joinIds(List<Integer> idList, StringBuilder stringBuilder) {
        stringBuilder.append("(");
        boolean isFirst = true;
        for (Integer id : idList) {
            if (isFirst) {
                isFirst = false;
            } else {
                stringBuilder.append(", ");
            }
            stringBuilder.append(id.toString());
        }
        stringBuilder.append(")");
    }

    /**
     * 将 id 列表 join 起来，用逗号分隔，并且用小括号括起来
     * ids ===> ("1","2","3","4","5","6","7"...)
     *
     * @param ids
     * @return
     */
    public static String joinIds(List<String> ids) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        boolean isFirst = true;
        for (String id : ids) {
            if (isFirst) {
                isFirst = false;
            } else {
                stringBuilder.append(", ");
            }
            stringBuilder.append("'").append(id.toString()).append("'");
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}

